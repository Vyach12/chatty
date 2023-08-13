package com.chatty.chatsupport.services;

import com.chatty.chatsupport.models.Chat;
import com.chatty.chatsupport.repositories.ChatRepository;
import com.chatty.chatsupport.util.dto.chats.ChatDTO;
import com.chatty.chatsupport.util.exceptions.chat.ChatNotFoundException;
import com.chatty.chatsupport.util.exceptions.chat.NoAccessException;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
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

    public Chat findChatById(ObjectId id) {
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

    public List<Message> getMessagesFromChat(ObjectId chatID, ObjectId user) {
        Chat chat = findChatById(chatID);
        if(!chat.getUsers().contains(user)) {
            throw NoAccessException.builder()
                    .errorCode(ErrorCode.NO_ACCESS)
                    .errorMessage("No access to the chat")
                    .dataCausedError(chatID)
                    .errorDate(LocalDateTime.now())
                    .build();
        }
        return chat.getMessages();
    }

    public List<ChatDTO> findChatsById(ObjectId id) {
        return chatRepository.findChatByUsersIsContaining(id).stream()
                .map(this::convertToChatDTO)
                .toList();
    }

    private ChatDTO convertToChatDTO(Chat chat) {
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
}
