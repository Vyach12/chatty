package com.chatty.chatsupport.dto.chats;

import lombok.Builder;

@Builder
public record ChatForPresentationDTO(String id, String name, String lastMessage, String lastMessageOwner){}
