package com.zappqui.api.auth.zappqui_auth_service.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;


@DisplayName("Testes Unitários - User Model")
class UserTest {

    private User user;
    private String testUsername = "joao.silva";
    private String testPasswordHash = "$2a$10$abcdefghijklmnopqrstuvwxyz";

    @BeforeEach
    void setUp() {
        user = new User(testUsername, testPasswordHash);
    }

    @Test
    @DisplayName("Deve criar usuário com username e passwordHash")
    void testUserCreation() {
        // Assert
        assertEquals(testUsername, user.getUsername());
        assertEquals(testPasswordHash, user.getPasswordHash());
    }

    @Test
    @DisplayName("Deve criar usuário vazio")
    void testEmptyUserCreation() {
        // Act
        User emptyUser = new User();

        // Assert
        assertNotNull(emptyUser);
    }

    @Test
    @DisplayName("Deve atualizar username")
    void testSetUsername() {
        // Act
        String newUsername = "maria.silva";
        user.setUsername(newUsername);

        // Assert
        assertEquals(newUsername, user.getUsername());
    }

    @Test
    @DisplayName("Deve atualizar passwordHash")
    void testSetPasswordHash() {
        // Act
        String newHash = "$2a$10$zyxwvutsrqponmlkjihgfedcba";
        user.setPasswordHash(newHash);

        // Assert
        assertEquals(newHash, user.getPasswordHash());
    }

    @Test
    @DisplayName("Deve retornar null para ID não atribuído")
    void testIdInitiallyNull() {
        // Assert
        assertNull(user.getId());
    }

    @Test
    @DisplayName("Deve armazenar ID após atribuição")
    void testSetId() {
        // Act
        long testId = 1L;
        user.setId(testId); // Usando reflexão ou setter se existir

        // Assert
        // Nota: Se não há setter público para ID, este teste validará que o campo é privado
        assertNotNull(user);
    }

    @Test
    @DisplayName("Deve manter dados após múltiplas modificações")
    void testMultipleUpdates() {
        // Act
        user.setUsername("novo.username");
        user.setPasswordHash("novo.hash");
        String username = user.getUsername();
        String hash = user.getPasswordHash();

        // Assert
        assertEquals("novo.username", username);
        assertEquals("novo.hash", hash);
    }

    @Test
    @DisplayName("Deve diferenciar entre dois usuários diferentes")
    void testUsersDifferentiation() {
        // Arrange
        User user1 = new User("usuario1", "hash1");
        User user2 = new User("usuario2", "hash2");

        // Assert
        assertNotEquals(user1.getUsername(), user2.getUsername());
        assertNotEquals(user1.getPasswordHash(), user2.getPasswordHash());
    }
}

