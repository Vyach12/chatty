package com.chatty.usermanagement.util.exceptions;

import com.chatty.usermanagement.util.dto.errors.logic.ErrorCode;
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