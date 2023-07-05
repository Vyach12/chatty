package ru.gusarov.messenger.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.gusarov.messenger.dto.MessageDTO;
import ru.gusarov.messenger.models.User;
import ru.gusarov.messenger.services.MessageService;
import ru.gusarov.messenger.services.UserService;
import ru.gusarov.messenger.utils.UserErrorResponse;
import ru.gusarov.messenger.utils.UserException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/message")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;
    private final UserService userService;

    @GetMapping("/{username}")
    public ResponseEntity<List<MessageDTO>> messenger(@PathVariable String username, @AuthenticationPrincipal UserDetails userDetails) {
        User recipient = userService.findByUsername(username);
        User sender = userService.findByUsername(userDetails.getUsername());

        List<MessageDTO> list = messageService
                .findMessagesBySenderAndRecipient(sender, recipient).stream()
                        .map(messageService::convertToMessageDTO)
                        .toList();
        return ResponseEntity.ok(list);
    }

    @ExceptionHandler
    private ResponseEntity<UserErrorResponse> handleException(UserException e) {
        UserErrorResponse response = new UserErrorResponse(
                e.getMessage(),
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
