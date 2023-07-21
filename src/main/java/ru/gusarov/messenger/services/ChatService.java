package ru.gusarov.messenger.services;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.gusarov.messenger.models.Chat;
import ru.gusarov.messenger.models.Message;
import ru.gusarov.messenger.repositories.ChatRepository;
import ru.gusarov.messenger.util.dto.errors.logic.ErrorCode;
import ru.gusarov.messenger.util.exceptions.chat.ChatNotFoundException;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ModelMapper modelMapper;
    private final ChatRepository chatRepository;
    private final UserService userService;

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

    }

/*    public List<Message> findMessagesByPeople(User first, User second) {
        List<Message> list = messageRepository.findAllBySenderAndRecipient(first, second);
        list.addAll(messageRepository.findAllBySenderAndRecipient(second, first));
        list.sort(Comparator.comparing(Message::getDateOfSending));
        return list;
    }

    public void save(Message message) {
        messageRepository.save(message);
    }

    public void update(int idMessage, String newText) {
        Optional<Message> message = messageRepository.findById(idMessage);
        if(message.isEmpty()) {
            throw MessageNotFoundException.builder()
                    .errorCode(ErrorCode.MESSAGE_NOT_FOUND)
                    .errorDate(LocalDateTime.now())
                    .dataCausedError(idMessage)
                    .errorMessage("Message with id = " + idMessage + " does not exist")
                    .build();
        }
        message.get().setDateOfChange(LocalDateTime.now());
        message.get().setMessage(newText);
        messageRepository.save(message.get());
    }

    public MessageDTO convertToMessageDTO(Message message) {
        MessageDTO messageDTO = modelMapper.map(message, MessageDTO.class);
        messageDTO.setSender(userService.convertToUserForMessageDTO(message.getSender()));
        messageDTO.setRecipient(userService.convertToUserForMessageDTO(message.getRecipient()));
        return messageDTO;
    }*/
}
