package ru.gusarov.messenger.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.gusarov.messenger.models.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Role findByName(String name);
}
