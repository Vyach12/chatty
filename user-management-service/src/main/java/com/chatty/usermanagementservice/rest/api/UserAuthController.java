package com.chatty.usermanagementservice.rest.api;

import com.chatty.usermanagementservice.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/users/auth")
@RequiredArgsConstructor
public class UserClaimsController {
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

}
