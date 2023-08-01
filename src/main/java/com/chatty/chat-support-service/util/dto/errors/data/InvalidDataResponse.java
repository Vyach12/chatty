package ru.gusarov.messenger.util.dto.errors.data;

import lombok.*;
import ru.gusarov.messenger.util.dto.errors.data.DataFieldError;

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
