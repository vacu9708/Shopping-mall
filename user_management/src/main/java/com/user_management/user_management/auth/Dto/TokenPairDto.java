package com.user_management.user_management.auth.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class TokenPairDto {
    String accessToken;
    String refreshToken;
}
