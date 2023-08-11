package com.chatty.chatsupport.rest.api;

import com.chatty.chatsupport.models.Chat;
import com.chatty.chatsupport.models.Message;
import com.chatty.chatsupport.services.ChatService;
import com.chatty.chatsupport.services.TokenService;
import com.chatty.chatsupport.util.dto.authentication.MessageResponse;
import com.chatty.chatsupport.util.dto.message.MessageDTO;
import com.chatty.chatsupport.models.UserID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@RestController
@RequestMapping("/api/v1/chats")
@RequiredArgsConstructor
@Slf4j
public class ChatRestController {
    private final ChatService chatService;
    private final TokenService tokenService;
    private final WebClient.Builder webClientBuilder;

    @GetMapping("/{chatID}")
    public ResponseEntity<?> messenger(@PathVariable String chatID) {
        List<Message> messages = chatService.getMessagesFromChat(chatID);
        return ResponseEntity.ok(messages);
    }

    @PostMapping("/{username}")
    public ResponseEntity<?> createChat(
            @PathVariable String username,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken) {

        String accessToken = tokenService.getToken(bearerToken);

        String userID = webClientBuilder.build().get()
                .uri("http://user-management-service/api/v1/users/{username}/getID", username)
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        List<UserID> usersId = Arrays.asList(
                new UserID(userID),
                new UserID(tokenService.extractSubject(accessToken))
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

    @PatchMapping("/{chatId}")
    public ResponseEntity<?> sendMessage(
            @PathVariable String chatId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken,
            @RequestBody MessageDTO messageDTO) {

        Chat chat = chatService.findChatById(chatId);
        String accessToken = tokenService.getToken(bearerToken);

        Message message = Message.builder()
                .text(messageDTO.getText())
                .sender(tokenService.extractSubject(accessToken))
                .dateOfSending(LocalDateTime.now())
                .music(messageDTO.getMusic())
                .images(messageDTO.getImages())
                .videos(messageDTO.getVideos())
                .build();

        chatService.saveMessage(chat, message);
        return ResponseEntity.ok(new MessageResponse("Message successfully sent"));
    }
}
