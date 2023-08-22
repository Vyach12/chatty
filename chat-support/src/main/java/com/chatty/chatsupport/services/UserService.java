package com.chatty.chatsupport.services;

import com.chatty.chatsupport.models.User;
import com.chatty.chatsupport.repositories.UserRepository;
import com.chatty.util.dto.NewUsernameRequest;
import com.chatty.util.dto.UserCreationForChatServiceRequest;
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

    public User findUserById(String id) {
        return userRepository.findUserById(id).orElseThrow(
                () -> UserNotFoundException.builder()
                        .errorCode(ErrorCode.USER_NOT_FOUND)
                        .errorDate(LocalDateTime.now())
                        .dataCausedError(id)
                        .errorMessage("User not found")
                        .build());
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }


    public void createUser(UserCreationForChatServiceRequest request) {
        if(existById(request.id())){
            throw IdOccupiedException.builder()
                    .errorCode(ErrorCode.ID_IS_OCCUPIED)
                    .errorDate(LocalDateTime.now())
                    .dataCausedError(request.id())
                    .errorMessage("User with id = " + request.id() + " already exists")
                    .build();
        }

        User user = User.builder()
                .id(request.id())
                .username(request.username())
                .build();

        save(user);
    }

    private boolean existById(String id) {
        return userRepository.existsUserById(id);
    }

    private void save(User user) {
        userRepository.save(user);
    }

    public void changeUsername(NewUsernameRequest request) {
        User user = findUserById(request.id());
        user.setUsername(request.username());
        save(user);
    }
}
