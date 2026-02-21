// Java
package com.zappqui.api.auth.zappqui_auth_service.service;

import com.zappqui.api.auth.zappqui_auth_service.repository.UserRepository;
import com.zappqui.api.auth.zappqui_auth_service.security.JwtUtil;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository repo;
    private final UserService userService;

    public AuthService(UserRepository repo, UserService userService) {
        this.repo = repo;
        this.userService = userService;
    }

    public boolean authenticate(String username, String password) {

        return repo.findByUsername(username)
                .map(u -> userService.matches(password, u.getPasswordHash()))
                .orElse(false);
    }

    public String issueToken(String subject, long ttlSeconds) {
        return JwtUtil.generateToken(subject, ttlSeconds);
    }

    public boolean validateToken(String token) {
        return JwtUtil.isValid(token);
    }

    public String getSubject(String token) {
        return JwtUtil.getSubject(token);
    }
}