package ru.gusarov.messenger.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.gusarov.messenger.services.AuthenticationService;
import ru.gusarov.messenger.services.UserService;
import ru.gusarov.messenger.util.AuthErrorResponse;
import ru.gusarov.messenger.util.AuthException;
import ru.gusarov.messenger.util.auth.AuthenticationRequest;
import ru.gusarov.messenger.util.auth.AuthenticationResponse;
import ru.gusarov.messenger.util.auth.RegisterRequest;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @Valid @RequestBody RegisterRequest request,
            BindingResult bindingResult
    ){
        if (bindingResult.hasErrors()) {
            String errors = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.joining());
            throw new AuthException(errors);
        }

        if (userService.existByUsername(request.getUsername())) {
            throw new AuthException("User with username " + request.getUsername() + " already exist");
        }

        if (userService.existByEmail(request.getEmail())) {
            throw new AuthException("User with email " + request.getEmail() + " already exist");
        }
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @Valid @RequestBody AuthenticationRequest request,
            BindingResult bindingResult
    ){
        if (bindingResult.hasErrors()) {
            String errors = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.joining());
            throw new AuthException(errors);
        }

        if (!userService.existByUsername(request.getUsername())) {
            throw new AuthException("User with username " + request.getUsername() + " does not exist");
        }

        return ResponseEntity.ok(service.authenticate(request));
    }

    @ExceptionHandler
    private ResponseEntity<AuthErrorResponse> handleException(AuthException e) {
        AuthErrorResponse response = new AuthErrorResponse(
                e.getMessage(),
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
