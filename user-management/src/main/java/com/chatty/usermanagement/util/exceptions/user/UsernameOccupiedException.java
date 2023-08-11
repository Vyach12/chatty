package com.chatty.usermanagement.util.exceptions.user;

import com.chatty.usermanagement.util.dto.errors.logic.ErrorCode;
import com.chatty.usermanagement.util.exceptions.BaseException;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
@Getter
public class UsernameOccupiedException extends BaseException {
    @Builder
    public UsernameOccupiedException(LocalDateTime errorDate, String errorMessage, ErrorCode errorCode, Object dataCausedError) {
        super(errorDate, errorMessage, errorCode, dataCausedError);
    }
}