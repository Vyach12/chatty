package com.chatty.usermanagement.util.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
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
public class UserInfo {

    @Size(min = 4, max = 30, message = "username should be between 4 and 30 symbols")
    @NotNull
    private String username;
    @Email
    @NotNull
    private String email;
    @NotNull
    private Date dateOfBirth;
}
