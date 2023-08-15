package com.chatty.util.errors.logic;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private List<ErrorEntity> errors;
}
