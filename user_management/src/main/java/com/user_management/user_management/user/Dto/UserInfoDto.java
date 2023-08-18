package com.user_management.user_management.user.Dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserInfoDto {
    UUID userId;
    String username;
    String email;
}