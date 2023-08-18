package com.chatty.util.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCreationForUserServiceRequest {
    private String id;
    private String username;
    private String email;
    private Date dateOfBirth;
}
