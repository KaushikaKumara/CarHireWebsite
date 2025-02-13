package com.kaushika.coreapi.repository;

import com.kaushika.coreapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    // Keep existing method
    User findByUserName(String username);

    // Add new methods for JWT authentication
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    // Additional useful methods
    boolean existsByUserName(String username);

    Optional<User> findByUserNameOrEmail(String username, String email);
}