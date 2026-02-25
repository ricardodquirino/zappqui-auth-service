package com.zappqui.api.auth.zappqui_auth_service.web.dto;

import com.zappqui.api.auth.zappqui_auth_service.dto.LoginRequest;
import com.zappqui.api.auth.zappqui_auth_service.dto.TokenResponse;
import com.zappqui.api.auth.zappqui_auth_service.dto.UserCreateRequest;
import com.zappqui.api.auth.zappqui_auth_service.dto.UserResponse;
import com.zappqui.api.auth.zappqui_auth_service.dto.ValidateRequest;
import com.zappqui.api.auth.zappqui_auth_service.dto.ValidateResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes Unitários - DTOs")
class DTOTest {

    @DisplayName("Testes - LoginRequest")
    @org.junit.jupiter.api.Nested
    class LoginRequestTest {
        private LoginRequest loginRequest;

        @BeforeEach
        void setUp() {
            loginRequest = new LoginRequest("joao.silva", "senha123", 3600);
        }

        @Test
        @DisplayName("Deve criar LoginRequest com valores")
        void testLoginRequestCreation() {
            // Assert
            assertEquals("joao.silva", loginRequest.getUsername());
            assertEquals("senha123", loginRequest.getPassword());
            assertEquals(3600, loginRequest.getTtlSeconds());
        }

        @Test
        @DisplayName("Deve criar LoginRequest vazio")
        void testLoginRequestEmptyCreation() {
            // Act
            LoginRequest empty = new LoginRequest();

            // Assert
            assertNotNull(empty);
        }

        @Test
        @DisplayName("Deve atualizar username")
        void testSetUsername() {
            // Act
            loginRequest.setUsername("maria.silva");

            // Assert
            assertEquals("maria.silva", loginRequest.getUsername());
        }

        @Test
        @DisplayName("Deve atualizar password")
        void testSetPassword() {
            // Act
            loginRequest.setPassword("novaSenha456");

            // Assert
            assertEquals("novaSenha456", loginRequest.getPassword());
        }

        @Test
        @DisplayName("Deve atualizar TTL")
        void testSetTtlSeconds() {
            // Act
            loginRequest.setTtlSeconds(7200);

            // Assert
            assertEquals(7200, loginRequest.getTtlSeconds());
        }
    }

    @DisplayName("Testes - ValidateRequest")
    @org.junit.jupiter.api.Nested
    class ValidateRequestTest {
        private ValidateRequest validateRequest;

        @BeforeEach
        void setUp() {
            validateRequest = new ValidateRequest("token.valor.aqui");
        }

        @Test
        @DisplayName("Deve criar ValidateRequest com token")
        void testValidateRequestCreation() {
            // Assert
            assertEquals("token.valor.aqui", validateRequest.getToken());
        }

        @Test
        @DisplayName("Deve criar ValidateRequest vazio")
        void testValidateRequestEmptyCreation() {
            // Act
            ValidateRequest empty = new ValidateRequest();

            // Assert
            assertNotNull(empty);
        }

        @Test
        @DisplayName("Deve atualizar token")
        void testSetToken() {
            // Act
            validateRequest.setToken("novo.token.aqui");

            // Assert
            assertEquals("novo.token.aqui", validateRequest.getToken());
        }
    }

    @DisplayName("Testes - UserCreateRequest")
    @org.junit.jupiter.api.Nested
    class UserCreateRequestTest {
        private UserCreateRequest userCreateRequest;

        @BeforeEach
        void setUp() {
            userCreateRequest = new UserCreateRequest("novo.usuario", "senha@123");
        }

        @Test
        @DisplayName("Deve criar UserCreateRequest com valores")
        void testUserCreateRequestCreation() {
            // Assert
            assertEquals("novo.usuario", userCreateRequest.getUsername());
            assertEquals("senha@123", userCreateRequest.getPassword());
        }

        @Test
        @DisplayName("Deve criar UserCreateRequest vazio")
        void testUserCreateRequestEmptyCreation() {
            // Act
            UserCreateRequest empty = new UserCreateRequest();

            // Assert
            assertNotNull(empty);
        }

        @Test
        @DisplayName("Deve atualizar username")
        void testSetUsername() {
            // Act
            userCreateRequest.setUsername("outro.usuario");

            // Assert
            assertEquals("outro.usuario", userCreateRequest.getUsername());
        }

        @Test
        @DisplayName("Deve atualizar password")
        void testSetPassword() {
            // Act
            userCreateRequest.setPassword("novaSenha@456");

            // Assert
            assertEquals("novaSenha@456", userCreateRequest.getPassword());
        }
    }

    @DisplayName("Testes - TokenResponse")
    @org.junit.jupiter.api.Nested
    class TokenResponseTest {
        private TokenResponse tokenResponse;

        @BeforeEach
        void setUp() {
            tokenResponse = new TokenResponse("eyJhbGc...");
        }

        @Test
        @DisplayName("Deve criar TokenResponse com token")
        void testTokenResponseCreation() {
            // Assert
            assertEquals("eyJhbGc...", tokenResponse.getToken());
        }

        @Test
        @DisplayName("Deve atualizar token")
        void testSetToken() {
            // Act
            tokenResponse.setToken("novoToken...");

            // Assert
            assertEquals("novoToken...", tokenResponse.getToken());
        }
    }

    @DisplayName("Testes - UserResponse")
    @org.junit.jupiter.api.Nested
    class UserResponseTest {
        private UserResponse userResponse;

        @BeforeEach
        void setUp() {
            userResponse = new UserResponse(1L, "joao.silva");
        }

        @Test
        @DisplayName("Deve criar UserResponse com valores")
        void testUserResponseCreation() {
            // Assert
            assertEquals(1L, userResponse.getId());
            assertEquals("joao.silva", userResponse.getUsername());
        }

        @Test
        @DisplayName("Deve atualizar username")
        void testSetUsername() {
            // Act
            userResponse.setUsername("maria.silva");

            // Assert
            assertEquals("maria.silva", userResponse.getUsername());
        }
    }

    @DisplayName("Testes - ValidateResponse")
    @org.junit.jupiter.api.Nested
    class ValidateResponseTest {
        private ValidateResponse validateResponse;

        @BeforeEach
        void setUp() {
            validateResponse = new ValidateResponse(true, "joao.silva");
        }

        @Test
        @DisplayName("Deve criar ValidateResponse válido")
        void testValidateResponseCreationValid() {
            // Assert
            assertTrue(validateResponse.isValid());
            assertEquals("joao.silva", validateResponse.getSubject());
        }

        @Test
        @DisplayName("Deve criar ValidateResponse inválido")
        void testValidateResponseCreationInvalid() {
            // Act
            ValidateResponse invalid = new ValidateResponse(false, null);

            // Assert
            assertFalse(invalid.isValid());
            assertNull(invalid.getSubject());
        }

        @Test
        @DisplayName("Deve atualizar valid flag")
        void testSetValid() {
            // Act
            validateResponse.setValid(false);

            // Assert
            assertFalse(validateResponse.isValid());
        }

        @Test
        @DisplayName("Deve atualizar subject")
        void testSetSubject() {
            // Act
            validateResponse.setSubject("maria.silva");

            // Assert
            assertEquals("maria.silva", validateResponse.getSubject());
        }
    }
}

