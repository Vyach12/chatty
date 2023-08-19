package com.chatty.chatsupport.rest.api;

import com.chatty.chatsupport.models.Chat;
import com.chatty.chatsupport.models.User;
import com.chatty.chatsupport.services.ChatService;
import com.chatty.chatsupport.services.TokenService;
import com.chatty.chatsupport.services.UserService;
import com.chatty.chatsupport.dto.chats.ChatCreationRequest;
import com.chatty.chatsupport.dto.message.MessageDTO;
import com.chatty.util.dto.MessageResponse;
import com.chatty.util.dto.UserCreationForChatServiceRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/chats")
@RequiredArgsConstructor
@Slf4j
public class ChatRestController {
    private final ChatService chatService;
    private final TokenService tokenService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<?> getChats(@RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken) {
        String accessToken = tokenService.getToken(bearerToken);
        String userID = tokenService.extractSubject(accessToken);

        return ResponseEntity.ok(
                chatService.findChatsById(userService.findUserById(userID)).stream()
                        .map(chatService::convertToChatDTO)
                        .toList()
        );
    }

    @PostMapping
    public ResponseEntity<?> createChat(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken,
            @RequestBody ChatCreationRequest chatCreationRequest
    ) {
        String accessToken = tokenService.getToken(bearerToken);
        User user = userService.findUserById(tokenService.extractSubject(accessToken));
        chatService.createChat(chatCreationRequest.getUsers(), user, chatCreationRequest.getName());

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

    @RabbitListener(queues = "chat.queue")
    public void createUser(
            @RequestBody UserCreationForChatServiceRequest request
    ) {
        userService.createUser(request);
    }

}
