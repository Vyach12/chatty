package com.chatty.chatsupport.models;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Document(collection = "chats")
public class Chat {
    @Id
    private String id;
    private String name;
    @DBRef
    private List<User> users;
    private List<Message> messages;
}
