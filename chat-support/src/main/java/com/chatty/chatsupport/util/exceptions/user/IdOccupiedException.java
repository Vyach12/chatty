package com.chatty.chatsupport.util.exceptions.user;

import com.chatty.chatsupport.util.dto.errors.logic.ErrorCode;
import com.chatty.chatsupport.util.exceptions.BaseException;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class IdOccupiedException extends BaseException {
    @Builder
    public IdOccupiedException(LocalDateTime errorDate, String errorMessage, ErrorCode errorCode, Object dataCausedError) {
        super(errorDate, errorMessage, errorCode, dataCausedError);
    }
}
