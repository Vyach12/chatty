package com.chatty.authentication.util.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInfo {
    private String username;
    private String email;
    private Date dateOfBirth;
}
