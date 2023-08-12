package com.chatty.chatsupport.util.exceptions.chat;

import com.chatty.chatsupport.util.dto.errors.logic.ErrorCode;
import com.chatty.chatsupport.util.exceptions.BaseException;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class NoAccessException extends BaseException {
    @Builder
    public NoAccessException(LocalDateTime errorDate, String errorMessage, ErrorCode errorCode, Object dataCausedError) {
        super(errorDate, errorMessage, errorCode, dataCausedError);
    }
}