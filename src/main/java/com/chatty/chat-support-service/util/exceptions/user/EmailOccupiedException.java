package ru.gusarov.messenger.util.exceptions.user;

import ru.gusarov.messenger.util.exceptions.BaseException;

import lombok.Builder;
import lombok.Getter;
import ru.gusarov.messenger.util.dto.errors.logic.ErrorCode;

import java.time.LocalDateTime;
@Getter
public class EmailOccupiedException extends BaseException {
    @Builder
    public EmailOccupiedException(LocalDateTime errorDate, String errorMessage, ErrorCode errorCode, Object dataCausedError) {
        super(errorDate, errorMessage, errorCode, dataCausedError);
    }
}