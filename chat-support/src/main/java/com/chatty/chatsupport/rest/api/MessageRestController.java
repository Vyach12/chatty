package com.chatty.chatsupport.rest.api

-support.rest.api;

import com.chatty.chatsupport.models.Message;
import com.chatty.chatsupport.services.MessageService;
import com.chatty.chatsupport.util.dto.authentication.MessageResponse;
import com.chatty.chatsupport.util.dto.errors.logic.ErrorCode;
import com.chatty.chatsupport.util.dto.message.MessageDTO;
import com.chatty.chatsupport.util.exceptions.user.NoRightToChangeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/message")
@RequiredArgsConstructor
public class MessageRestController {
    private final MessageService messageService;
    private final UserService userService;

    @GetMapping("/{username}")
    public ResponseEntity<?> messenger(
            @PathVariable String username,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User recipient = userService.findByUsername(username);
        User sender = userService.findByUsername(userDetails.getUsername());

        return ResponseEntity.ok(messageService
                .findMessagesByPeople(sender, recipient).stream()
                .map(messageService::convertToMessageDTO)
                .toList());
    }

    @PostMapping("/{username}")
    public ResponseEntity<?> sendMessage(
            @PathVariable String username,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody String textMessage
    ) {
        User recipient = userService.findByUsername(username);
        User sender = userService.findByUsername(userDetails.getUsername());

        messageService.save(
                Message.builder()
                        .sender(sender)
                        .recipient(recipient)
                        .message(textMessage)
                        .dateOfSending(LocalDateTime.now())
                        .build()
        );

        return ResponseEntity.ok(new MessageResponse("Message was successfully sent"));
    }

    @PatchMapping
    public ResponseEntity<?> changeMessage(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody MessageDTO messageDTO
    ) {
        if(!messageDTO.getSender().getUsername().equals(userDetails.getUsername())) {
            throw NoRightToChangeMessage.builder()
                    .errorCode(ErrorCode.NO_RIGHT_TO_CHANGE)
                    .errorDate(LocalDateTime.now())
                    .dataCausedError(messageDTO)
                    .errorMessage("You cant change this massage")
                    .build();
        }

        messageService.update(messageDTO.getId(), messageDTO.getMessage());

        return ResponseEntity.ok(new MessageResponse("Message was successfully edited"));
    }
}
