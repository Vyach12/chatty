package com.chatty.usermanagementservice.rest.api;

import com.chatty.usermanagementservice.services.UserService;
import com.chatty.usermanagementservice.util.dto.authentication.RegisterRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/users/auth")
@RequiredArgsConstructor
@Slf4j
public class UserAuthController {
    private final UserService userService;
    @GetMapping("/claims/{username}")
    public ResponseEntity<?> getUserClaims(@PathVariable String username) {
        log.info("try to take user claims {}", username);
        return ResponseEntity.ok(
                userService.convertToUserClaims(
                        userService.findByUsername(username)
                )
        );
    }

    @PostMapping("/createUser")
    public ResponseEntity<?> createUser(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userService.createUser(request));
    }

}
