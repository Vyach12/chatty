package ru.gusarov.messenger.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.gusarov.messenger.models.Role;
import ru.gusarov.messenger.repositories.RoleRepository;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    public Role findByName(String name) {
        return roleRepository.findByName(name);
    }
}
