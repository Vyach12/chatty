package com.chatty.usermanagementservice.util.exceptions.token;

import com.chatty.usermanagementservice.util.dto.errors.logic.ErrorCode;
import com.chatty.usermanagementservice.util.exceptions.BaseException;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TokenNotFoundException extends BaseException {
    @Builder
    public TokenNotFoundException(LocalDateTime errorDate, String errorMessage, ErrorCode errorCode, Object dataCausedError) {
        super(errorDate, errorMessage, errorCode, dataCausedError);
    }
}