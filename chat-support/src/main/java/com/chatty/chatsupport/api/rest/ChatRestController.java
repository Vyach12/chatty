package com.chatty.chatsupport.api.rest;

import com.chatty.chatsupport.models.Chat;
import com.chatty.chatsupport.models.User;
import com.chatty.chatsupport.services.ChatService;
import com.chatty.chatsupport.services.TokenService;
import com.chatty.chatsupport.services.UserService;
import com.chatty.chatsupport.dto.chats.ChatCreationRequest;
import com.chatty.chatsupport.dto.message.MessageDTO;
import com.chatty.util.dto.MessageResponse;
import com.chatty.util.dto.NewUsernameRequest;
import com.chatty.util.dto.UserCreationForChatServiceRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/chats")
@RequiredArgsConstructor
public class ChatRestController {
    private final ChatService chatService;
    private final TokenService tokenService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<?> getChats(@RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken) {
        String accessToken = tokenService.getToken(bearerToken);

        return ResponseEntity.ok(
                chatService.findChatsById(userService.findUserById(tokenService.extractSubject(accessToken))).stream()
                        .map(chatService::convertToChatDTO)
                        .toList()
        );
    }

    @PostMapping
    public ResponseEntity<?> createChat(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken,
            @Valid @RequestBody ChatCreationRequest chatCreationRequest
    ) {
        String accessToken = tokenService.getToken(bearerToken);
        User user = userService.findUserById(tokenService.extractSubject(accessToken));

        chatService.createChat(chatCreationRequest.users(), user, chatCreationRequest.name());

        return ResponseEntity.ok(new MessageResponse("chat successfully created"));
    }

    @GetMapping("/{chatID}")
    public ResponseEntity<?> getInfo(
            @PathVariable String chatID,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken
    ) {
        String accessToken = tokenService.getToken(bearerToken);

        User user = userService.findUserById(tokenService.extractSubject(accessToken));
        Chat chat = chatService.findChatById(chatID);

        return ResponseEntity.ok(chatService.getChatInfo(chat, user));
    }

    @GetMapping("/message/{chatID}")
    public ResponseEntity<?> getMessages(
            @PathVariable String chatID,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken
    ) {
        String accessToken = tokenService.getToken(bearerToken);

        User user = userService.findUserById(tokenService.extractSubject(accessToken));
        Chat chat = chatService.findChatById(chatID);

        return ResponseEntity.ok(chatService.getMessagesFromChat(chat, user));
    }

    @PostMapping("/message/{chatId}")
    public ResponseEntity<?> sendMessage(
            @PathVariable String chatId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken,
            @RequestBody MessageDTO messageDTO
    ) {
        String accessToken = tokenService.getToken(bearerToken);

        Chat chat = chatService.findChatById(chatId);
        User sender = userService.findUserById(tokenService.extractSubject(accessToken));

        chatService.sendMessage(messageDTO, chat, sender);
        return ResponseEntity.ok(new MessageResponse("Message successfully sent"));
    }

    @GetMapping("/users")
    public ResponseEntity<?> getUsers(){
        return ResponseEntity.ok(userService.findAll());
    }

}
