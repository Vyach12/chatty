package com.chatty.authentication.rest.api;

import com.chatty.authentication.models.Token;
import com.chatty.authentication.services.AuthenticationService;
import com.chatty.authentication.services.TokenService;
import com.chatty.authentication.util.dto.authentication.AccessTokenResponse;
import com.chatty.authentication.util.dto.authentication.AuthenticationRequest;
import com.chatty.authentication.util.dto.authentication.MessageResponse;
import com.chatty.authentication.util.dto.authentication.RegisterRequest;
import com.chatty.authentication.util.dto.errors.logic.ErrorCode;
import com.chatty.authentication.util.dto.user.UserDTO;
import com.chatty.authentication.util.exceptions.token.TokenNotFoundException;
import com.chatty.authentication.util.exceptions.user.UserNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin
@Slf4j
public class AuthenticationRestController {
    private final AuthenticationService authService;
    private final TokenService tokenService;
    private final WebClient.Builder webClientBuilder;


    @PostMapping("register")
    public ResponseEntity<?> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        UserDTO user = authService.register(request);
        ResponseCookie cookie = tokenService.generateRefreshTokenCookie(user);
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new AccessTokenResponse(tokenService.generateAccessToken(user)));
    }

    @PostMapping("authenticate")
    public ResponseEntity<?> authenticate(
            @Valid @RequestBody AuthenticationRequest request
    ){
        UserDTO user = authService.authenticate(request);
        ResponseCookie cookie = tokenService.generateRefreshTokenCookie(user);
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new AccessTokenResponse(tokenService.generateAccessToken(user)));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @CookieValue("refresh_token") String refreshToken
    ) {
        tokenService.delete(refreshToken);
        ResponseCookie cookie = tokenService.getCleanJwtCookie();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new MessageResponse("You've been signed out!"));
    }

    @PostMapping("/refresh-token")
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

        String username = tokenService.extractUsername(refreshToken);
        UserDTO user = webClientBuilder.build().get()
                .uri("http://user-management-service/api/v1/users/{username}", username)
                .retrieve()
                .bodyToMono(UserDTO.class)
                .block();
        if(user == null) {
            throw UserNotFoundException.builder()
                    .errorCode(ErrorCode.USER_NOT_FOUND)
                    .dataCausedError(username)
                    .errorMessage("User with username " + username + " doess not exist")
                    .dataCausedError(LocalDateTime.now())
                    .build();
        }

        if (tokenService.isTokenValid(refreshToken, user)) {
            tokenService.delete(refreshToken);
            ResponseCookie cookie = tokenService.generateRefreshTokenCookie(user);
            if(cookie != null) {
                return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                        .body(new AccessTokenResponse(tokenService.generateAccessToken(user)));
            }

        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}