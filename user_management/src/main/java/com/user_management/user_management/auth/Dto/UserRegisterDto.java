package com.user_management.user_management.auth.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Data
public class UserRegisterDto  {
    String username;
    String password;
    String email;
}