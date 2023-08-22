package com.chatty.authentication.dto;

import jakarta.validation.constraints.NotNull;

public record AuthenticationRequest(
        @NotNull(message = "The request must contain the username field")
        String username,
        @NotNull(message = "The request must contain the password field")
        String password
){}
