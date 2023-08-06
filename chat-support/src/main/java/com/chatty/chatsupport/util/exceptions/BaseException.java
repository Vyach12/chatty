package com.chatty.chatsupport.util.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.chatty.chatsupport.util.dto.errors.logic.ErrorCode;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class BaseException extends RuntimeException{
    protected LocalDateTime errorDate;
    protected String errorMessage;
    protected ErrorCode errorCode;
    protected Object dataCausedError;
}
