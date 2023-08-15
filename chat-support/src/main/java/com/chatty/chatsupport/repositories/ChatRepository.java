package com.chatty.chatsupport.repositories;

import com.chatty.chatsupport.models.Chat;
import com.chatty.chatsupport.models.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends MongoRepository<Chat, String> {

    Optional<Chat> findChatById(String id);

    List<Chat> findChatByUsersIsContaining(User user);
}
