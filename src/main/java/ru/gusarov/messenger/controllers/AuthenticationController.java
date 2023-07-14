package ru.gusarov.messenger.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.gusarov.messenger.models.User;
import ru.gusarov.messenger.services.AuthenticationService;
import ru.gusarov.messenger.services.TokenService;
import ru.gusarov.messenger.services.UserService;
import ru.gusarov.messenger.util.AuthException;
import ru.gusarov.messenger.util.auth.AuthenticationRequest;
import ru.gusarov.messenger.util.auth.AuthenticationResponse;
import ru.gusarov.messenger.util.auth.RegisterRequest;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authService;
    private final UserService userService;
    private final TokenService tokenService;

    @PostMapping("/register")
    public ResponseEntity<HttpStatus> register(
            @Valid @RequestBody RegisterRequest authRequest,
            HttpServletResponse response,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            String errors = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.joining());
            throw new AuthException(errors);
        }

        if (userService.existByUsername(authRequest.getUsername())) {
            throw new AuthException("User with username " + authRequest.getUsername() + " already exist");
        }

        if (userService.existByEmail(authRequest.getEmail())) {
            throw new AuthException("User with email " + authRequest.getEmail() + " already exist");
        }

        tokenService.updateTokens(
                response, authService.register(authRequest)
        );

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<HttpStatus> authenticate(
            @Valid @RequestBody AuthenticationRequest authRequest,
            HttpServletResponse response,
            BindingResult bindingResult
    ){
        if (bindingResult.hasErrors()) {
            String errors = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.joining());
            throw new AuthException(errors);
        }

        if (!userService.existByUsername(authRequest.getUsername())) {
            throw new AuthException("User with username " + authRequest.getUsername() + " does not exist");
        }

        if(!userService.isEnabled(authRequest.getUsername())) {
            throw new AuthException("User with username " + authRequest.getUsername() + " is banned");
        }

        tokenService.updateTokens(
                response, authService.authenticate(authRequest)
        );

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/refresh")
    public ResponseEntity<HttpStatus> refresh(HttpServletRequest request, HttpServletResponse response) {
        tokenService.refresh(request, response);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
