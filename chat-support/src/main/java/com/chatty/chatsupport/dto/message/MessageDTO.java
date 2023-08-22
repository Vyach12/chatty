package com.chatty.chatsupport.dto.message;

import lombok.Builder;

import java.util.List;

@Builder
public record MessageDTO(String text, List<String> images, List<String> videos, List<String> music){}
