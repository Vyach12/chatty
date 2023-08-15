package com.chatty.chatsupport.dto.chats;

import com.chatty.chatsupport.models.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ChatCreationRequest {
    private String name;
    private List<User> users;
}
