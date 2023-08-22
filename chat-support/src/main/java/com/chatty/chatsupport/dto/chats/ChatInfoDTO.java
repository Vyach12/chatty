package com.chatty.chatsupport.dto.chats;

import com.chatty.chatsupport.models.User;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatInfoDTO {

    private String id;
    private String name;
    private List<User> users;
}
