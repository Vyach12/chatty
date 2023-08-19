package com.chatty.chatsupport.dto.chats;

import com.chatty.chatsupport.models.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ChatInfoDTO {

    private String id;
    private String name;
    private List<User> users;
}
