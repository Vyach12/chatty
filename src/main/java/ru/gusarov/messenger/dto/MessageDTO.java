package ru.gusarov.messenger.dto;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.gusarov.messenger.models.User;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageDTO {
    private String message;
    private UserDTO sender;
    private UserDTO recipient;
    private LocalDateTime dateOfSending;
    private LocalDateTime dateOfChange;
}
