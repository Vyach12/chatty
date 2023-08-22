package com.chatty.chatsupport.api.consumer;

import com.chatty.chatsupport.services.UserService;
import com.chatty.util.dto.NewUsernameRequest;
import com.chatty.util.dto.UserCreationForChatServiceRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

@Component
@RequiredArgsConstructor
public class ChatSupportConsumer {

    private final UserService userService;
    @RabbitListener(queues = "${rabbitmq.queue.user-creation}")
    public void createUser(
            @RequestBody UserCreationForChatServiceRequest request
    ) {
        userService.createUser(request);
    }

    @RabbitListener(queues = "${rabbitmq.queue.username-change}")
    public void changeUsername(
            @RequestBody NewUsernameRequest request
    ) {
        userService.changeUsername(request);
    }
}
