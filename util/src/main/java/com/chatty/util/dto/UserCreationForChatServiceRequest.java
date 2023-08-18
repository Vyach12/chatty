package com.chatty.util.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCreationForChatServiceRequest {
    private String id;
    private String username;
}
