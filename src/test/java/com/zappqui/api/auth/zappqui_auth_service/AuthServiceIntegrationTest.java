package com.zappqui.api.auth.zappqui_auth_service;

import com.zappqui.api.auth.zappqui_auth_service.model.User;
import com.zappqui.api.auth.zappqui_auth_service.repository.UserRepository;
import com.zappqui.api.auth.zappqui_auth_service.service.AuthService;
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
@DisplayName("Testes de Integração - AuthService")
class AuthServiceIntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private String testUsername = "auth.test";
    private String testPassword = "SenhaAuth@123";

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        userService.create(testUsername, testPassword);
    }

    @Test
    @DisplayName("Deve autenticar usuário existente com credenciais corretas")
    void testAuthenticateExistingUserSuccess() {
        // Act
        boolean isAuthenticated = authService.authenticate(testUsername, testPassword);

        // Assert
        assertTrue(isAuthenticated);
    }

    @Test
    @DisplayName("Deve rejeitar autenticação com senha incorreta")
    void testAuthenticateWithWrongPassword() {
        // Act
        boolean isAuthenticated = authService.authenticate(testUsername, "SenhaErrada@123");

        // Assert
        assertFalse(isAuthenticated);
    }

    @Test
    @DisplayName("Deve rejeitar autenticação com usuário inexistente")
    void testAuthenticateNonExistentUser() {
        // Act
        boolean isAuthenticated = authService.authenticate("usuario.inexistente", testPassword);

        // Assert
        assertFalse(isAuthenticated);
    }

    @Test
    @DisplayName("Deve gerar token para usuário autenticado")
    void testIssueTokenSuccess() {
        // Act
        String token = authService.issueToken(testUsername, 3600);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.contains("."));
    }

    @Test
    @DisplayName("Deve validar token gerado")
    void testGeneratedTokenIsValid() {
        // Act
        String token = authService.issueToken(testUsername, 3600);
        boolean isValid = authService.validateToken(token);

        // Assert
        assertTrue(isValid);
    }

    @Test
    @DisplayName("Deve extrair subject correto do token")
    void testGetSubjectFromToken() {
        // Act
        String token = authService.issueToken(testUsername, 3600);
        String subject = authService.getSubject(token);

        // Assert
        assertEquals(testUsername, subject);
    }

    @Test
    @DisplayName("Deve rejeitar token inválido")
    void testValidateInvalidToken() {
        // Act
        boolean isValid = authService.validateToken("token.invalido.aqui");

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Deve retornar null para subject de token inválido")
    void testGetSubjectFromInvalidToken() {
        // Act
        String subject = authService.getSubject("token.invalido.aqui");

        // Assert
        assertNull(subject);
    }

    @Test
    @DisplayName("Deve executar fluxo completo: autenticar, emitir token e validar")
    void testCompleteAuthFlow() {
        // 1. Autenticar usuário
        boolean isAuthenticated = authService.authenticate(testUsername, testPassword);
        assertTrue(isAuthenticated);

        // 2. Emitir token
        String token = authService.issueToken(testUsername, 3600);
        assertNotNull(token);

        // 3. Validar token
        boolean isValid = authService.validateToken(token);
        assertTrue(isValid);

        // 4. Extrair subject
        String subject = authService.getSubject(token);
        assertEquals(testUsername, subject);
    }

    @Test
    @DisplayName("Deve gerar tokens válidos para o mesmo usuário")
    void testGenerateTokensForSameUser() {
        // Act
        String token1 = authService.issueToken(testUsername, 3600);
        String token2 = authService.issueToken(testUsername, 3600);

        // Assert
        assertNotNull(token1);
        assertNotNull(token2);
        assertFalse(token1.isEmpty());
        assertFalse(token2.isEmpty());

        // Ambos os tokens devem ser válidos e ter o mesmo subject
        assertTrue(authService.validateToken(token1));
        assertTrue(authService.validateToken(token2));
        assertEquals(testUsername, authService.getSubject(token1));
        assertEquals(testUsername, authService.getSubject(token2));

        // Opcional: se futuramente JwtUtil incluir um jti/nonce, podemos voltar a afirmar que são diferentes
        // assertNotEquals(token1, token2);
    }

    @Test
    @DisplayName("Deve falhar ao autenticar após deletar usuário")
    void testAuthenticationFailsAfterUserDeletion() {
        // Arrange
        boolean isAuthenticatedBefore = authService.authenticate(testUsername, testPassword);
        assertTrue(isAuthenticatedBefore);

        // Deletar usuário
        User user = userRepository.findByUsername(testUsername).orElseThrow();
        userRepository.delete(user);

        // Act
        boolean isAuthenticatedAfter = authService.authenticate(testUsername, testPassword);

        // Assert
        assertFalse(isAuthenticatedAfter);
    }

    @Test
    @DisplayName("Deve validar token com TTL diferente")
    void testTokenWithDifferentTTL() {
        // Act
        String tokenShortTTL = authService.issueToken(testUsername, 1);
        String tokenLongTTL = authService.issueToken(testUsername, 7200);

        // Assert
        assertTrue(authService.validateToken(tokenShortTTL));
        assertTrue(authService.validateToken(tokenLongTTL));
        assertEquals(testUsername, authService.getSubject(tokenShortTTL));
        assertEquals(testUsername, authService.getSubject(tokenLongTTL));
    }

    @Test
    @DisplayName("Deve manter isolamento entre múltiplos usuários")
    void testMultipleUsersTokenIsolation() {
        // Arrange
        String user2Username = "usuario2";
        String user2Password = "SenhaUser2@123";
        userService.create(user2Username, user2Password);

        // Act
        String token1 = authService.issueToken(testUsername, 3600);
        String token2 = authService.issueToken(user2Username, 3600);

        // Assert
        assertEquals(testUsername, authService.getSubject(token1));
        assertEquals(user2Username, authService.getSubject(token2));
        assertNotEquals(token1, token2);
    }
}
