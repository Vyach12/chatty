package ru.gusarov.messenger.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MessageErrorResponse {
    private String message;
    private long timestamp;
}
