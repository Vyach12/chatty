package com.chatty.authentication.util.exceptions;

import com.chatty.authentication.util.dto.errors.logic.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class BaseException extends RuntimeException{
    protected LocalDateTime errorDate;
    protected String errorMessage;
    protected ErrorCode errorCode;
    protected Object dataCausedError;
}
