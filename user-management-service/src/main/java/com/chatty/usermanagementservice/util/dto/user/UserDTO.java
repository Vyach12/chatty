package com.chatty.usermanagementservice.util.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {
    private Integer id;
    private String username;
    private String email;
    private Date dateOfBirth;
    private boolean isEnabled;
    private String role;

    private Collection<? extends GrantedAuthority> authorities;
}