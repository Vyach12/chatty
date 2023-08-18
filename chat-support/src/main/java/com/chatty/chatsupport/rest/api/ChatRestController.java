package com.chatty.chatsupport.rest.api;

import com.chatty.chatsupport.models.Chat;
import com.chatty.chatsupport.models.Message;
import com.chatty.chatsupport.models.User;
import com.chatty.chatsupport.services.ChatService;
import com.chatty.chatsupport.services.TokenService;
import com.chatty.chatsupport.services.UserService;
import com.chatty.chatsupport.dto.chats.ChatDTO;
import com.chatty.chatsupport.dto.chats.ChatCreationRequest;
import com.chatty.chatsupport.dto.message.MessageDTO;
import com.chatty.util.dto.MessageResponse;
import com.chatty.util.dto.UserCreationForChatServiceRequest;
import com.chatty.util.errors.logic.ErrorCode;
import com.chatty.util.exceptions.chat.NoAccessException;
import com.chatty.util.exceptions.user.DuplicatedUsersException;
import com.chatty.util.exceptions.user.IdOccupiedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.http.HttpHeaders;
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
        String userID = tokenService.extractSubject(accessToken);

        List<ChatDTO> chats = chatService.findChatsById(userService.findUserById(userID)).stream()
                .map(chatService::convertToChatDTO)
                .toList();;
        return ResponseEntity.ok(chats);
    }

    @PostMapping
    public ResponseEntity<?> createChat(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken,
            @RequestBody ChatCreationRequest chatCreationRequest
    ) {
        String accessToken = tokenService.getToken(bearerToken);
        User user = userService.findUserById(tokenService.extractSubject(accessToken));

        List<User> users = chatCreationRequest.getUsers();
        users.add(user);

        List<User> uniqueUsers = users.stream().distinct().toList();
        if (uniqueUsers.size() != chatCreationRequest.getUsers().size()) {
            throw DuplicatedUsersException.builder()
                    .errorCode(ErrorCode.DUPLICATED_USERS)
                    .errorDate(LocalDateTime.now())
                    .dataCausedError(users)
                    .errorMessage("You cannot create chat with duplicated users")
                    .build();
        }

        Chat chat = Chat.builder()
                .name(chatCreationRequest.getName())
                .users(uniqueUsers)
                .messages(Collections.emptyList())
                .build();

        chatService.save(chat);

        return ResponseEntity.ok(new MessageResponse("chat successfully created"));
    }

    @GetMapping("/{chatID}")
    public ResponseEntity<?> getInfo(
            @PathVariable String chatID,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken
    ) {
        String accessToken = tokenService.getToken(bearerToken);
        String userID = tokenService.extractSubject(accessToken);

        User user = userService.findUserById(userID);
        Chat chat = chatService.findChatById(chatID);

        if(!chatService.canAccess(chat,user)) {
            throw NoAccessException.builder()
                    .errorCode(ErrorCode.NO_ACCESS)
                    .errorMessage("No access to the chat")
                    .dataCausedError(chat.getId())
                    .errorDate(LocalDateTime.now())
                    .build();
        }

        return ResponseEntity.ok(chat);
    }

    @GetMapping("/message/{chatID}")
    public ResponseEntity<?> getMessages(
            @PathVariable String chatID,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken
    ) {
        String accessToken = tokenService.getToken(bearerToken);
        String userID = tokenService.extractSubject(accessToken);
        List<Message> messages = chatService.getMessagesFromChat(chatService.findChatById(chatID), userService.findUserById(userID));
        return ResponseEntity.ok(messages);
    }

    @PostMapping("/message/{chatId}")
    public ResponseEntity<?> sendMessage(
            @PathVariable String chatId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken,
            @RequestBody MessageDTO messageDTO
    ) {

        Chat chat = chatService.findChatById(chatId);
        String accessToken = tokenService.getToken(bearerToken);

        Message message = Message.builder()
                .id(UUID.randomUUID().toString())
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

    @RabbitListener(queues = "chat.queue")
    public void createUser(
            @RequestBody UserCreationForChatServiceRequest request) {

        if(userService.existById(request.getId())){
            throw IdOccupiedException.builder()
                    .errorCode(ErrorCode.ID_IS_OCCUPIED)
                    .errorDate(LocalDateTime.now())
                    .dataCausedError(request.getId())
                    .errorMessage("User with id = " + request.getId() + " already exists")
                    .build();
        }

        User user = User.builder()
                .id(request.getId())
                .username(request.getUsername())
                .build();

        userService.save(user);
    }

}
