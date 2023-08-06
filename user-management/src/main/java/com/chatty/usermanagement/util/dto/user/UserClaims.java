package com.chatty.usermanagement.util.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserClaims {
    private Integer id;
    private String username;
    private String password;
    private String role;
}
