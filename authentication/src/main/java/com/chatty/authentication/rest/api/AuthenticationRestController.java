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
import com.chatty.util.errors.logic.ErrorCode;
import com.chatty.util.exceptions.token.TokenNotFoundException;
import com.chatty.util.exceptions.token.TokenNotValidException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationRestController {
    private final AuthenticationService authService;
    private final TokenService tokenService;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        authService.register(request);
        return ResponseEntity.ok(new MessageResponse("User successfully created"));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(
            @Valid @RequestBody AuthenticationRequest request
    ){
        User user = authService.authenticate(request);
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

        if (!tokenService.isTokenValid(refreshToken)) {
            throw TokenNotValidException.builder()
                    .errorCode(ErrorCode.TOKEN_NOT_VALID)
                    .errorDate(LocalDateTime.now())
                    .dataCausedError(refreshToken)
                    .errorMessage("Token not valid")
                    .build();
        }
        String id = tokenService.extractSubject(refreshToken);
        User user = userService.findById(id);
        tokenService.delete(refreshToken);

        ResponseCookie cookie = tokenService.generateRefreshTokenCookie(user);
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new AccessTokenResponse(tokenService.generateAccessToken(user)));
    }

    @GetMapping("/validateToken")
    public Boolean validateToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken) {
        String accessToken = tokenService.getToken(bearerToken);
        return tokenService.isTokenValid(accessToken);
    }
}
