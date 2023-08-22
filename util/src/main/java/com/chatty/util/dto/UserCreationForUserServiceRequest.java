package com.chatty.util.dto;

import lombok.Builder;

import java.util.Date;

@Builder
public record UserCreationForUserServiceRequest(String id, String username, String email, Date dateOfBirth){}
