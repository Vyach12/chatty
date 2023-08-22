package com.chatty.authentication.api.consumer;

import com.chatty.authentication.services.UserService;
import com.chatty.util.dto.NewUsernameRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

@Component
@RequiredArgsConstructor
public class AuthenticationConsumer {
    private final UserService userService;

    @RabbitListener(queues = "${rabbitmq.queue.username-change}")
    public void changeUsername(
            @RequestBody NewUsernameRequest request
    ) {
        userService.changeUsername(request);
    }
}
