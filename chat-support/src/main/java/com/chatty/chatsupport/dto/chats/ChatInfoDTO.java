package com.chatty.chatsupport.dto.chats;

import com.chatty.chatsupport.models.User;
import lombok.Builder;

import java.util.List;

@Builder
public record ChatInfoDTO(String id, String name, List<User> users){}
