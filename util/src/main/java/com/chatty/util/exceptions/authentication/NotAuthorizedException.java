package com.chatty.util.exceptions.authentication;

import com.chatty.util.errors.logic.ErrorCode;
import com.chatty.util.exceptions.BaseException;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class NotAuthorizedException extends BaseException {
    @Builder
    public NotAuthorizedException(LocalDateTime errorDate, String errorMessage, ErrorCode errorCode, Object dataCausedError) {
        super(errorDate, errorMessage, errorCode, dataCausedError);
    }
}
