package com.user_management.user_management.user.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class TokenPairDto {
    String accessToken;
    String refreshToken;
}
