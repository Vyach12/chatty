package com.chatty.chatsupport.services;

import com.chatty.chatsupport.models.User;
import com.chatty.chatsupport.repositories.UserRepository;
import com.chatty.chatsupport.util.dto.errors.logic.ErrorCode;
import com.chatty.chatsupport.util.exceptions.user.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User findUserById(ObjectId id) {
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

    public boolean existById(UUID id) {
        return userRepository.existsById(id);
    }

    public void save(User user) {
        userRepository.save(user);
    }
}
