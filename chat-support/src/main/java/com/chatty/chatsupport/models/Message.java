package com.chatty.chatsupport.models;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    private String id;
    private String sender;
    private String text;
    private LocalDateTime dateOfSending;
    private LocalDateTime dateOfChange;
    private List<String> images;
    private List<String> videos;
    private List<String> music;
}
