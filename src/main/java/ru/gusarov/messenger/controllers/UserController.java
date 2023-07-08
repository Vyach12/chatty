package ru.gusarov.messenger.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.gusarov.messenger.dto.UpdatedUsernameDTO;
import ru.gusarov.messenger.dto.UserDTO;
import ru.gusarov.messenger.models.User;
import ru.gusarov.messenger.services.UserService;
import ru.gusarov.messenger.util.UserException;

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

    @PatchMapping("/changeUsername")
    public ResponseEntity<HttpStatus> changeUsername(
            @RequestBody UpdatedUsernameDTO updatedUsernameDTO,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        System.out.println(updatedUsernameDTO.getUsername());
        if(userService.existByUsername(updatedUsernameDTO.getUsername())) {
            throw new UserException(
                    "Person with username " + updatedUsernameDTO.getUsername() + " already exist"
            );
        }

        User user = userService.findByUsername(userDetails.getUsername());
        user.setUsername(updatedUsernameDTO.getUsername());
        userService.save(user);
        return ResponseEntity.ok(HttpStatus.OK);
    }
    //При необходимости методы для email и date_of_birth и password
    @PreAuthorize("hasAnyAuthority('ban_users')")
    @PatchMapping("{username}/ban")
    public ResponseEntity<HttpStatus> banUser(@PathVariable String username) {
        userService.changeEnabled(username);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('ban_users')")
    @PatchMapping("{username}/unban")
    public ResponseEntity<HttpStatus> unbanUser(@PathVariable String username) {
        userService.changeEnabled(username);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
