package com.chatty.chatsupport.dto.chats;

import com.chatty.chatsupport.models.User;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatCreationRequest {
    private String name;
    private List<User> users;
}
