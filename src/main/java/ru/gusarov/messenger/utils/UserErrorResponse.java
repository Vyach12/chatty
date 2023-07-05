package ru.gusarov.messenger.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserErrorResponse {
    private String message;
    private long timestamp;
}
