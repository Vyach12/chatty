package ru.gusarov.messenger.repositories;

import org.hibernate.mapping.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import ru.gusarov.messenger.models.Chat;

import java.util.Optional;

@Repository
public interface ChatRepository extends MongoRepository<Chat, String> {
    @Query(value = "{ '_id' : ?0 }", fields = "{ 'name': 1,'users' : 1, 'messages' : 1 }")
    Optional<Chat> findByIdWithUsersAndMessages(String id);
}
