package com.chatty.usermanagement.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChangeUsernameRequest {
    private String username;
}
