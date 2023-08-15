package com.chatty.util.exceptions.chat;

import com.chatty.util.errors.logic.ErrorCode;
import com.chatty.util.exceptions.BaseException;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
@Getter
public class ChatNotFoundException extends BaseException {
    @Builder
    public ChatNotFoundException(LocalDateTime errorDate, String errorMessage, ErrorCode errorCode, Object dataCausedError) {
        super(errorDate, errorMessage, errorCode, dataCausedError);
    }
}
