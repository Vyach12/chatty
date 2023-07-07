package ru.gusarov.messenger.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthErrorResponse {
    private String message;
    private long timestamp;
}
