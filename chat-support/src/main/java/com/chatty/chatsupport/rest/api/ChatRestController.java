package com.chatty.chatsupport.rest.api;

import com.chatty.chatsupport.models.Chat;
import com.chatty.chatsupport.models.Message;
import com.chatty.chatsupport.models.User;
import com.chatty.chatsupport.repositories.UserRepository;
import com.chatty.chatsupport.services.ChatService;
import com.chatty.chatsupport.services.TokenService;
import com.chatty.chatsupport.services.UserService;
import com.chatty.chatsupport.util.dto.authentication.MessageResponse;
import com.chatty.chatsupport.util.dto.chats.ChatDTO;
import com.chatty.chatsupport.util.dto.chats.ChatRequest;
import com.chatty.chatsupport.util.dto.errors.logic.ErrorCode;
import com.chatty.chatsupport.util.dto.message.MessageDTO;
import com.chatty.chatsupport.util.dto.user.UserRequest;
import com.chatty.chatsupport.util.exceptions.user.IdOccupiedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;


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
        ObjectId user = new ObjectId(tokenService.extractSubject(accessToken));
        List<ChatDTO> chats = chatService.findChatsById(user);
        return ResponseEntity.ok(chats);
    }

    @PostMapping
    public ResponseEntity<?> createChat(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken,
            @RequestBody ChatRequest chatRequest
    ) {
        String accessToken = tokenService.getToken(bearerToken);
        chatRequest.getUsers().add(new ObjectId(tokenService.extractSubject(accessToken)));
        Chat chat = Chat.builder()
                .users(chatRequest.getUsers())
                .messages(Collections.emptyList())
                .build();
        chatService.save(chat);

        return ResponseEntity.ok(
                new MessageResponse("chat successfully created")
        );
    }


    @GetMapping("/message/{chatID}")
    public ResponseEntity<?> getMessages(
            @PathVariable String chatID,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken
    ) {
        String accessToken = tokenService.getToken(bearerToken);
        ObjectId userId = new ObjectId(tokenService.extractSubject(accessToken));
        List<Message> messages = chatService.getMessagesFromChat(new ObjectId(chatID), userId);
        return ResponseEntity.ok(messages);
    }


    @PostMapping("/message/{chatId}")
    public ResponseEntity<?> sendMessage(
            @PathVariable String chatId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken,
            @RequestBody MessageDTO messageDTO
    ) {

        Chat chat = chatService.findChatById(new ObjectId(chatId));
        String accessToken = tokenService.getToken(bearerToken);

        Message message = Message.builder()
                .id(UUID.randomUUID())
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

    @GetMapping("/users")
    public ResponseEntity<?> getUsers(){
        return ResponseEntity.ok(userService.findAll());
    }

    @PostMapping("/users/new")
    public void createUser(
            @RequestBody UserRequest request,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken) {
        log.info("Creating user {}", request.getUsername());

        String accessToken = tokenService.getToken(bearerToken);

        UUID id = UUID.fromString(tokenService.extractSubject(accessToken));

        if(userService.existById(id)){
            throw IdOccupiedException.builder()
                    .errorCode(ErrorCode.ID_IS_OCCUPIED)
                    .errorDate(LocalDateTime.now())
                    .dataCausedError(id)
                    .errorMessage("User with id = " + id + " already exists")
                    .build();
        }

        User user = User.builder()
                .id(id)
                .username(request.getUsername())
                .build();

        userService.save(user);
    }
}
