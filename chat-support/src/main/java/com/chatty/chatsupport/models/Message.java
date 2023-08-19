package com.chatty.chatsupport.models;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    private String id;
    @DBRef
    private User sender;
    private String text;
    private LocalDateTime dateOfSending;
    private LocalDateTime dateOfChange;
    private List<String> images;
    private List<String> videos;
    private List<String> music;
}
