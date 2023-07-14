package ru.gusarov.messenger.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.gusarov.messenger.models.User;
import ru.gusarov.messenger.util.dto.errors.logic.ErrorCode;
import ru.gusarov.messenger.util.exceptions.authentication.InvalidUsernamePasswordException;
import ru.gusarov.messenger.util.exceptions.user.*;
import ru.gusarov.messenger.util.dto.authentication.AuthenticationRequest;
import ru.gusarov.messenger.util.dto.authentication.RegisterRequest;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserService userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public User register(RegisterRequest request) {
        if (userService.existByUsername(request.getUsername())) {
            throw UsernameOccupiedException.builder()
                    .errorCode(ErrorCode.USERNAME_IS_OCCUPIED)
                    .errorDate(LocalDateTime.now())
                    .dataCausedError(request)
                    .errorMessage("User with username " + request.getEmail() + " already exist")
                    .build();
        }

        if (userService.existByEmail(request.getEmail())) {
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

        userService.save(user);

        return user;
    }

    public User authenticate(AuthenticationRequest request) {
        if (!userService.existByUsername(request.getUsername())) {
            throw UserNotFoundException.builder()
                    .errorCode(ErrorCode.USER_NOT_FOUND)
                    .errorDate(LocalDateTime.now())
                    .dataCausedError(request)
                    .errorMessage("User with username " + request.getUsername() + " does not exist")
                    .build();
        }

        if(!userService.isEnabled(request.getUsername())) {
            throw UserIsNotEnabledException.builder()
                    .errorCode(ErrorCode.USER_IS_NOT_ENABLED)
                    .errorDate(LocalDateTime.now())
                    .dataCausedError(request)
                    .errorMessage("User with username " + request.getUsername() + " is banned")
                    .build();
        }
        User user;
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
            user = userService.findByUsername(request.getUsername());
        } catch (AuthenticationException e) {
            throw InvalidUsernamePasswordException.builder()
                    .errorCode(ErrorCode.INVALID_USERNAME_PASSWORD)
                    .errorDate(LocalDateTime.now())
                    .dataCausedError(request)
                    .errorMessage("Invalid username or password")
                    .build();
        }
        return user;
    }

    public void logout(String refreshToken) {
        var storedToken = tokenService.findByToken(refreshToken);
        if (storedToken.isPresent()) {
            tokenService.delete(storedToken.get());
            SecurityContextHolder.clearContext();
        }
    }
}
