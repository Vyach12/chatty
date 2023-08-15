package com.chatty.chatsupport.services;

import com.chatty.chatsupport.models.User;
import com.chatty.chatsupport.repositories.UserRepository;
import com.chatty.util.errors.logic.ErrorCode;
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

    public boolean existById(String id) {
        return userRepository.existsById(id);
    }

    public void save(User user) {
        userRepository.save(user);
    }
}
