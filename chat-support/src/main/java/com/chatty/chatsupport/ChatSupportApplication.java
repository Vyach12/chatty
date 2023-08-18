package com.chatty.chatsupport;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = {
		"com.chatty.chatsupport",
		"com.chatty.amqp"
})
public class ChatSupportApplication {
	public static void main(String[] args) {
		SpringApplication.run(ChatSupportApplication.class, args);
	}

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}
}
