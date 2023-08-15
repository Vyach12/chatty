package com.chatty.authentication.services;

import com.chatty.authentication.models.Role;
import com.chatty.authentication.repositories.RoleRepository;
import com.chatty.util.errors.logic.ErrorCode;
import com.chatty.util.exceptions.authentication.RoleNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    public Role findByName(String name) {
        return roleRepository.findByName(name).orElseThrow(
                () -> RoleNotFoundException.builder()
                        .errorCode(ErrorCode.ROLE_NOT_FOUND)
                        .errorDate(LocalDateTime.now())
                        .dataCausedError(name)
                        .errorMessage("Does not exist role = " + name)
                        .build()
        );
    }
}
