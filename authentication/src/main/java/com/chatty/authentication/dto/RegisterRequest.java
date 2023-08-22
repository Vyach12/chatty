package com.chatty.authentication.dto;

import jakarta.validation.constraints.*;

import java.util.Date;

public record RegisterRequest(
        @NotNull(message = "The request must contain the username field")
        @Size(min = 4, max = 30, message = "Username should be between 4 and 30 symbols")
        String username,
        @NotNull(message = "The request must contain the password field")
        @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}$",
                message = "Password should contain at least one uppercase letter, one lowercase letter," +
                        "one digit, one special character and be at least 8 characters long")
        String password,
        @Email(message = "Incorrect email")
        @NotNull(message = "The request must contain the email field")
        String email,
        @NotNull(message = "The request must contain the dateOfBirth field")
        @Past(message = "Incorrect Date")
        Date dateOfBirth
) {}

