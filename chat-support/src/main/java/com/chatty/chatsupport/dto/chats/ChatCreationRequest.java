package com.chatty.chatsupport.dto.chats;

import com.chatty.chatsupport.models.User;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ChatCreationRequest(
        @NotNull(message = "The request must contain the message field")
        @Size(min = 4, max = 30, message = "name should be between 4 and 30 symbols")
        String name,
        @NotNull(message = "The request must contain the users field")
        @NotEmpty(message = "There must be at least 1 person in the chat")
        List<User> users
) {}
