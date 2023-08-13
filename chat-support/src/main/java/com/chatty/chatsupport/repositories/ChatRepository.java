package com.chatty.chatsupport.repositories;

import com.chatty.chatsupport.models.Chat;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends MongoRepository<Chat, ObjectId> {

    Optional<Chat> findChatById(ObjectId id);

    List<Chat> findChatByUsersIsContaining(ObjectId user);
}
