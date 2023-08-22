package com.chatty.usermanagement.services;

import com.chatty.amqp.RabbitMQMessageProducer;
import com.chatty.usermanagement.models.User;
import com.chatty.usermanagement.repositories.UserRepository;
import com.chatty.util.dto.NewUsernameRequest;
import com.chatty.util.dto.UserCreationForUserServiceRequest;
import com.chatty.util.errors.logic.ErrorCode;
import com.chatty.util.exceptions.user.IdOccupiedException;
import com.chatty.util.exceptions.user.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class UserService {

    @Value("${rabbitmq.routing-key.username-change}")
    private String usernameChangeRoutingKey;
    @Value("${rabbitmq.exchange}")
    private String exchange;

    private final UserRepository userRepository;
    private final RabbitMQMessageProducer messageProducer;

    public User findUserById(String id) {
        return userRepository.findUserById(id)
                .orElseThrow(() -> UserNotFoundException.builder()
                        .errorCode(ErrorCode.USER_NOT_FOUND)
                        .errorDate(LocalDateTime.now())
                        .dataCausedError(id)
                        .errorMessage("User with id = " + id + " does not exist")
                        .build()
                );
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public void createUser(UserCreationForUserServiceRequest request) {
        String id = request.id();
        if(existById(id)){
            throw IdOccupiedException.builder()
                    .errorCode(ErrorCode.ID_IS_OCCUPIED)
                    .errorDate(LocalDateTime.now())
                    .dataCausedError(id)
                    .errorMessage("User with id = " + id + " already exists")
                    .build();
        }
        User user = User.builder()
                .id(id)
                .username(request.username())
                .email(request.email())
                .dateOfBirth(request.dateOfBirth())
                .build();
        save(user);
    }

    private boolean existById(String id) {
        return userRepository.existsUserById(id);
    }

    private void save(User user) {
        userRepository.save(user);
    }

    public void changeUsername(User user, String username) {
        user.setUsername(username);
        save(user);

        var request = NewUsernameRequest.builder()
                .id(user.getId())
                .username(username)
                .build();

        messageProducer.publish(request, exchange, usernameChangeRoutingKey);
    }
}
