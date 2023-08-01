package com.chatty.usermanagementservice.services;

import com.chatty.usermanagementservice.models.Role;
import com.chatty.usermanagementservice.repositories.RoleRepository;
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
