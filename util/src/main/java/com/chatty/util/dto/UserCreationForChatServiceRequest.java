package com.chatty.util.dto;

import lombok.Builder;

@Builder
public record UserCreationForChatServiceRequest(String id, String username){ }
