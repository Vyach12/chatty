package ru.gusarov.messenger.rest.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.gusarov.messenger.models.Chat;
import ru.gusarov.messenger.models.Message;
import ru.gusarov.messenger.models.UserID;
import ru.gusarov.messenger.services.ChatService;
import ru.gusarov.messenger.services.UserService;
import ru.gusarov.messenger.util.dto.authentication.MessageResponse;
import ru.gusarov.messenger.util.dto.message.MessageDTO;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatsRestController {
    private final ChatService chatService;
    private final UserService userService;

    @GetMapping("/chatName")
    public ResponseEntity<?> messenger() {
        return ResponseEntity.ok(HttpEntity.EMPTY);
    }

    @PostMapping("/{username}")
    public ResponseEntity<?> createChat(
            @PathVariable String username,
            @AuthenticationPrincipal UserDetails userDetails) {
        List<UserID> usersId = Arrays.asList(
                new UserID(userService.findByUsername(username).getId()),
                new UserID(userService.findByUsername(userDetails.getUsername()).getId())
        );
        Chat chat = Chat.builder()
                .users(usersId)
                .messages(new ArrayList<>())
                .build();
        chatService.save(chat);
        return ResponseEntity.ok(
                new MessageResponse("chat successfully created")
        );
    }

    @PostMapping("/{chatId}")
    public ResponseEntity<?> sendMessage(
            @PathVariable String chatId,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody MessageDTO messageDTO) {

        Chat chat = chatService.findChatById(chatId);

        Message message = Message.builder()
                .text(messageDTO.getText())
                .sender(userService.findByUsername(userDetails.getUsername()).getId())
                .dateOfSending(LocalDateTime.now())
                .music(messageDTO.getMusic())
                .images(messageDTO.getImages())
                .videos(messageDTO.getVideos())
                .build();

        chatService.saveMessage(chat, message);
        return ResponseEntity.ok(new MessageResponse("Message successfully sent"));
    }
/*
    @GetMapping("/{username}")
    public ResponseEntity<?> messenger(
            @PathVariable String username,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User recipient = userService.findByUsername(username);
        User sender = userService.findByUsername(userDetails.getUsername());

        return ResponseEntity.ok(chatsService
                .findMessagesByPeople(sender, recipient).stream()
                .map(chatsService::convertToMessageDTO)
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

        chatsService.save(
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

        chatsService.update(messageDTO.getId(), messageDTO.getMessage());

        return ResponseEntity.ok(new MessageResponse("Message was successfully edited"));
    }*/
}
