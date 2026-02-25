package com.zappqui.api.auth.zappqui_auth_service.service;

import com.zappqui.api.auth.zappqui_auth_service.model.User;
import com.zappqui.api.auth.zappqui_auth_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - AuthService")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private BCryptPasswordEncoder encoder;

    @BeforeEach
    void setUp() {
        encoder = new BCryptPasswordEncoder();
        testUser = new User("joao.silva", encoder.encode("senha123"));
        testUser.setId(1L);
    }

    @Test
    @DisplayName("Deve autenticar usuário com credenciais corretas")
    void testAuthenticateSuccess() {
        // Arrange
        String username = "joao.silva";
        String password = "senha123";

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
        when(userService.matches(password, testUser.getPasswordHash())).thenReturn(true);

        // Act
        boolean isAuthenticated = authService.authenticate(username, password);

        // Assert
        assertTrue(isAuthenticated);
        verify(userRepository, times(1)).findByUsername(username);
        verify(userService, times(1)).matches(password, testUser.getPasswordHash());
    }

    @Test
    @DisplayName("Deve rejeitar autenticação com senha incorreta")
    void testAuthenticateFailureWrongPassword() {
        // Arrange
        String username = "joao.silva";
        String wrongPassword = "senhaErrada";

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
        when(userService.matches(wrongPassword, testUser.getPasswordHash())).thenReturn(false);

        // Act
        boolean isAuthenticated = authService.authenticate(username, wrongPassword);

        // Assert
        assertFalse(isAuthenticated);
        verify(userRepository, times(1)).findByUsername(username);
        verify(userService, times(1)).matches(wrongPassword, testUser.getPasswordHash());
    }

    @Test
    @DisplayName("Deve rejeitar autenticação com usuário não encontrado")
    void testAuthenticateFailureUserNotFound() {
        // Arrange
        String username = "usuario.inexistente";
        String password = "senha123";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act
        boolean isAuthenticated = authService.authenticate(username, password);

        // Assert
        assertFalse(isAuthenticated);
        verify(userRepository, times(1)).findByUsername(username);
        verify(userService, never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("Deve gerar token JWT válido")
    void testIssueTokenSuccess() {
        // Act
        String token = authService.issueToken("joao.silva", 3600);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.contains("."));
    }

    @Test
    @DisplayName("Deve validar token JWT válido")
    void testValidateTokenSuccess() {
        // Arrange
        String token = authService.issueToken("joao.silva", 3600);

        // Act
        boolean isValid = authService.validateToken(token);

        // Assert
        assertTrue(isValid);
    }

    @Test
    @DisplayName("Deve rejeitar token JWT inválido")
    void testValidateTokenFailure() {
        // Arrange
        String invalidToken = "token.invalido.aqui";

        // Act
        boolean isValid = authService.validateToken(invalidToken);

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Deve rejeitar token JWT vazio")
    void testValidateEmptyToken() {
        // Act
        boolean isValid = authService.validateToken("");

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Deve extrair subject de token JWT válido")
    void testGetSubjectSuccess() {
        // Arrange
        String username = "joao.silva";
        String token = authService.issueToken(username, 3600);

        // Act
        String subject = authService.getSubject(token);

        // Assert
        assertEquals(username, subject);
    }

    @Test
    @DisplayName("Deve retornar null para subject de token inválido")
    void testGetSubjectFailure() {
        // Arrange
        String invalidToken = "token.invalido.aqui";

        // Act
        String subject = authService.getSubject(invalidToken);

        // Assert
        assertNull(subject);
    }
}

