package com.chatty.authentication.rest.api;

import com.chatty.authentication.models.Token;
import com.chatty.authentication.models.User;
import com.chatty.authentication.services.AuthenticationService;
import com.chatty.authentication.services.TokenService;
import com.chatty.authentication.services.UserService;
import com.chatty.authentication.util.dto.authentication.AccessTokenResponse;
import com.chatty.authentication.util.dto.authentication.AuthenticationRequest;
import com.chatty.authentication.util.dto.authentication.MessageResponse;
import com.chatty.authentication.util.dto.authentication.RegisterRequest;
import com.chatty.authentication.util.dto.errors.logic.ErrorCode;
import com.chatty.authentication.util.dto.user.UserInfo;
import com.chatty.authentication.util.exceptions.token.TokenNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final WebClient.Builder webClientBuilder;
    private final TokenService tokenService;
    private final UserService userService;

    @PostMapping("register")
    public ResponseEntity<?> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        User user = authService.register(request); //Сместить сохранение пользователя в конец базы данных

        var userInfo = UserInfo.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .dateOfBirth(request.getDateOfBirth())
                .build();

        ResponseCookie cookie = tokenService.generateRefreshTokenCookie(user);
        String accessToken = tokenService.generateAccessToken(user);

        log.info("Send request to create user");
        String x = webClientBuilder.build().post()
                .uri("http://user-management-service/api/v1/users/new")
                .header("Authorization", "Bearer " + accessToken)
                .bodyValue(userInfo)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        log.info(x);
        log.info("suuuuuuiiii");

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

    @PostMapping("refresh-token") //переделать не используя поиск юзера в бд
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
        log.info(bearerToken);
        if(bearerToken.startsWith("Bearer ")) {
            bearerToken = bearerToken.substring(7);
            log.info(bearerToken);
            return tokenService.isTokenValid(bearerToken);
        }
        return false;
    }
}
