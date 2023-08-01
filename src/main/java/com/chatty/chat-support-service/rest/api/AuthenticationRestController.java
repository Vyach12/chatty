package ru.gusarov.messenger.rest.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.gusarov.messenger.models.Token;
import ru.gusarov.messenger.models.User;
import ru.gusarov.messenger.services.AuthenticationService;
import ru.gusarov.messenger.services.TokenService;
import ru.gusarov.messenger.services.UserService;
import ru.gusarov.messenger.util.dto.authentication.AccessTokenResponse;
import ru.gusarov.messenger.util.dto.authentication.AuthenticationRequest;
import ru.gusarov.messenger.util.dto.authentication.MessageResponse;
import ru.gusarov.messenger.util.dto.authentication.RegisterRequest;
import ru.gusarov.messenger.util.dto.errors.logic.ErrorCode;
import ru.gusarov.messenger.util.exceptions.token.TokenNotFoundException;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationRestController {
    private final AuthenticationService authService;
    private final TokenService tokenService;

    private final UserService userService;

    @PostMapping("register")
    public ResponseEntity<?> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        User user = authService.register(request);
        ResponseCookie cookie = tokenService.generateRefreshTokenCookie(user);

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(
                        new AccessTokenResponse(tokenService.generateAccessToken(user))
                );
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
        User user = userService.findByUsername(username);

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
