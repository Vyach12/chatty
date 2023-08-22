package com.chatty.authentication.api.rest.handling;

import com.chatty.util.errors.data.DataFieldError;
import com.chatty.util.errors.data.InvalidDataResponse;
import com.chatty.util.errors.logic.ErrorCode;
import com.chatty.util.errors.logic.ErrorEntity;
import com.chatty.util.errors.logic.ErrorResponse;
import com.chatty.util.exceptions.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoHandlerFoundException.class)
    private ErrorResponse handleNoHandlerException(NoHandlerFoundException ex) {
        return ErrorResponse.builder()
                .errors(List.of(ErrorEntity.builder()
                        .errorCode(ErrorCode.INVALID_REQUEST_PATH)
                        .errorDate(LocalDateTime.now())
                        .errorMessage(ex.getMessage())
                        .dataCausedError(ex.getRequestURL())
                        .build()))
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    private InvalidDataResponse handleValidationException(MethodArgumentNotValidException ex) {
        List<DataFieldError> fieldErrors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> fieldErrors.add(DataFieldError.builder()
                .fieldName(((FieldError) error).getField())
                .fieldError(error.getDefaultMessage())
                .build()));

        return InvalidDataResponse.builder()
                .fieldErrorsNumber(fieldErrors.size())
                .fieldErrors(fieldErrors)
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BaseException.class)
    private ErrorResponse handleException(BaseException e) {
        return ErrorResponse.builder()
                .errors(List.of(ErrorEntity.builder()
                        .errorCode(e.getErrorCode())
                        .errorDate(e.getErrorDate())
                        .errorMessage(e.getErrorMessage())
                        .dataCausedError(e.getDataCausedError())
                        .build()))
                .build();
    }
}
