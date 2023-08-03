package com.chatty.usermanagementservice.services;

import com.chatty.usermanagementservice.models.User;
import com.chatty.usermanagementservice.repositories.UserRepository;
import com.chatty.usermanagementservice.util.dto.authentication.RegisterRequest;
import com.chatty.usermanagementservice.util.dto.errors.logic.ErrorCode;
import com.chatty.usermanagementservice.util.dto.user.UserDTO;
import com.chatty.usermanagementservice.util.dto.user.UserClaims;
import com.chatty.usermanagementservice.util.exceptions.user.EmailOccupiedException;
import com.chatty.usermanagementservice.util.exceptions.user.UserNotFoundException;
import com.chatty.usermanagementservice.util.exceptions.user.UsernameOccupiedException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleService roleService;

    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

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

    public UserClaims createUser(RegisterRequest request) {


        return convertToUserClaims(user);
    }

    public void changeEnabled(String username) {
        User user = findByUsername(username);
        user.setEnabled(!user.isEnabled());
        save(user);
    }

    public UserDTO convertToUserDTO(User user) {
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        userDTO.setRole(user.getRole().getName());
        userDTO.setAuthorities(user.getAuthorities());
        return userDTO;
    }

    public UserClaims convertToUserClaims(User user) {
        UserClaims userDTO = modelMapper.map(user, UserClaims.class);
        userDTO.setRole(user.getRole().getName());
        userDTO.setAuthorities(user.getAuthorities());
        return userDTO;
    }

}
