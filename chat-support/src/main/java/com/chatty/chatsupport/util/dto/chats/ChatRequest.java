package com.chatty.chatsupport.util.dto.chats;

import com.chatty.chatsupport.models.UserID;
import lombok.Builder;
import lombok.Setter;

import java.util.List;

@Setter
@Builder
public class ChatRequest {
    private String name;
    private List<UserID> users;
}
