package com.chatty.authentication.services;

import com.chatty.authentication.util.dto.authentication.AuthenticationRequest;
import com.chatty.authentication.util.dto.authentication.RegisterRequest;
import com.chatty.authentication.util.dto.errors.logic.ErrorCode;
import com.chatty.authentication.util.dto.user.UserDTO;
import com.chatty.authentication.util.exceptions.authentication.InvalidUsernamePasswordException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final WebClient.Builder webClientBuilder;

    public UserDTO register(RegisterRequest request) {
        log.info("sent request for getting user from register");
        return webClientBuilder.build().post()
                .uri("http://user-management-service/api/v1/users/createUser")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), RegisterRequest.class)
                .retrieve()
                .bodyToMono(UserDTO.class)
                .block();
    }

    public UserDTO authenticate(AuthenticationRequest request) {
        log.info("sent request for getting user from authenticate");
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
        } catch (AuthenticationException e) {
            throw InvalidUsernamePasswordException.builder()
                    .errorCode(ErrorCode.INVALID_USERNAME_PASSWORD)
                    .errorDate(LocalDateTime.now())
                    .dataCausedError(request)
                    .errorMessage("Invalid username or password")
                    .build();
        }

        return webClientBuilder.build().get()
                .uri("http://user-management-service/api/v1/users/{username}", request.getUsername())
                .retrieve()
                .bodyToMono(UserDTO.class)
                .block();
    }
}
