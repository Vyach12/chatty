package ru.gusarov.messenger.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserForMessageDTO {
    private String username;
}
