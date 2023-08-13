package com.chatty.chatsupport.models;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
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
    private ObjectId id;
    private String name;
    private List<ObjectId> users;
    private List<Message> messages;
}
