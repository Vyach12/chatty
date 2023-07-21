package ru.gusarov.messenger.services;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.gusarov.messenger.util.dto.errors.logic.ErrorCode;
import ru.gusarov.messenger.util.dto.user.UserDTO;
import ru.gusarov.messenger.util.dto.user.UserForMessageDTO;
import ru.gusarov.messenger.models.User;
import ru.gusarov.messenger.repositories.UserRepository;
import ru.gusarov.messenger.util.exceptions.user.UserNotFoundException;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class UserService {
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;

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

    public User findById(int id) {
        return userRepository.findById(id).orElseThrow();
    }

    public void changeEnabled(String username) {
        User user = findByUsername(username);
        user.setEnabled(!user.isEnabled());
        save(user);
    }

    public boolean existByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean isEnabled(String username) {
        return findByUsername(username).isEnabled();
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public UserForMessageDTO convertToUserForMessageDTO(User user) {
        return modelMapper.map(user, UserForMessageDTO.class);
    }

    public UserDTO convertToUserDTO(User user) {
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        userDTO.setRole(user.getRole().getName());
        return userDTO;
    }
}
