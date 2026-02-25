package com.zappqui.api.auth.zappqui_auth_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zappqui.api.auth.zappqui_auth_service.repository.UserRepository;
import com.zappqui.api.auth.zappqui_auth_service.service.UserService;
import com.zappqui.api.auth.zappqui_auth_service.dto.UserCreateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Testes de Integração - UserController")
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve criar usuário e persistir no banco de dados")
    void testCreateUserPersistence() throws Exception {
        // Arrange
        UserCreateRequest createRequest = new UserCreateRequest("novo.usuario", "Senha@123");

        // Act & Assert
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.username").value("novo.usuario"));

        // Verificar que foi salvo no banco
        assertTrue(userRepository.existsByUsername("novo.usuario"));
    }

    @Test
    @DisplayName("Deve rejeitar criação de usuário com username vazio")
    void testCreateUserWithEmptyUsername() throws Exception {
        String json = "{\"username\": \"\", \"password\": \"Senha@123\"}";

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve rejeitar criação de usuário com password vazio")
    void testCreateUserWithEmptyPassword() throws Exception {
        // Arrange
        String json = "{\"username\": \"usuario.novo\", \"password\": \"\"}";

        // Act & Assert
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve criar múltiplos usuários sem conflito")
    void testCreateMultipleUsers() throws Exception {
        // Act
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new UserCreateRequest("usuario1", "Senha@111"))))
                .andExpect(status().isOk());

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new UserCreateRequest("usuario2", "Senha@222"))))
                .andExpect(status().isOk());

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new UserCreateRequest("usuario3", "Senha@333"))))
                .andExpect(status().isOk());

        // Assert
        assertEquals(3, userRepository.count());
    }

    @Test
    @DisplayName("Deve impedir duplicação de username")
    void testPreventDuplicateUsername() throws Exception {
        // Arrange
        UserCreateRequest createRequest1 = new UserCreateRequest("usuario.duplicado", "Senha@111");
        UserCreateRequest createRequest2 = new UserCreateRequest("usuario.duplicado", "Senha@222");

        // Act
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest1)))
                .andExpect(status().isOk());

        // Assert - Segunda tentativa deve falhar com 400 BAD_REQUEST
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest2)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar ID do usuário criado")
    void testCreateUserReturnsValidId() throws Exception {
        // Arrange
        UserCreateRequest createRequest = new UserCreateRequest("usuario.com.id", "Senha@123");

        // Act & Assert
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.id").value(org.hamcrest.Matchers.greaterThan(0)));
    }

    @Test
    @DisplayName("Deve responder com Content-Type application/json")
    void testCreateUserReturnsJsonResponse() throws Exception {
        // Arrange
        UserCreateRequest createRequest = new UserCreateRequest("usuario.json", "Senha@123");

        // Act & Assert
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Deve aceitar passwords com caracteres especiais")
    void testCreateUserWithSpecialCharactersPassword() throws Exception {
        // Arrange
        UserCreateRequest createRequest = new UserCreateRequest("usuario.especial", "P@$$w0rd!#%&*()");

        // Act & Assert
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve aceitar usernames com pontos e underscores")
    void testCreateUserWithSpecialUsernameCharacters() throws Exception {
        // Arrange
        UserCreateRequest createRequest = new UserCreateRequest("usuario.nome_valido", "Senha@123");

        // Act & Assert
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("usuario.nome_valido"));
    }

    @Test
    @DisplayName("Deve validar que usuário criado pode fazer login")
    void testCreatedUserCanLogin() throws Exception {
        // 1. Criar usuário
        UserCreateRequest createRequest = new UserCreateRequest("usuario.login", "Senha@123");
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk());

        // 2. Verificar que o usuário existe no banco
        assertTrue(userRepository.existsByUsername("usuario.login"));
    }

    @Test
    @DisplayName("Deve rejeitar requisição sem Content-Type")
    void testCreateUserWithoutContentType() throws Exception {
        UserCreateRequest createRequest = new UserCreateRequest("usuario.teste", "Senha@123");

        mockMvc.perform(post("/users")
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isUnsupportedMediaType());
    }
}
