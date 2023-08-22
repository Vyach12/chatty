package com.chatty.usermanagement.repositories;

import com.chatty.usermanagement.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findUserById(String id);

    boolean existsUserById(String id);
}
