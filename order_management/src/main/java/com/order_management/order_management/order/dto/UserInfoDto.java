package com.order_management.order_management.order.dto;

import java.util.UUID;

import lombok.Getter;

@Getter
public class UserInfoDto {
    UUID userId;
    String username;
    String email;
}
