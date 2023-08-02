package com.chatty.authentication.services;

import com.chatty.authentication.models.User;
import com.chatty.authentication.util.dto.authentication.AuthenticationRequest;
import com.chatty.authentication.util.dto.authentication.RegisterRequest;
import com.chatty.authentication.util.dto.errors.logic.ErrorCode;
import com.chatty.authentication.util.dto.user.UserClaims;
import com.chatty.authentication.util.dto.user.UserDTO;
import com.chatty.authentication.util.exceptions.authentication.InvalidUsernamePasswordException;
import com.chatty.authentication.util.exceptions.user.EmailOccupiedException;
import com.chatty.authentication.util.exceptions.user.UsernameOccupiedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final UserService userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    public User register(RegisterRequest request) {
        log.info("sent request for getting user from register");
        if (userService.existByUsername(request.getUsername())) {
            throw UsernameOccupiedException.builder()
                    .errorCode(ErrorCode.USERNAME_IS_OCCUPIED)
                    .errorDate(LocalDateTime.now())
                    .dataCausedError(request)
                    .errorMessage("User with username " + request.getUsername() + " already exist")
                    .build();
        }

        if (userService.existByEmail(request.getEmail())) {
            throw EmailOccupiedException.builder()
                    .errorCode(ErrorCode.EMAIL_IS_OCCUPIED)
                    .errorDate(LocalDateTime.now())
                    .dataCausedError(request)
                    .errorMessage("User with email " + request.getEmail() + " already exist")
                    .build();
        }

        var user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .isEnabled(true)
                .role(roleService.findByName("ROLE_USER"))
                .build();

        userService.save(user);

        //Метод для отправки данных на создание чела в Сервис пользователей
        return user;
    }

    public User authenticate(AuthenticationRequest request) {
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

        return userService.findByUsername(request.getUsername());
    }
}
