package com.chatty.usermanagementservice.util.exceptions.authentication;

import com.chatty.usermanagementservice.util.dto.errors.logic.ErrorCode;
import com.chatty.usermanagementservice.util.exceptions.BaseException;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class InvalidUsernamePasswordException extends BaseException {
    @Builder
    public InvalidUsernamePasswordException(LocalDateTime errorDate, String errorMessage, ErrorCode errorCode, Object dataCausedError) {
        super(errorDate, errorMessage, errorCode, dataCausedError);
    }
}
