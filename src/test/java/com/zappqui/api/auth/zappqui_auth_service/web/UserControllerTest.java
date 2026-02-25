package com.zappqui.api.auth.zappqui_auth_service.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zappqui.api.auth.zappqui_auth_service.config.SecurityConfig;
import com.zappqui.api.auth.zappqui_auth_service.model.User;
import com.zappqui.api.auth.zappqui_auth_service.service.UserService;
import com.zappqui.api.auth.zappqui_auth_service.dto.UserCreateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
@DisplayName("Testes Unitários - UserController")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("joao.silva", "hash_de_senha");
        testUser.setId(1L);
    }

    @Test
    @DisplayName("Deve criar um novo usuário com sucesso")
    void testCreateUserSuccess() throws Exception {
        // Arrange
        when(userService.create(anyString(), anyString())).thenReturn(testUser);

        UserCreateRequest request = new UserCreateRequest("joao.silva", "Senha@123");

        // Act & Assert
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("joao.silva"));
    }

    @Test
    @DisplayName("Deve retornar 400 quando username já existe")
    void testCreateUserDuplicate() throws Exception {
        // Arrange
        when(userService.create(anyString(), anyString()))
                .thenThrow(new IllegalArgumentException("username já existe"));

        UserCreateRequest request = new UserCreateRequest("joao.silva", "Senha@123");

        // Act & Assert
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar 400 quando senha é inválida")
    void testCreateUserInvalidPassword() throws Exception {
        // Arrange
        when(userService.create(anyString(), anyString()))
                .thenThrow(new IllegalArgumentException("A senha deve ter no mínimo 8 caracteres"));

        UserCreateRequest request = new UserCreateRequest("joao.silva", "fraca");

        // Act & Assert
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar 400 quando corpo da requisição está vazio")
    void testCreateUserWithoutBody() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar 400 quando username é nulo")
    void testCreateUserWithoutUsername() throws Exception {
        // Arrange
        String json = "{\"password\": \"Senha@123\"}";

        // Act & Assert
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar 400 quando password é nulo")
    void testCreateUserWithoutPassword() throws Exception {
        // Arrange
        String json = "{\"username\": \"joao.silva\"}";

        // Act & Assert
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar 400 quando password é vazio")
    void testCreateUserWithEmptyPassword() throws Exception {
        // Arrange
        String json = "{\"username\": \"joao.silva\", \"password\": \"\"}";

        // Act & Assert
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar ID e username do usuário criado")
    void testCreateUserReturnsIdAndUsername() throws Exception {
        // Arrange
        when(userService.create(anyString(), anyString())).thenReturn(testUser);

        UserCreateRequest request = new UserCreateRequest("joao.silva", "Senha@123");

        // Act & Assert
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.username").exists());
    }
}
