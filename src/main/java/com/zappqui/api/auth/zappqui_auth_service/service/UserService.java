// Java
package com.zappqui.api.auth.zappqui_auth_service.service;

import com.zappqui.api.auth.zappqui_auth_service.model.User;
import com.zappqui.api.auth.zappqui_auth_service.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository repo;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public User create(String username, String rawPassword) {
        if (repo.existsByUsername(username)) {
            throw new IllegalArgumentException("username já existe");
        }
        String hash = encoder.encode(rawPassword);
        User user = new User(username, hash);
        return repo.save(user);
    }

    public boolean matches(String rawPassword, String hash) {
        return encoder.matches(rawPassword, hash);
    }
}