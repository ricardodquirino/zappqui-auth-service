package com.zappqui.api.auth.zappqui_auth_service.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zappqui.api.auth.zappqui_auth_service.config.SecurityConfig;
import com.zappqui.api.auth.zappqui_auth_service.service.AuthService;
import com.zappqui.api.auth.zappqui_auth_service.dto.LoginRequest;
import com.zappqui.api.auth.zappqui_auth_service.dto.ValidateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
@DisplayName("Testes Unitários - AuthController")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    private LoginRequest loginRequest;
    private ValidateRequest validateRequest;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest("joao.silva", "senha123", 3600);
        validateRequest = new ValidateRequest("token-valido");
    }

    @Test
    @DisplayName("Deve fazer login com credenciais corretas")
    void testLoginSuccess() throws Exception {
        // Arrange
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...";
        when(authService.authenticate("joao.silva", "senha123")).thenReturn(true);
        when(authService.issueToken("joao.silva", 3600)).thenReturn(token);

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    @DisplayName("Deve rejeitar login com credenciais incorretas")
    void testLoginFailure() throws Exception {
        // Arrange
        when(authService.authenticate("joao.silva", "senhaErrada")).thenReturn(false);

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new LoginRequest("joao.silva", "senhaErrada", 3600))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Deve validar token JWT válido")
    void testValidateTokenSuccess() throws Exception {
        // Arrange
        when(authService.validateToken("token-valido")).thenReturn(true);
        when(authService.getSubject("token-valido")).thenReturn("joao.silva");

        // Act & Assert
        mockMvc.perform(post("/auth/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.subject").value("joao.silva"));
    }

    @Test
    @DisplayName("Deve retornar válido=false para token inválido")
    void testValidateTokenFailure() throws Exception {
        // Arrange
        String invalidToken = "token-invalido";
        when(authService.validateToken(invalidToken)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(post("/auth/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new ValidateRequest(invalidToken))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.subject").doesNotExist());
    }

    @Test
    @DisplayName("Deve rejeitar requisição de login sem corpo")
    void testLoginWithoutBody() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve rejeitar requisição de validação sem corpo")
    void testValidateWithoutBody() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/auth/validate")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
