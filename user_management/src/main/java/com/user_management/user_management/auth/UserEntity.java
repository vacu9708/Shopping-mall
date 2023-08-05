package com.user_management.user_management.auth;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity(name = "users")
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity  {
    @Id
    // @GeneratedValue
    // @Column(columnDefinition = "BINARY(16)")
    UUID userId;
    String username;
    String password;
    String email;
}