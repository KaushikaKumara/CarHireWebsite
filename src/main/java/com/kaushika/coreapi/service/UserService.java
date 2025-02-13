package com.kaushika.coreapi.service;

import com.kaushika.coreapi.model.*;
import com.kaushika.coreapi.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        User user = userRepository.findByUserNameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username/email: " + usernameOrEmail));

        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }

    public User saveUser(User userDetails) {
        // Check if username or email already exists
        if (userRepository.existsByUserName(userDetails.getUserName())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(userDetails.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Encode password
        String encodedPassword = passwordEncoder.encode(userDetails.getPassword());

        // Create appropriate user type based on role
        User user = createUserByRole(userDetails.getRole());

        // Set common properties
        user.setUserName(userDetails.getUserName());
        user.setEmail(userDetails.getEmail());
        user.setPassword(encodedPassword);
        user.setPhoneNumber(userDetails.getPhoneNumber());
        user.setAddress(userDetails.getAddress());
        user.setRole(userDetails.getRole());

        // Set role-specific properties
        switch (userDetails.getRole()) {
            case CUSTOMER:
                ((Customer) user).setCustomerNumber(generateCustomerNumber());
                break;
            case STAFF:
                ((Staff) user).setEmployeeId(generateEmployeeId());
                break;
            case MANAGER:
                ((Manager) user).setDepartmentCode(generateDepartmentCode());
                break;
        }

        return userRepository.save(user);
    }

    private User createUserByRole(UserRole role) {
        switch (role) {
            case CUSTOMER:
                return new Customer();
            case STAFF:
                return new Staff();
            case MANAGER:
                return new Manager();
            default:
                throw new IllegalArgumentException("Invalid role: " + role);
        }
    }

    // Helper methods to generate IDs
    private String generateCustomerNumber() {
        return "CUST-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private String generateEmployeeId() {
        return "EMP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private String generateDepartmentCode() {
        return "DEPT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public User updateUser(User user) {
        User existingUser = userRepository.findById(user.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Only encode password if it's been changed
        if (user.getPassword() != null && !user.getPassword().equals(existingUser.getPassword())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        return userRepository.save(user);
    }

    public Optional<User> getUserById(Integer id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void deleteUser(Integer id) {
        userRepository.deleteById(id);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    // Method to get current authenticated user
    public User getCurrentUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    // Method to change password
    public void changePassword(String email, String oldPassword, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // Verify old password
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        // Set and encode new password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}