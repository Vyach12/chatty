package com.chatty.chatsupport.dto.message;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageDTO {
    private String text;
    private List<String> images;
    private List<String> videos;
    private List<String> music;
}
