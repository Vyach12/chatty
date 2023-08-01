package com.chatty.authentication.services;

import com.chatty.authentication.util.dto.authentication.AuthenticationRequest;
import com.chatty.authentication.util.dto.authentication.RegisterRequest;
import com.chatty.authentication.util.dto.errors.logic.ErrorCode;
import com.chatty.authentication.util.dto.user.UserDTO;
import com.chatty.authentication.util.exceptions.authentication.InvalidUsernamePasswordException;
import com.chatty.authentication.util.exceptions.user.EmailOccupiedException;
import com.chatty.authentication.util.exceptions.user.UserIsNotEnabledException;
import com.chatty.authentication.util.exceptions.user.UserNotFoundException;
import com.chatty.authentication.util.exceptions.user.UsernameOccupiedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final WebClient.Builder webClientBuilder;

    public UserDTO register(RegisterRequest request) {
        log.info("sent request for getting user from register");
        log.info(request.getUsername() + " " + request.getPassword());
        return webClientBuilder.build().post()
                .uri("http://user-management-service/api/v1/users/createUser")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), RegisterRequest.class)
                .retrieve()
                .bodyToMono(UserDTO.class)
                .block();
    }

    /*public User authenticate(AuthenticationRequest request) {
        if (!userService.existByUsername(request.getUsername())) {
            throw UserNotFoundException.builder()
                    .errorCode(ErrorCode.USER_NOT_FOUND)
                    .errorDate(LocalDateTime.now())
                    .dataCausedError(request)
                    .errorMessage("User with username " + request.getUsername() + " does not exist")
                    .build();
        }
        if(!userService.isEnabled(request.getUsername())) {
            throw UserIsNotEnabledException.builder()
                    .errorCode(ErrorCode.USER_IS_NOT_ENABLED)
                    .errorDate(LocalDateTime.now())
                    .dataCausedError(request)
                    .errorMessage("User with username " + request.getUsername() + " is banned")
                    .build();
        }
        User user;
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
            user = userService.findByUsername(request.getUsername());
        } catch (AuthenticationException e) {
            throw InvalidUsernamePasswordException.builder()
                    .errorCode(ErrorCode.INVALID_USERNAME_PASSWORD)
                    .errorDate(LocalDateTime.now())
                    .dataCausedError(request)
                    .errorMessage("Invalid username or password")
                    .build();
        }
        return user;
    }*/
}
