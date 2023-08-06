package com.chatty.chatsupport.util.exceptions.user;

import lombok.Builder;
import lombok.Getter;
import com.chatty.chatsupport.util.dto.errors.logic.ErrorCode;
import com.chatty.chatsupport.util.exceptions.BaseException;

import java.time.LocalDateTime;

@Getter
public class UserNotFoundException extends BaseException {
    @Builder

    public UserNotFoundException(LocalDateTime errorDate, String errorMessage, ErrorCode errorCode, Object dataCausedError) {
        super(errorDate, errorMessage, errorCode, dataCausedError);
    }
}