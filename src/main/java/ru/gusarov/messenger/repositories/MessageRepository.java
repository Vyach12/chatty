package ru.gusarov.messenger.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import ru.gusarov.messenger.models.Message;
import ru.gusarov.messenger.models.User;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Integer> {
    List<Message> findAllBySenderAndRecipient(User sender, User recipient);
}
