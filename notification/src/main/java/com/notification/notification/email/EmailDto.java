package com.notification.notification.email;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailDto {
    String to;
    String subject;
    String text;
}
