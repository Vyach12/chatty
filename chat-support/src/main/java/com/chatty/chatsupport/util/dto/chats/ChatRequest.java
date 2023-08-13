package com.chatty.chatsupport.util.dto.chats;

import com.chatty.chatsupport.models.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;

import java.util.List;

@Getter
@Setter
@Builder
public class ChatRequest {
    private String name;
    private List<ObjectId> users;
}
