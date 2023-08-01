package ru.gusarov.messenger.util.exceptions.authentication;

import lombok.Builder;
import lombok.Getter;
import ru.gusarov.messenger.util.dto.errors.logic.ErrorCode;
import ru.gusarov.messenger.util.exceptions.BaseException;

import java.time.LocalDateTime;

@Getter
public class NotAuthorizedException extends BaseException {
    @Builder
    public NotAuthorizedException(LocalDateTime errorDate, String errorMessage, ErrorCode errorCode, Object dataCausedError) {
        super(errorDate, errorMessage, errorCode, dataCausedError);
    }
}
