package com.chatty.authentication.services;

import com.chatty.authentication.models.Role;
import com.chatty.authentication.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    public Role findByName(String name) {
        return roleRepository.findByName(name);
    }
}
