package com.zappqui.api.auth.zappqui_auth_service.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


@DisplayName("Testes Unitários - JwtUtil")
class JwtUtilTest {

    @Test
    @DisplayName("Deve gerar token JWT válido")
    void testGenerateTokenSuccess() {
        // Arrange
        String subject = "joao.silva";
        long ttlSeconds = 3600;

        // Act
        String token = JwtUtil.generateToken(subject, ttlSeconds);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.contains("."));
        assertEquals(3, token.split("\\.").length); // JWT tem 3 partes separadas por ponto
    }

    @Test
    @DisplayName("Deve gerar tokens válidos para mesmo subject")
    void testGenerateTokensForSameSubject() {
        // Arrange
        String subject = "joao.silva";

        // Act
        String token1 = JwtUtil.generateToken(subject, 3600);
        String token2 = JwtUtil.generateToken(subject, 3600);

        // Assert
        assertNotNull(token1);
        assertNotNull(token2);
        assertTrue(JwtUtil.isValid(token1));
        assertTrue(JwtUtil.isValid(token2));
        assertEquals(subject, JwtUtil.getSubject(token1));
        assertEquals(subject, JwtUtil.getSubject(token2));
    }

    @Test
    @DisplayName("Deve validar token gerado como válido")
    void testValidateTokenSuccess() {
        // Arrange
        String token = JwtUtil.generateToken("joao.silva", 3600);

        // Act
        boolean isValid = JwtUtil.isValid(token);

        // Assert
        assertTrue(isValid);
    }

    @Test
    @DisplayName("Deve rejeitar token inválido")
    void testValidateInvalidToken() {
        // Arrange
        String invalidToken = "token.invalido.aqui";

        // Act
        boolean isValid = JwtUtil.isValid(invalidToken);

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Deve rejeitar token vazio")
    void testValidateEmptyToken() {
        // Arrange
        String emptyToken = "";

        // Act
        boolean isValid = JwtUtil.isValid(emptyToken);

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Deve retornar false para token null")
    void testValidateNullToken() {
        // Act & Assert
        assertFalse(JwtUtil.isValid(null));
    }

    @Test
    @DisplayName("Deve extrair subject de token válido")
    void testGetSubjectSuccess() {
        // Arrange
        String expectedSubject = "joao.silva";
        String token = JwtUtil.generateToken(expectedSubject, 3600);

        // Act
        String subject = JwtUtil.getSubject(token);

        // Assert
        assertEquals(expectedSubject, subject);
    }

    @Test
    @DisplayName("Deve retornar null ao extrair subject de token inválido")
    void testGetSubjectInvalidToken() {
        // Arrange
        String invalidToken = "token.invalido.aqui";

        // Act
        String subject = JwtUtil.getSubject(invalidToken);

        // Assert
        assertNull(subject);
    }

    @Test
    @DisplayName("Deve retornar null ao extrair subject de token vazio")
    void testGetSubjectEmptyToken() {
        // Arrange
        String emptyToken = "";

        // Act
        String subject = JwtUtil.getSubject(emptyToken);

        // Assert
        assertNull(subject);
    }

    @Test
    @DisplayName("Deve respeitar TTL ao gerar token")
    void testGenerateTokenWithDifferentTTL() {
        // Arrange
        String token = JwtUtil.generateToken("joao.silva", 1);

        // Act & Assert
        assertTrue(JwtUtil.isValid(token));
    }

    @Test
    @DisplayName("Deve usar TTL padrão quando TTL <= 0")
    void testGenerateTokenWithDefaultTTL() {
        // Arrange
        String subject = "joao.silva";

        // Act
        String token1 = JwtUtil.generateToken(subject, -1);
        String token2 = JwtUtil.generateToken(subject, 0);

        // Assert
        assertTrue(JwtUtil.isValid(token1));
        assertTrue(JwtUtil.isValid(token2));
    }
}
