package com.chatty.util.dto;

import lombok.Builder;

@Builder
public record NewUsernameRequest(String id, String username) {}
