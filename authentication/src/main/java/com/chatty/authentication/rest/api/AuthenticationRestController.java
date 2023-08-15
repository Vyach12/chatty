package com.chatty.authentication.rest.api;

import com.chatty.authentication.models.Token;
import com.chatty.authentication.models.User;
import com.chatty.authentication.services.AuthenticationService;
import com.chatty.authentication.services.TokenService;
import com.chatty.authentication.services.UserService;
import com.chatty.authentication.dto.AccessTokenResponse;
import com.chatty.authentication.dto.AuthenticationRequest;
import com.chatty.util.dto.MessageResponse;
import com.chatty.authentication.dto.RegisterRequest;
import com.chatty.util.dto.UserCreationForUserServiceRequest;
import com.chatty.util.dto.UserCreationForChatServiceRequest;
import com.chatty.util.errors.logic.ErrorCode;
import com.chatty.util.exceptions.token.TokenNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationRestController {
    private final AuthenticationService authService;
    private final WebClient.Builder webClientBuilder;
    private final TokenService tokenService;
    private final UserService userService;

    @PostMapping("register")
    public ResponseEntity<?> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        User user = authService.register(request);

        var userInfo = UserCreationForUserServiceRequest.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .dateOfBirth(request.getDateOfBirth())
                .build();

        String accessToken = tokenService.generateAccessToken(user);

        webClientBuilder.build().post()
                .uri("http://user-management-service/api/v1/users/new")
                .header("Authorization", "Bearer " + accessToken)
                .bodyValue(userInfo)
                .retrieve()
                .bodyToMono(Void.class)
                .block();

        var userRequest = UserCreationForChatServiceRequest.builder()
                .username(request.getUsername())
                .build();

        webClientBuilder.build().post()
                .uri("http://chat-support-service/api/v1/chats/users/new")
                .header("Authorization", "Bearer " + accessToken)
                .bodyValue(userRequest)
                .retrieve()
                .bodyToMono(Void.class)
                .block();

        ResponseCookie cookie = tokenService.generateRefreshTokenCookie(user);
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new AccessTokenResponse(accessToken));
    }

    @PostMapping("authenticate")
    public ResponseEntity<?> authenticate(
            @Valid @RequestBody AuthenticationRequest request
    ){
        User user = authService.authenticate(request);
        ResponseCookie cookie = tokenService.generateRefreshTokenCookie(user);
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new AccessTokenResponse(tokenService.generateAccessToken(user)));
    }

    @PostMapping("logout")
    public ResponseEntity<?> logout(
            @CookieValue("refresh_token") String refreshToken
    ) {
        tokenService.delete(refreshToken);
        ResponseCookie cookie = tokenService.getCleanJwtCookie();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new MessageResponse("You've been signed out!"));
    }

    @PostMapping("refresh-token")
    public ResponseEntity<?> refresh(
            @CookieValue("refresh_token") String refreshToken
    ) {
        Optional<Token> storedToken = tokenService.findByToken(refreshToken);

        if(storedToken.isEmpty()) {
            throw TokenNotFoundException.builder()
                    .errorCode(ErrorCode.TOKEN_NOT_FOUND)
                    .errorDate(LocalDateTime.now())
                    .dataCausedError(refreshToken)
                    .errorMessage("Token not found")
                    .build();
        }

        String id = tokenService.extractSubject(refreshToken);
        User user = userService.findById(id);

        if (tokenService.isTokenValid(refreshToken)) {
            tokenService.delete(refreshToken);
            ResponseCookie cookie = tokenService.generateRefreshTokenCookie(user);
            if(cookie != null) {
                return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                        .body(new AccessTokenResponse(tokenService.generateAccessToken(user)));
            }

        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/validateToken")
    public Boolean validateToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken) {
        if(bearerToken.startsWith("Bearer ")) {
            bearerToken = bearerToken.substring(7);
            return tokenService.isTokenValid(bearerToken);
        }
        return false;
    }
}
