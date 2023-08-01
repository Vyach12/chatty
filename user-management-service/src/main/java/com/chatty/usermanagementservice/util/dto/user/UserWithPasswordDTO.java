package com.chatty.usermanagementservice.util.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserWithPasswordDTO {
    private Integer id;
    private String username;
    private String password;
    private String email;
    private Date dateOfBirth;
    private boolean isEnabled;
    private String role;
    private Collection<? extends GrantedAuthority> authorities;
}
