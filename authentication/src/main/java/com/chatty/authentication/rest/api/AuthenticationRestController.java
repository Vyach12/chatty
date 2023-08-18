package com.chatty.authentication.rest.api;

import com.chatty.amqp.RabbitMQMessageProducer;
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
import org.springframework.beans.factory.annotation.Value;
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
    private final RabbitMQMessageProducer messageProducer;

    @Value("${rabbitmq.exchanges.internal}")
    private String rabbitExchange;

    @Value("${rabbitmq.routing-keys.chat}")
    private String rabbitChatRoutingKey;

    @Value("${rabbitmq.routing-keys.user}")
    private String rabbitUserRoutingKey;

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        User user = authService.register(request);

        var userCreationForUserServiceRequest = UserCreationForUserServiceRequest.builder()
                .id(user.getId().toString())
                .username(request.getUsername())
                .email(request.getEmail())
                .dateOfBirth(request.getDateOfBirth())
                .build();

        messageProducer.publish(userCreationForUserServiceRequest, rabbitExchange, rabbitUserRoutingKey);

        var userCreationForChatServiceRequest = UserCreationForChatServiceRequest.builder()
                .id(user.getId().toString())
                .username(request.getUsername())
                .build();

        messageProducer.publish(userCreationForChatServiceRequest, rabbitExchange, rabbitChatRoutingKey);

        ResponseCookie cookie = tokenService.generateRefreshTokenCookie(user);
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new AccessTokenResponse(tokenService.generateAccessToken(user)));
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
