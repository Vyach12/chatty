package com.chatty.chatsupport;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
		"com.chatty.chatsupport",
		"com.chatty.amqp"
})
public class ChatSupportApplication {
	public static void main(String[] args) {
		SpringApplication.run(ChatSupportApplication.class, args);
	}
}
