package com.chatty.usermanagement.rest.api;

import com.chatty.usermanagement.services.UserService;
import com.chatty.util.dto.UserCreationForUserServiceRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@CrossOrigin
@Slf4j
public class UserRestController {
    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable String id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    @RabbitListener(queues = "user.queue")
    public void createUser(
            @RequestBody UserCreationForUserServiceRequest request
    ) {
        userService.createUser(request);
    }
}
