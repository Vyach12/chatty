package com.chatty.usermanagementservice.util.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserWithPasswordDTO {
    private String username;
    private String password;
    private String role;
    private Collection<? extends GrantedAuthority> authorities;
}
