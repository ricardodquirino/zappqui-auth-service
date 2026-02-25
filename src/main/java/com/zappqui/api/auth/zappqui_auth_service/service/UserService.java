// Java
package com.zappqui.api.auth.zappqui_auth_service.service;

import com.zappqui.api.auth.zappqui_auth_service.model.User;
import com.zappqui.api.auth.zappqui_auth_service.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Pattern;

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

        validatePassword(rawPassword);

        String hash = encoder.encode(rawPassword);
        User user = new User(username, hash);
        return repo.save(user);
    }

    public boolean matches(String rawPassword, String hash) {
        return encoder.matches(rawPassword, hash);
    }

    /**
     * Validação centralizada de senha.
     * Regras:
     * - Não pode ser null ou vazia
     * - Mínimo 8 caracteres
     * - Pelo menos 1 letra maiúscula
     * - Pelo menos 1 letra minúscula
     * - Pelo menos 1 dígito
     * - Pelo menos 1 caractere especial (@$!%*?&)
     */
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}$");

    private void validatePassword(String password) {
        if (password == null || password.isBlank() ||
                !PASSWORD_PATTERN.matcher(password).matches()) {

            throw new IllegalArgumentException(
                    "A senha deve ter no mínimo 8 caracteres, incluindo letra maiúscula, minúscula, número e caractere especial (@$!%*?&)."
            );
        }
    }
}