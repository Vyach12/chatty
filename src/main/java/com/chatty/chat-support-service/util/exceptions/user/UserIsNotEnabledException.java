package ru.gusarov.messenger.util.exceptions.user;

import lombok.Builder;
import lombok.Getter;
import ru.gusarov.messenger.util.dto.errors.logic.ErrorCode;
import ru.gusarov.messenger.util.exceptions.BaseException;

import java.time.LocalDateTime;

@Getter
public class UserIsNotEnabledException extends BaseException {
    @Builder
    public UserIsNotEnabledException(LocalDateTime errorDate, String errorMessage, ErrorCode errorCode, Object dataCausedError) {
        super(errorDate, errorMessage, errorCode, dataCausedError);
    }
}
