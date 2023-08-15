package com.chatty.authentication.repositories;

import com.chatty.authentication.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findById(UUID id);

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);
    boolean existsByEmail(String username);

}
