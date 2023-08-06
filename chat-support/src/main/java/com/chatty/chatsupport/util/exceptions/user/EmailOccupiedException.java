package com.chatty.chatsupport.util.exceptions.user;

import com.chatty.chatsupport.util.exceptions.BaseException;

import lombok.Builder;
import lombok.Getter;
import com.chatty.chatsupport.util.dto.errors.logic.ErrorCode;

import java.time.LocalDateTime;
@Getter
public class EmailOccupiedException extends BaseException {
    @Builder
    public EmailOccupiedException(LocalDateTime errorDate, String errorMessage, ErrorCode errorCode, Object dataCausedError) {
        super(errorDate, errorMessage, errorCode, dataCausedError);
    }
}