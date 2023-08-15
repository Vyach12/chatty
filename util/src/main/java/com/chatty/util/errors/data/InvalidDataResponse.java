package com.chatty.util.errors.data;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvalidDataResponse {
    private int fieldErrorsNumber;
    private List<DataFieldError> fieldErrors;
}
