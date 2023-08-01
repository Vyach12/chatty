package ru.gusarov.messenger.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.gusarov.messenger.models.Token;

public interface TokenRepository extends JpaRepository<Token, Integer> {

    Optional<Token> findByToken(String token);
}
