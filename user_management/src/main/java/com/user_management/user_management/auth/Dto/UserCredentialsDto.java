package com.user_management.user_management.auth.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserCredentialsDto  {
    String username;
    String password;
}