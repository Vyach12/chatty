package com.chatty.chatsupport.util.exceptions.authentication;

import lombok.Builder;
import lombok.Getter;
import com.chatty.chatsupport.util.dto.errors.logic.ErrorCode;
import com.chatty.chatsupport.util.exceptions.BaseException;

import java.time.LocalDateTime;

@Getter
public class NotAuthorizedException extends BaseException {
    @Builder
    public NotAuthorizedException(LocalDateTime errorDate, String errorMessage, ErrorCode errorCode, Object dataCausedError) {
        super(errorDate, errorMessage, errorCode, dataCausedError);
    }
}
