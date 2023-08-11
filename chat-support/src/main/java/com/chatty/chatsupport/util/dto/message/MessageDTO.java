package com.chatty.chatsupport.util.dto.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageDTO {
    private String text;
    private List<String> images;
    private List<String> videos;
    private List<String> music;
}
