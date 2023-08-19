package com.order_management.order_management.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class EmailDto {
    String to;
    String subject;
    String text;
}
