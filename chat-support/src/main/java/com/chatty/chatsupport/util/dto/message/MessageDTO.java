package com.chatty.chatsupport.util.dto.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.chatty.chatsupport.util.dto.user.UserForMessageDTO;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageDTO {
    private int id;
    private String message;
    private UserForMessageDTO sender;
    private UserForMessageDTO recipient;
    private LocalDateTime dateOfSending;
    private LocalDateTime dateOfChange;
}
