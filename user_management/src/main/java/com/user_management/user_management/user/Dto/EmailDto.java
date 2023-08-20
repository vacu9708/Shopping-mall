package com.user_management.user_management.user.Dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class EmailDto {
    String to;
    String subject;
    String text;
}
