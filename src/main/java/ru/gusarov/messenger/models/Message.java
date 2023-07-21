package ru.gusarov.messenger.models;

import lombok.*;

import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    private String id;
    private Integer sender;
    private String text;
    private LocalDateTime dateOfSending;
    private LocalDateTime dateOfChange;
    private List<String> images;
    private List<String> videos;
    private List<String> music;
}
