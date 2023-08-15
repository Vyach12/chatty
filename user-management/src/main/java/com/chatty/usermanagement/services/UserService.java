package com.chatty.usermanagement.services;

import com.chatty.usermanagement.models.User;
import com.chatty.usermanagement.repositories.UserRepository;
import com.chatty.usermanagement.dto.UserDTO;
import com.chatty.util.errors.logic.ErrorCode;
import com.chatty.util.exceptions.user.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> UserNotFoundException.builder()
                        .errorCode(ErrorCode.USER_NOT_FOUND)
                        .errorDate(LocalDateTime.now())
                        .dataCausedError(username)
                        .errorMessage("User with username = " + username + " does not exist")
                        .build()
                );
    }
    public User findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> UserNotFoundException.builder()
                        .errorCode(ErrorCode.USER_NOT_FOUND)
                        .errorDate(LocalDateTime.now())
                        .dataCausedError(id)
                        .errorMessage("User with id = " + id + " does not exist")
                        .build()
                );
    }

    public boolean existById(UUID id) {
        return userRepository.existsById(id);
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public UserDTO convertToUserDTO(User user) {
        return modelMapper.map(user, UserDTO.class);
    }
}
