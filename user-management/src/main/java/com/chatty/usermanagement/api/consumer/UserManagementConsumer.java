package com.chatty.usermanagement.api.consumer;

import com.chatty.usermanagement.services.UserService;
import com.chatty.util.dto.UserCreationForUserServiceRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

@Component
@RequiredArgsConstructor
public class UserManagementConsumer {
    private final UserService userService;
    @RabbitListener(queues = "${rabbitmq.queue.user-creation}")
    public void createUser(
            @RequestBody UserCreationForUserServiceRequest request
    ) {
        userService.createUser(request);
    }
}
