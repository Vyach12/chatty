package com.chatty.chatsupport.dto.chats;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatForPresentationDTO {
    private String id;
    private String name;
    private String lastMessage;
    private String lastMessageOwner;
}
