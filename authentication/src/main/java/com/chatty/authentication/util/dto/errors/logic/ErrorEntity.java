package com.chatty.authentication.util.dto.errors.logic;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorEntity {
    private ErrorCode errorCode;
    private LocalDateTime errorDate;
    private String errorMessage;
    private Object dataCausedError;
}
