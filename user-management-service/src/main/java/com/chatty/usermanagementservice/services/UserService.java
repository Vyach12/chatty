package com.chatty.usermanagementservice.services;

import com.chatty.usermanagementservice.models.User;
import com.chatty.usermanagementservice.repositories.UserRepository;
import com.chatty.usermanagementservice.util.dto.authentication.AuthenticationRequest;
import com.chatty.usermanagementservice.util.dto.authentication.RegisterRequest;
import com.chatty.usermanagementservice.util.dto.errors.logic.ErrorCode;
import com.chatty.usermanagementservice.util.dto.user.UserDTO;
import com.chatty.usermanagementservice.util.dto.user.UserWithPasswordDTO;
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

    public UserDTO createUser(RegisterRequest request) {
        if (existByUsername(request.getUsername())) {
            throw UsernameOccupiedException.builder()
                    .errorCode(ErrorCode.USERNAME_IS_OCCUPIED)
                    .errorDate(LocalDateTime.now())
                    .dataCausedError(request)
                    .errorMessage("User with username " + request.getUsername() + " already exist")
                    .build();
        }

        if (existByEmail(request.getEmail())) {
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
                .dateOfBirth(request.getDateOfBirth())
                .email(request.getEmail())
                .isEnabled(true)
                .role(roleService.findByName("ROLE_USER"))
                .build();

        save(user);

        return convertToUserDTO(user);
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

    public UserWithPasswordDTO convertToUserWithPasswordDTO(User user) {
        UserWithPasswordDTO userDTO = modelMapper.map(user, UserWithPasswordDTO.class);
        userDTO.setRole(user.getRole().getName());
        userDTO.setAuthorities(user.getAuthorities());
        return userDTO;
    }

}
