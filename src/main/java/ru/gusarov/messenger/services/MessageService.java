package ru.gusarov.messenger.services;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.gusarov.messenger.dto.MessageDTO;
import ru.gusarov.messenger.models.Message;
import ru.gusarov.messenger.models.User;
import ru.gusarov.messenger.repositories.MessageRepository;
import ru.gusarov.messenger.utils.MessageException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final ModelMapper modelMapper;
    private final MessageRepository messageRepository;
    private final UserService userService;

    public List<Message> findMessagesBySenderAndRecipient(User sender, User recipient) {
        return messageRepository.findAllBySenderAndRecipient(sender, recipient);
    }

    public Message findById(int id) {
        return messageRepository.findById(id).orElseThrow(() -> new MessageException("Message with id = " + id + " does not exist"));
    }

    public void save(Message message) {
        messageRepository.save(message);
    }

    public void update(Message message, String newText) {
        message.setDateOfChange(LocalDateTime.now());
        message.setMessage(newText);
        messageRepository.save(message);
    }

    public MessageDTO convertToMessageDTO(Message message) {
        MessageDTO messageDTO = modelMapper.map(message, MessageDTO.class);
        messageDTO.setSender(userService.convertToUserForMessageDTO(message.getSender()));
        messageDTO.setRecipient(userService.convertToUserForMessageDTO(message.getRecipient()));
        return messageDTO;
    }
}
