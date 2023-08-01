package ru.gusarov.messenger.util.dto.authentication;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    @Size(min = 4, max = 30, message = "username should be between 4 and 30 symbols")
    @NotNull
    private String username;
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}$",
            message = "Password should contain at least one uppercase letter," +
                    "one lowercase letter, one digit, one special character and be at least 8 characters long"
    )
    @NotNull
    private String password;
    @Email
    @NotNull
    private String email;
    @NotNull
    private Date dateOfBirth;
}
