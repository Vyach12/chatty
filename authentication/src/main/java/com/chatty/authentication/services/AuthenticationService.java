package com.chatty.authentication.services;

import com.chatty.amqp.RabbitMQMessageProducer;
import com.chatty.authentication.models.User;
import com.chatty.authentication.dto.AuthenticationRequest;
import com.chatty.authentication.dto.RegisterRequest;
import com.chatty.util.dto.UserCreationForChatServiceRequest;
import com.chatty.util.dto.UserCreationForUserServiceRequest;
import com.chatty.util.errors.logic.ErrorCode;
import com.chatty.util.exceptions.authentication.InvalidUsernamePasswordException;
import com.chatty.util.exceptions.user.EmailOccupiedException;
import com.chatty.util.exceptions.user.UsernameOccupiedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    @Value("${rabbitmq.exchanges.internal}")
    private String rabbitExchange;

    @Value("${rabbitmq.routing-keys.chat}")
    private String rabbitChatRoutingKey;

    @Value("${rabbitmq.routing-keys.user}")
    private String rabbitUserRoutingKey;

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final RabbitMQMessageProducer messageProducer;

    public void register(RegisterRequest request) {
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
