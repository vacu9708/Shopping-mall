package com.user_management.user_management.user;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import jakarta.transaction.Transactional;

@Transactional
public interface UserRepository extends JpaRepository<UserEntity, UUID>{
    @Modifying
    @Query(value = "INSERT INTO users (username, password, email) VALUES (?1, ?2, ?3)", nativeQuery = true)
    void addUser(String username, String password, String email);

    @Query(value = "SELECT EXISTS(SELECT * FROM users WHERE username = ?1);", nativeQuery = true)
    Object existsByUserId(UUID uuid);

    @Query(value = "SELECT * FROM users WHERE user_id = ? LIMIT 1;", nativeQuery = true)
    UserEntity findByUserId(UUID userId);

    @Query(value = "SELECT * FROM users WHERE username = ? LIMIT 1;", nativeQuery = true)
    UserEntity findByUsername(String username);

    @Modifying
    @Query(value = "DELETE FROM users WHERE user_id = ?1 LIMIT 1;", nativeQuery = true)
    void deleteByUserId(UUID userId);
    void deleteByUsername(String username);
}
