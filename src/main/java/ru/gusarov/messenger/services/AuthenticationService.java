package ru.gusarov.messenger.services;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.gusarov.messenger.models.User;
import ru.gusarov.messenger.util.UserException;
import ru.gusarov.messenger.util.auth.AuthenticationRequest;
import ru.gusarov.messenger.util.auth.RegisterRequest;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserService userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    public User register(RegisterRequest request) {
        var user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .dateOfBirth(request.getDateOfBirth())
                .email(request.getEmail())
                .isEnabled(true)
                .role(roleService.findByName("ROLE_USER"))
                .build();

        try {
            userService.save(user);
        } catch (DuplicateKeyException e) {
            throw new UserException("Username or email already exists");
        }

        return user;
    }

    public User authenticate(AuthenticationRequest request) {
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
            throw new UserException("Invalid username or password");
        }
        return user;
    }
}
