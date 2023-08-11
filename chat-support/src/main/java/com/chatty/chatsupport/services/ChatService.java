package com.chatty.chatsupport.services;

import com.chatty.chatsupport.models.Chat;
import com.chatty.chatsupport.repositories.ChatRepository;
import com.chatty.chatsupport.util.dto.message.MessageDTO;
import com.chatty.chatsupport.util.exceptions.chat.ChatNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import com.chatty.chatsupport.util.dto.errors.logic.ErrorCode;
import com.chatty.chatsupport.models.Message;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ModelMapper modelMapper;
    private final ChatRepository chatRepository;

    public Chat findChatById(String id) {
        return chatRepository.findByIdWithUsersAndMessages(id)
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

    public List<Message> getMessagesFromChat(String chatID) {
        return findChatById(chatID).getMessages();
    }
}
