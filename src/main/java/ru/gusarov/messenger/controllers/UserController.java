package ru.gusarov.messenger.controllers;

import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.gusarov.messenger.dto.UserDTO;
import ru.gusarov.messenger.models.User;
import ru.gusarov.messenger.services.UserService;


@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("{username}")
    public ResponseEntity<UserDTO> getUser(@PathVariable String username) {
        UserDTO userDTO = userService.convertToUserDTO(
                userService.findByUsername(username)
        );
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('ban_users')")
    @PatchMapping("{username}/ban")
    public ResponseEntity<HttpStatus> banUser(@PathVariable String username) {
        User user = userService.findByUsername(username);
        user.setEnabled(false);
        userService.save(user);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
