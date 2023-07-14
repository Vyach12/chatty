package ru.gusarov.messenger.rest.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.gusarov.messenger.util.dto.errors.logic.ErrorCode;
import ru.gusarov.messenger.util.dto.user.UpdatedUsernameDTO;
import ru.gusarov.messenger.util.dto.user.UserDTO;
import ru.gusarov.messenger.models.User;
import ru.gusarov.messenger.services.UserService;
import ru.gusarov.messenger.util.exceptions.user.UsernameOccupiedException;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserRestController {
    private final UserService userService;

    @GetMapping("{username}")
    public UserDTO getUser(@PathVariable String username) {
        return userService.convertToUserDTO(
                userService.findByUsername(username)
        );
    }

    @PatchMapping("/changeUsername")
    public HttpStatus changeUsername(
            @RequestBody UpdatedUsernameDTO updatedUsernameDTO,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        if(userService.existByUsername(updatedUsernameDTO.getUsername())) {
            throw UsernameOccupiedException.builder()
                    .errorCode(ErrorCode.USERNAME_IS_OCCUPIED)
                    .errorDate(LocalDateTime.now())
                    .dataCausedError(updatedUsernameDTO)
                    .errorMessage("User with username = " + updatedUsernameDTO.getUsername() + " already exist")
                    .build();
        }

        User user = userService.findByUsername(userDetails.getUsername());
        user.setUsername(updatedUsernameDTO.getUsername());
        userService.save(user);

        return HttpStatus.OK;
    }
    //При необходимости методы для email и date_of_birth и password
    @PreAuthorize("hasAnyAuthority('ban_users')")
    @PatchMapping("{username}/ban")
    public HttpStatus banUser(@PathVariable String username) {
        userService.changeEnabled(username);
        return HttpStatus.OK;
    }

    @PreAuthorize("hasAnyAuthority('ban_users')")
    @PatchMapping("{username}/unban")
    public HttpStatus unbanUser(@PathVariable String username) {
        userService.changeEnabled(username);
        return HttpStatus.OK;
    }
}
