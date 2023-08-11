package com.chatty.usermanagement.rest.api;

import com.chatty.usermanagement.models.User;
import com.chatty.usermanagement.services.TokenService;
import com.chatty.usermanagement.services.UserService;
import com.chatty.usermanagement.util.dto.user.UserInfo;
import com.chatty.usermanagement.util.dto.errors.logic.ErrorCode;
import com.chatty.usermanagement.util.exceptions.user.IdOccupiedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;


@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@CrossOrigin
@Slf4j
public class UserRestController {
    private final UserService userService;
    private final TokenService tokenService;


    @GetMapping("{username}")
    public ResponseEntity<?> getUser(@PathVariable String username) {
        log.info("try to take user {}", username);
        return ResponseEntity.ok(userService.convertToUserDTO(
                userService.findByUsername(username))
        );
    }

    @GetMapping("{username}/getID")
    public ResponseEntity<?> getUserID(@PathVariable String username) {
        return ResponseEntity.ok(userService.findByUsername(username).getId());
    }


    @PostMapping("new")
    public void newUser(
            @RequestBody UserInfo request,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken
    ) {
        log.info("Creating user {}", request.getUsername());
        bearerToken = bearerToken.substring(7);
        UUID id = UUID.fromString(tokenService.extractSubject(bearerToken));
        if(userService.existById(id)){
            throw IdOccupiedException.builder()
                    .errorCode(ErrorCode.ID_IS_OCCUPIED)
                    .errorDate(LocalDateTime.now())
                    .dataCausedError(id)
                    .errorMessage("User with id = " + id + " already exists")
                    .build();
        }
        User user = User.builder()
                .id(id)
                .username(request.getUsername())
                .email(request.getEmail())
                .dateOfBirth(request.getDateOfBirth())
                .build();
        userService.save(user);
    }
}
