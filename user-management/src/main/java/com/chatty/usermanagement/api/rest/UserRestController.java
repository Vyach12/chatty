package com.chatty.usermanagement.api.rest;

import com.chatty.usermanagement.dto.ChangeUsernameRequest;
import com.chatty.util.dto.MessageResponse;
import com.chatty.usermanagement.models.User;
import com.chatty.usermanagement.services.TokenService;
import com.chatty.usermanagement.services.UserService;
import com.chatty.util.dto.UserCreationForUserServiceRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@CrossOrigin
@Slf4j
public class UserRestController {
    private final UserService userService;
    private final TokenService tokenService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable String id) {
        return ResponseEntity.ok(userService.findUserById(id));
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    @PatchMapping("/username")
    public ResponseEntity<?> changeUsername(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken,
            @Valid @RequestBody ChangeUsernameRequest request
    ) {
        String accessToken = tokenService.getToken(bearerToken);

        User user = userService.findUserById(tokenService.extractSubject(accessToken));

        userService.changeUsername(user, request.username());
        return ResponseEntity.ok(new MessageResponse("Username successfully changed"));
    }
}
