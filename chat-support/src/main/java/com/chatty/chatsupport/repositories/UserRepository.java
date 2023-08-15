package com.chatty.chatsupport.repositories;

import com.chatty.chatsupport.models.User;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends MongoRepository<User, ObjectId> {
    Optional<User> findUserById(String id);
    @NonNull
    List<User> findAll();

    boolean existsById(String id);
}
