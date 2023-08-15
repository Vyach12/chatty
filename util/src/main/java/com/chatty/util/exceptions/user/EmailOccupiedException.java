package com.chatty.util.exceptions.user;

import com.chatty.util.errors.logic.ErrorCode;
import com.chatty.util.exceptions.BaseException;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
@Getter
public class EmailOccupiedException extends BaseException {
    @Builder
    public EmailOccupiedException(LocalDateTime errorDate, String errorMessage, ErrorCode errorCode, Object dataCausedError) {
        super(errorDate, errorMessage, errorCode, dataCausedError);
    }
}