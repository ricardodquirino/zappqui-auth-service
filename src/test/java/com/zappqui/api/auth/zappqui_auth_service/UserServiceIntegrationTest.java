package com.zappqui.api.auth.zappqui_auth_service;

import com.zappqui.api.auth.zappqui_auth_service.model.User;
import com.zappqui.api.auth.zappqui_auth_service.repository.UserRepository;
import com.zappqui.api.auth.zappqui_auth_service.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Testes de Integração - UserService")
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private String testUsername = "teste.usuario";
    private String testPassword = "SenhaTest@123";

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve criar usuário e persistir no banco de dados")
    void testCreateUserPersistence() {
        // Act
        User createdUser = userService.create(testUsername, testPassword);

        // Assert
        assertNotNull(createdUser);
        assertTrue(createdUser.getId() > 0);
        assertEquals(testUsername, createdUser.getUsername());
        assertNotNull(createdUser.getPasswordHash());

        // Verificar que foi salvo no banco
        assertTrue(userRepository.existsByUsername(testUsername));
    }

    @Test
    @DisplayName("Deve recuperar usuário criado do banco de dados")
    void testRetrieveCreatedUser() {
        // Arrange
        User createdUser = userService.create(testUsername, testPassword);

        // Act
        var retrievedUser = userRepository.findByUsername(testUsername);

        // Assert
        assertTrue(retrievedUser.isPresent());
        assertEquals(createdUser.getId(), retrievedUser.get().getId());
        assertEquals(testUsername, retrievedUser.get().getUsername());
    }

    @Test
    @DisplayName("Deve falhar ao criar usuário duplicado")
    void testCreateDuplicateUserFails() {
        // Arrange
        userService.create(testUsername, testPassword);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            userService.create(testUsername, "OutraSenha@123");
        });
    }

    @Test
    @DisplayName("Deve validar senha corretamente após persistência")
    void testPasswordValidationAfterPersistence() {
        // Arrange
        User createdUser = userService.create(testUsername, testPassword);
        var retrievedUser = userRepository.findByUsername(testUsername).orElseThrow();

        // Act
        boolean passwordMatches = userService.matches(testPassword, retrievedUser.getPasswordHash());

        // Assert
        assertTrue(passwordMatches);
    }

    @Test
    @DisplayName("Deve rejeitar senha incorreta após persistência")
    void testIncorrectPasswordRejectionAfterPersistence() {
        // Arrange
        userService.create(testUsername, testPassword);
        var retrievedUser = userRepository.findByUsername(testUsername).orElseThrow();

        // Act
        boolean passwordMatches = userService.matches("SenhaErrada@123", retrievedUser.getPasswordHash());

        // Assert
        assertFalse(passwordMatches);
    }

    @Test
    @DisplayName("Deve criar múltiplos usuários sem conflito")
    void testCreateMultipleUsers() {
        // Act
        User user1 = userService.create("usuario1", "Senha@111");
        User user2 = userService.create("usuario2", "Senha@222");
        User user3 = userService.create("usuario3", "Senha@333");

        // Assert
        assertEquals(3, userRepository.count());
        assertTrue(userRepository.existsByUsername("usuario1"));
        assertTrue(userRepository.existsByUsername("usuario2"));
        assertTrue(userRepository.existsByUsername("usuario3"));
    }

    @Test
    @DisplayName("Deve codificar senha diferente a cada criação")
    void testPasswordEncodingIsUnique() {
        // Arrange
        String password = "MesmaSenha@123";

        // Act
        User user1 = userService.create("usuario1", password);
        User user2 = userService.create("usuario2", password);

        // Assert
        assertNotEquals(user1.getPasswordHash(), user2.getPasswordHash());
    }

    @Test
    @DisplayName("Deve validar que senha codificada não é igual à original")
    void testEncodedPasswordIsDifferentFromOriginal() {
        // Act
        User createdUser = userService.create(testUsername, testPassword);

        // Assert
        assertNotEquals(testPassword, createdUser.getPasswordHash());
    }

    @Test
    @DisplayName("Deve existir usuário após criação")
    void testUserExistsAfterCreation() {
        // Act
        userService.create(testUsername, testPassword);

        // Assert
        assertTrue(userRepository.existsByUsername(testUsername));
    }
}

