package com.chatty.usermanagementservice.rest.api;

import com.chatty.usermanagementservice.models.User;
import com.chatty.usermanagementservice.services.TokenService;
import com.chatty.usermanagementservice.services.UserService;
import com.chatty.usermanagementservice.util.dto.user.UserInfo;
import com.chatty.usermanagementservice.util.dto.errors.logic.ErrorCode;
import com.chatty.usermanagementservice.util.exceptions.user.IdOccupiedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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

    @PostMapping("new")
    public String newUser(
            @RequestBody UserInfo request,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken
    ) {
        log.info("****************************************");
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
        return "true";
    }
}
