package com.chatty.usermanagementservice.util.dto.errors.data;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataFieldError {
    private String fieldName;
    private String fieldError;
}