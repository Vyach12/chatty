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
public class AuthenticationService {
    @Value("${rabbitmq.exchange}")
    private String rabbitExchange;
    @Value("${rabbitmq.routing-key.chat-support.user-creation-chat-support}")
    private String rabbitChatRoutingKey;
    @Value("${rabbitmq.routing-key.user-management.user-creation-user-management}")
    private String rabbitUserRoutingKey;

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final RabbitMQMessageProducer messageProducer;

    public void register(RegisterRequest request) {
        if (userService.existByUsername(request.username())) {
            throw UsernameOccupiedException.builder()
                    .errorCode(ErrorCode.USERNAME_IS_OCCUPIED)
                    .errorDate(LocalDateTime.now())
                    .dataCausedError(request)
                    .errorMessage("User with username " + request.username() + " already exist")
                    .build();
        }

        if (userService.existByEmail(request.email())) {
            throw EmailOccupiedException.builder()
                    .errorCode(ErrorCode.EMAIL_IS_OCCUPIED)
                    .errorDate(LocalDateTime.now())
                    .dataCausedError(request)
                    .errorMessage("User with email " + request.email() + " already exist")
                    .build();
        }

        var user = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .email(request.email())
                .isEnabled(true)
                .role(roleService.findByName("ROLE_USER"))
                .build();
        userService.save(user);

        var userCreationForUserServiceRequest = UserCreationForUserServiceRequest.builder()
                .id(user.getId().toString())
                .username(request.username())
                .email(request.email())
                .dateOfBirth(request.dateOfBirth())
                .build();
        messageProducer.publish(userCreationForUserServiceRequest, rabbitExchange, rabbitUserRoutingKey);

        var userCreationForChatServiceRequest = UserCreationForChatServiceRequest.builder()
                .id(user.getId().toString())
                .username(request.username())
                .build();
        messageProducer.publish(userCreationForChatServiceRequest, rabbitExchange, rabbitChatRoutingKey);
    }

    public User authenticate(AuthenticationRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.username(),
                            request.password()
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

        return userService.findByUsername(request.username());
    }
}
