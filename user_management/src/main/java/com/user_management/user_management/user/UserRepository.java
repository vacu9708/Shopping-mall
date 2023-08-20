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

    boolean existsByUserId(UUID uuid);

    // @Query(value = "SELECT * FROM users WHERE user_id = ?;", nativeQuery = true)
    UserEntity findByUserId(UUID userId);

    UserEntity findByUsername(String username);

    void deleteByUserId(UUID userId);
}
