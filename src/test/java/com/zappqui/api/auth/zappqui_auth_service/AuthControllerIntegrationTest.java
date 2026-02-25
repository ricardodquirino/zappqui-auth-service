package com.zappqui.api.auth.zappqui_auth_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zappqui.api.auth.zappqui_auth_service.model.User;
import com.zappqui.api.auth.zappqui_auth_service.repository.UserRepository;
import com.zappqui.api.auth.zappqui_auth_service.dto.LoginRequest;
import com.zappqui.api.auth.zappqui_auth_service.dto.UserCreateRequest;
import com.zappqui.api.auth.zappqui_auth_service.dto.ValidateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Testes de Integração - Fluxo Completo de Autenticação")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    private String testUsername = "integracao.test";
    private String testPassword = "SenhaSegura@123";

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        // Criar usuário de teste
        User testUser = new User(testUsername, encoder.encode(testPassword));
        userRepository.save(testUser);
    }

    @Test
    @DisplayName("Deve executar fluxo completo: criar usuário, fazer login e validar token")
    void testCompleteAuthenticationFlow() throws Exception {
        // 1. Criar novo usuário
        UserCreateRequest createRequest = new UserCreateRequest("novo.usuario", "Senha123@");
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.username").value("novo.usuario"));

        // 2. Login com usuário criado
        LoginRequest loginRequest = new LoginRequest("novo.usuario", "Senha123@", 3600);
        MvcResult loginResult = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn();

        String responseBody = loginResult.getResponse().getContentAsString();
        String token = objectMapper.readTree(responseBody).get("token").asText();

        // 3. Validar token gerado
        ValidateRequest validateRequest = new ValidateRequest(token);
        mockMvc.perform(post("/auth/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.subject").value("novo.usuario"));
    }

    @Test
    @DisplayName("Deve fazer login com usuário existente")
    void testLoginWithExistingUser() throws Exception {
        // Act & Assert
        LoginRequest loginRequest = new LoginRequest(testUsername, testPassword, 3600);
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    @DisplayName("Deve rejeitar login com senha incorreta")
    void testLoginWithWrongPassword() throws Exception {
        // Act & Assert
        LoginRequest loginRequest = new LoginRequest(testUsername, "SenhaErrada@123", 3600);
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Deve rejeitar login com usuário inexistente")
    void testLoginWithNonExistentUser() throws Exception {
        // Act & Assert
        LoginRequest loginRequest = new LoginRequest("usuario.inexistente", "qualquerSenha", 3600);
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Deve validar token JWT gerado")
    void testTokenValidation() throws Exception {
        // 1. Fazer login
        LoginRequest loginRequest = new LoginRequest(testUsername, testPassword, 3600);
        MvcResult loginResult = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String token = objectMapper.readTree(loginResult.getResponse().getContentAsString())
                .get("token").asText();

        // 2. Validar token
        ValidateRequest validateRequest = new ValidateRequest(token);
        mockMvc.perform(post("/auth/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.subject").value(testUsername));
    }

    @Test
    @DisplayName("Deve rejeitar token inválido")
    void testInvalidTokenValidation() throws Exception {
        // Act & Assert
        ValidateRequest validateRequest = new ValidateRequest("token.invalido.aqui");
        mockMvc.perform(post("/auth/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(false));
    }

    @Test
    @DisplayName("Deve rejeitar duplicação de usuário")
    void testCreateDuplicateUser() throws Exception {
        UserCreateRequest createRequest = new UserCreateRequest(testUsername, testPassword);
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve manter usuários isolados no banco de dados")
    void testUserIsolation() throws Exception {
        // 1. Criar primeiro usuário
        UserCreateRequest createRequest1 = new UserCreateRequest("usuario1", "Senha@123");
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest1)))
                .andExpect(status().isOk());

        // 2. Criar segundo usuário
        UserCreateRequest createRequest2 = new UserCreateRequest("usuario2", "Senha@456");
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest2)))
                .andExpect(status().isOk());

        // 3. Login do primeiro usuário
        LoginRequest loginRequest1 = new LoginRequest("usuario1", "Senha@123", 3600);
        MvcResult result1 = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest1)))
                .andExpect(status().isOk())
                .andReturn();

        String token1 = objectMapper.readTree(result1.getResponse().getContentAsString())
                .get("token").asText();

        // 4. Validar que o token do primeiro usuário contém seu nome
        ValidateRequest validateRequest = new ValidateRequest(token1);
        mockMvc.perform(post("/auth/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subject").value("usuario1"));
    }
}
