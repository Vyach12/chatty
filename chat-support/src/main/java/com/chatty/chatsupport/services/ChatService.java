package com.chatty.chatsupport.services;

import com.chatty.chatsupport.dto.chats.ChatInfoDTO;
import com.chatty.chatsupport.dto.message.MessageDTO;
import com.chatty.chatsupport.models.Chat;
import com.chatty.chatsupport.models.User;
import com.chatty.chatsupport.repositories.ChatRepository;
import com.chatty.chatsupport.dto.chats.ChatForPresentationDTO;
import com.chatty.util.errors.logic.ErrorCode;
import com.chatty.util.exceptions.chat.ChatNotFoundException;
import com.chatty.util.exceptions.chat.NoAccessChatException;
import com.chatty.util.exceptions.user.DuplicatedUsersException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.chatty.chatsupport.models.Message;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
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
            throw NoAccessChatException.builder()
                    .errorCode(ErrorCode.NO_ACCESS)
                    .errorMessage("No access to the chat")
                    .dataCausedError(chat)
                    .errorDate(LocalDateTime.now())
                    .build();
        }
        return chat.getMessages();
    }

    public List<Chat> findChatsById(User user) {
        return chatRepository.findChatByUsersIsContaining(user);
    }

    public ChatForPresentationDTO convertToChatDTO(Chat chat) {
        if(chat.getMessages().isEmpty()) {
            return ChatForPresentationDTO.builder()
                    .id(chat.getId())
                    .name(chat.getName())
                    .lastMessage(null)
                    .lastMessageOwner(null)
                    .build();
        }

        Message lastMessage = chat.getMessages().get(chat.getMessages().size() - 1);

        return ChatForPresentationDTO.builder()
                .id(chat.getId())
                .name(chat.getName())
                .lastMessage(lastMessage.getText())
                .lastMessageOwner(lastMessage.getSender().getUsername())
                .build();
    }

    public boolean canAccess(Chat chat, User user) {
        return chat.getUsers().stream().anyMatch(u -> u.getId().equals(user.getId()));
    }

    public void createChat(List<User> users, User creator, String name) {
        users.add(creator);
        List<User> uniqueUsers = users.stream().distinct().toList();
        if (uniqueUsers.size() != users.size()) {
            throw DuplicatedUsersException.builder()
                    .errorCode(ErrorCode.DUPLICATED_USERS)
                    .errorDate(LocalDateTime.now())
                    .dataCausedError(users)
                    .errorMessage("You cannot create chat with duplicated users")
                    .build();
        }

        Chat chat = Chat.builder()
                .name(name)
                .users(uniqueUsers)
                .messages(Collections.emptyList())
                .build();
        save(chat);
    }

    public void sendMessage(MessageDTO messageDTO, Chat chat, User sender) {
        if(!canAccess(chat, sender)) {
            throw NoAccessChatException.builder()
                    .errorCode(ErrorCode.NO_ACCESS)
                    .errorMessage("No access to the chat: " + chat.getName())
                    .dataCausedError(sender.getId())
                    .errorDate(LocalDateTime.now())
                    .build();
        }

        Message message = Message.builder()
                .id(UUID.randomUUID().toString())
                .text(messageDTO.text())
                .sender(sender)
                .dateOfSending(LocalDateTime.now())
                .music(messageDTO.music())
                .images(messageDTO.images())
                .videos(messageDTO.videos())
                .build();

        saveMessage(chat, message);

    }

    public ChatInfoDTO getChatInfo(Chat chat, User user) {
        if(!canAccess(chat,user)) {
            throw NoAccessChatException.builder()
                    .errorCode(ErrorCode.NO_ACCESS)
                    .errorMessage("No access to the chat: " + chat.getId())
                    .dataCausedError(user.getId())
                    .errorDate(LocalDateTime.now())
                    .build();
        }
        return ChatInfoDTO.builder()
                .id(chat.getId())
                .name(chat.getName())
                .users(chat.getUsers())
                .build();
    }
}
