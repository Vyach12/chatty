package com.chatty.chatsupport.services;

import com.chatty.chatsupport.repositories.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import com.chatty.chatsupport.util.dto.errors.logic.ErrorCode;
import com.chatty.chatsupport.util.dto.message.MessageDTO;
import com.chatty.chatsupport.models.Message;
import com.chatty.chatsupport.util.exceptions.message.MessageNotFoundException;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final ModelMapper modelMapper;
    private final MessageRepository messageRepository;
    private final UserService userService;

    public List<Message> findMessagesByPeople(User first, User second) {
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
    }
}
