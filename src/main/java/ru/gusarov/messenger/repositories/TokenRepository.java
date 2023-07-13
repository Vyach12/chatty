package ru.gusarov.messenger.repositories;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.gusarov.messenger.models.Token;
import ru.gusarov.messenger.models.User;

public interface TokenRepository extends JpaRepository<Token, Integer> {

    List<Token> findAllTokenByUser_username(String username);

    Optional<Token> findByToken(String token);
}
