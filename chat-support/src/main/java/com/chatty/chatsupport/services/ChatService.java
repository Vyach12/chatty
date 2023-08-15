package com.chatty.chatsupport.services;

import com.chatty.chatsupport.models.Chat;
import com.chatty.chatsupport.models.User;
import com.chatty.chatsupport.repositories.ChatRepository;
import com.chatty.chatsupport.dto.chats.ChatDTO;
import com.chatty.util.errors.logic.ErrorCode;
import com.chatty.util.exceptions.chat.ChatNotFoundException;
import com.chatty.util.exceptions.chat.NoAccessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import com.chatty.chatsupport.models.Message;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ChatRepository chatRepository;

    public Chat findChatById(String id) {
        return chatRepository.findChatById(id)
                .orElseThrow(() -> ChatNotFoundException.builder()
                        .errorCode(ErrorCode.CHAT_NOT_FOUND)
                        .errorDate(LocalDateTime.now())
                        .dataCausedError(id)
                        .errorMessage("Chat with id = " + id + " does not exist")
                        .build()
                );
    }

    public void saveMessage(Chat chat, Message message) {
        if(chat.getMessages() == null) {
            chat.setMessages(Collections.singletonList(message));
        } else {
            chat.getMessages().add(message);
        }
        chatRepository.save(chat);
    }

    public void save(Chat chat) {
        chatRepository.save(chat);
    }

    public List<Message> getMessagesFromChat(Chat chat, User user) {
        if(!chat.getUsers().contains(user)) {
            throw NoAccessException.builder()
                    .errorCode(ErrorCode.NO_ACCESS)
                    .errorMessage("No access to the chat")
                    .dataCausedError(chat)
                    .errorDate(LocalDateTime.now())
                    .build();
        }
        return chat.getMessages();
    }

    public List<Chat> findChatsById(User user) {
        log.info("user: " + user.getId() + " " + user.getUsername());
        return chatRepository.findChatByUsersIsContaining(user);
    }

    public ChatDTO convertToChatDTO(Chat chat) {
        log.info("chat: {}, {}", chat.getId(), chat.getName());
        if(chat.getMessages().isEmpty()) {
            return ChatDTO.builder()
                    .id(chat.getId())
                    .name(chat.getName())
                    .lastMessage(null)
                    .lastMessageOwner(null)
                    .build();
        }

        Message lastMessage = chat.getMessages().get(chat.getMessages().size() - 1);

        return ChatDTO.builder()
                .id(chat.getId())
                .name(chat.getName())
                .lastMessage(lastMessage.getText())
                .lastMessageOwner(lastMessage.getSender())
                .build();
    }

    public boolean canAccess(Chat chat, User user) {
        return chat.getUsers().stream().anyMatch(u -> u.getId().equals(user.getId()));
    }
}
