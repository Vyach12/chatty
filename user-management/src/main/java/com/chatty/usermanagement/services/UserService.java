package com.chatty.usermanagement.services;

import com.chatty.usermanagement.models.User;
import com.chatty.usermanagement.repositories.UserRepository;
import com.chatty.util.dto.UserCreationForUserServiceRequest;
import com.chatty.util.errors.logic.ErrorCode;
import com.chatty.util.exceptions.user.IdOccupiedException;
import com.chatty.util.exceptions.user.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User findById(String id) {
        return userRepository.findByUsername(id)
                .orElseThrow(() -> UserNotFoundException.builder()
                        .errorCode(ErrorCode.USER_NOT_FOUND)
                        .errorDate(LocalDateTime.now())
                        .dataCausedError(id)
                        .errorMessage("User with username = " + id + " does not exist")
                        .build()
                );
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public void createUser(UserCreationForUserServiceRequest request) {
        String id = request.getId();
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
                .username(request.getUsername())
                .email(request.getEmail())
                .dateOfBirth(request.getDateOfBirth())
                .build();
        save(user);
    }

    private boolean existById(String id) {
        return userRepository.existsById(id);
    }

    private void save(User user) {
        userRepository.save(user);
    }
}
