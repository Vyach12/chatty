package com.chatty.chatsupport.dto.chats;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ChatForPresentationDTO {
    private String id;
    private String name;
    private String lastMessage;
    private String lastMessageOwner;
}
