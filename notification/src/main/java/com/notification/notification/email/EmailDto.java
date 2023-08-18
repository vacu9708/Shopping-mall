package com.notification.notification.email;

import lombok.Builder;

import lombok.Getter;

@Getter
@Builder
public class EmailDto {
    String to;
    String subject;
    String text;
}
