package com.chatty.usermanagement.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ChangeUsernameRequest(
        @NotNull(message = "The request must contain the username field")
        @Size(min = 4, max = 30, message = "Username should be between 4 and 30 symbols")
        String username
){}
