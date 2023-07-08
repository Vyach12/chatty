package ru.gusarov.messenger.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.gusarov.messenger.dto.MessageDTO;
import ru.gusarov.messenger.models.Message;
import ru.gusarov.messenger.models.User;
import ru.gusarov.messenger.services.MessageService;
import ru.gusarov.messenger.services.UserService;
import ru.gusarov.messenger.util.MessageException;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/message")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;
    private final UserService userService;

    @GetMapping("/{username}")
    public ResponseEntity<List<MessageDTO>> messenger(
            @PathVariable String username,
            @AuthenticationPrincipal UserDetails userDetails
    ) {

        User recipient = userService.findByUsername(username);
        User sender = userService.findByUsername(userDetails.getUsername());

        List<MessageDTO> list = messageService
                .findMessagesByPeople(sender, recipient).stream()
                .map(messageService::convertToMessageDTO)
                .toList();
        return ResponseEntity.ok(list);
    }

    @PostMapping("/{username}")
    public ResponseEntity<HttpStatus> sendMessage(
            @PathVariable String username,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody String textMessage) {
        User recipient = userService.findByUsername(username);
        User sender = userService.findByUsername(userDetails.getUsername());

        Message message = Message.builder()
                .sender(sender)
                .recipient(recipient)
                .message(textMessage)
                .dateOfSending(LocalDateTime.now())
                .build();

        messageService.save(message);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PatchMapping("/{username}")
    public ResponseEntity<HttpStatus> changeMessage(
            @PathVariable String username,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody MessageDTO messageDTO
    ) {

        if(!messageDTO.getSender().getUsername().equals(userDetails.getUsername())) {
            throw new MessageException("You cant change message");
        }

        messageService.update(
                messageService.findById(messageDTO.getId()),
                messageDTO.getMessage()
        );

        return ResponseEntity.ok(HttpStatus.OK);
    }
}
