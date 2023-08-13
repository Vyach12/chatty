package com.chatty.chatsupport.util.dto.chats;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ChatDTO {
    private ObjectId id;
    private String name;
    private String lastMessage;
    private String lastMessageOwner;
}
