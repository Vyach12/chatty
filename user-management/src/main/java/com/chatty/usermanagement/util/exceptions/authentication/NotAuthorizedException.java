package com.chatty.usermanagement.util.exceptions.authentication;

import com.chatty.usermanagement.util.dto.errors.logic.ErrorCode;
import com.chatty.usermanagement.util.exceptions.BaseException;
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
