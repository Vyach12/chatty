package com.chatty.chatsupport.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.chatty.chatsupport.models.Message;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Integer> {
    List<Message> findAllBySenderAndRecipient(User sender, User recipient);
}
