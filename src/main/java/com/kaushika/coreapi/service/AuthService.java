package com.kaushika.coreapi.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import com.kaushika.coreapi.model.Manager;
import com.kaushika.coreapi.model.User;
import com.kaushika.coreapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Value("${jwt.secret:defaultSecretKey12345678901234567890}")
    private String secret;

    @Value("${jwt.expiration:86400000}") // 24 hours in milliseconds
    private Long expiration;

    private Key getSigningKey() {
        byte[] keyBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String login(String username, String password) {
        User user = userRepository.findByUserName(username);

        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            // Check if the user is a Manager
            if (user instanceof Manager) {
                Manager manager = (Manager) user;
                return generateToken(manager);
            }
            return null; // Return null if user is not a Manager
        }
        return null;
    }

    private String generateToken(Manager manager) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", manager.getUserName());
        claims.put("role", "MANAGER");
        claims.put("managerName", manager.getUserName());
        claims.put("managerEmail", manager.getEmail());
        claims.put("userId", manager.getUserId());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(manager.getUserName())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Manager validateTokenAndGetManager(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // Verify that the token belongs to a manager
            if ("MANAGER".equals(claims.get("role"))) {
                User user = userRepository.findByUserName(claims.getSubject());
                if (user instanceof Manager) {
                    return (Manager) user;
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // Check if token is for a manager
            return "MANAGER".equals(claims.get("role"));
        } catch (Exception e) {
            return false;
        }
    }

    public Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean changePassword(String username, String oldPassword, String newPassword) {
        User user = userRepository.findByUserName(username);
        if (user instanceof Manager && passwordEncoder.matches(oldPassword, user.getPassword())) {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public void logout(String token) {
        // Implementation for token blacklisting would go here
    }

//    public Authentication getAuthentication(String token) {
//        // Decode token and get username
//        String username = getUsernameFromToken(token);
//        List<GrantedAuthority> authorities = getAuthoritiesFromToken(token);
//
//        return new UsernamePasswordAuthenticationToken(username, null, authorities);
//    }

}