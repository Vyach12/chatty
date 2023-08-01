package com.chatty.usermanagementservice.repositories;

import com.chatty.usermanagementservice.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Role findByName(String name);
}
