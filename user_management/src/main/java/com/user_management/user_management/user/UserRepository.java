package com.user_management.user_management.user;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<UserEntity, UUID>{
    @Modifying
    @Query(value = "INSERT INTO users (username, password, email) VALUES (?1, ?2, ?3)", nativeQuery = true)
    void addUser(String username, String password, String email);
    UserEntity findByUsername(String username);
    void deleteByUsername(String username);
}
