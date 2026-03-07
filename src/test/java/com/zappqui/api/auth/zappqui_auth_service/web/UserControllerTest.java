package com.zappqui.api.auth.zappqui_auth_service.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zappqui.api.auth.zappqui_auth_service.config.SecurityConfig;
import com.zappqui.api.auth.zappqui_auth_service.model.User;
import com.zappqui.api.auth.zappqui_auth_service.service.UserService;
import com.zappqui.api.auth.zappqui_auth_service.dto.UserCreateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    // ======================== GET /users (Paginação) ========================

    @Nested
    @DisplayName("GET /users - Listar usuários com paginação")
    class FindAllTests {

        @Test
        @DisplayName("Deve retornar página de usuários com parâmetros padrão (page=0, size=20)")
        void testFindAllDefaultParams() throws Exception {
            List<User> users = List.of(testUser);
            Page<User> page = new PageImpl<>(users, PageRequest.of(0, 20, Sort.by("id").ascending()), 1);
            when(userService.findAll(any(Pageable.class))).thenReturn(page);

            mockMvc.perform(get("/users"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content[0].id").value(1))
                    .andExpect(jsonPath("$.content[0].username").value("joao.silva"))
                    .andExpect(jsonPath("$.totalElements").value(1))
                    .andExpect(jsonPath("$.totalPages").value(1))
                    .andExpect(jsonPath("$.size").value(20))
                    .andExpect(jsonPath("$.number").value(0));

            verify(userService).findAll(any(Pageable.class));
        }

        @Test
        @DisplayName("Deve retornar página com parâmetros customizados (page=1, size=5)")
        void testFindAllCustomParams() throws Exception {
            Page<User> page = new PageImpl<>(Collections.emptyList(), PageRequest.of(1, 5, Sort.by("id").ascending()), 5);
            when(userService.findAll(any(Pageable.class))).thenReturn(page);

            mockMvc.perform(get("/users")
                            .param("page", "1")
                            .param("size", "5"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.size").value(5))
                    .andExpect(jsonPath("$.number").value(1))
                    .andExpect(jsonPath("$.totalElements").value(5));

            verify(userService).findAll(any(Pageable.class));
        }

        @Test
        @DisplayName("Deve retornar página vazia quando não há usuários")
        void testFindAllEmpty() throws Exception {
            Page<User> emptyPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 20, Sort.by("id").ascending()), 0);
            when(userService.findAll(any(Pageable.class))).thenReturn(emptyPage);

            mockMvc.perform(get("/users"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isEmpty())
                    .andExpect(jsonPath("$.totalElements").value(0))
                    .andExpect(jsonPath("$.totalPages").value(0));
        }

        @Test
        @DisplayName("Deve retornar múltiplos usuários na página")
        void testFindAllMultipleUsers() throws Exception {
            List<User> users = IntStream.rangeClosed(1, 3)
                    .mapToObj(i -> {
                        User u = new User("user" + i, "hash" + i);
                        u.setId((long) i);
                        return u;
                    }).toList();

            Page<User> page = new PageImpl<>(users, PageRequest.of(0, 20, Sort.by("id").ascending()), 3);
            when(userService.findAll(any(Pageable.class))).thenReturn(page);

            mockMvc.perform(get("/users"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content.length()").value(3))
                    .andExpect(jsonPath("$.content[0].id").value(1))
                    .andExpect(jsonPath("$.content[0].username").value("user1"))
                    .andExpect(jsonPath("$.content[1].id").value(2))
                    .andExpect(jsonPath("$.content[1].username").value("user2"))
                    .andExpect(jsonPath("$.content[2].id").value(3))
                    .andExpect(jsonPath("$.content[2].username").value("user3"))
                    .andExpect(jsonPath("$.totalElements").value(3));
        }

        @Test
        @DisplayName("Não deve expor passwordHash na resposta")
        void testFindAllDoesNotExposePassword() throws Exception {
            List<User> users = List.of(testUser);
            Page<User> page = new PageImpl<>(users, PageRequest.of(0, 20, Sort.by("id").ascending()), 1);
            when(userService.findAll(any(Pageable.class))).thenReturn(page);

            mockMvc.perform(get("/users"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].passwordHash").doesNotExist())
                    .andExpect(jsonPath("$.content[0].password").doesNotExist());
        }

        @Test
        @DisplayName("Deve respeitar paginação com totalPages correto")
        void testFindAllPaginationMetadata() throws Exception {
            List<User> users = IntStream.rangeClosed(1, 20)
                    .mapToObj(i -> {
                        User u = new User("user" + i, "hash" + i);
                        u.setId((long) i);
                        return u;
                    }).toList();

            Page<User> page = new PageImpl<>(users, PageRequest.of(0, 20, Sort.by("id").ascending()), 50);
            when(userService.findAll(any(Pageable.class))).thenReturn(page);

            mockMvc.perform(get("/users"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content.length()").value(20))
                    .andExpect(jsonPath("$.totalElements").value(50))
                    .andExpect(jsonPath("$.totalPages").value(3))
                    .andExpect(jsonPath("$.first").value(true))
                    .andExpect(jsonPath("$.last").value(false));
        }
    }

    // ======================== POST /users (Criar usuário) ========================

    @Nested
    @DisplayName("POST /users - Criar usuário")
    class CreateUserTests {

        @Test
        @DisplayName("Deve criar um novo usuário com sucesso")
        void testCreateUserSuccess() throws Exception {
            when(userService.create(anyString(), anyString())).thenReturn(testUser);

            UserCreateRequest request = new UserCreateRequest("joao.silva", "Senha@123");

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
            when(userService.create(anyString(), anyString()))
                    .thenThrow(new IllegalArgumentException("username já existe"));

            UserCreateRequest request = new UserCreateRequest("joao.silva", "Senha@123");

            mockMvc.perform(post("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Deve retornar 400 quando senha é inválida")
        void testCreateUserInvalidPassword() throws Exception {
            when(userService.create(anyString(), anyString()))
                    .thenThrow(new IllegalArgumentException("A senha deve ter no mínimo 8 caracteres"));

            UserCreateRequest request = new UserCreateRequest("joao.silva", "fraca");

            mockMvc.perform(post("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Deve retornar 400 quando corpo da requisição está vazio")
        void testCreateUserWithoutBody() throws Exception {
            mockMvc.perform(post("/users")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Deve retornar 400 quando username é nulo")
        void testCreateUserWithoutUsername() throws Exception {
            String json = "{\"password\": \"Senha@123\"}";

            mockMvc.perform(post("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Deve retornar 400 quando password é nulo")
        void testCreateUserWithoutPassword() throws Exception {
            String json = "{\"username\": \"joao.silva\"}";

            mockMvc.perform(post("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Deve retornar 400 quando password é vazio")
        void testCreateUserWithEmptyPassword() throws Exception {
            String json = "{\"username\": \"joao.silva\", \"password\": \"\"}";

            mockMvc.perform(post("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Deve retornar ID e username sem expor senha")
        void testCreateUserReturnsIdAndUsername() throws Exception {
            when(userService.create(anyString(), anyString())).thenReturn(testUser);

            UserCreateRequest request = new UserCreateRequest("joao.silva", "Senha@123");

            mockMvc.perform(post("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.username").exists())
                    .andExpect(jsonPath("$.passwordHash").doesNotExist())
                    .andExpect(jsonPath("$.password").doesNotExist());
        }

        @Test
        @DisplayName("Não deve chamar service quando validação do DTO falha")
        void testCreateUserValidationFailDoesNotCallService() throws Exception {
            String json = "{\"username\": \"\", \"password\": \"\"}";

            mockMvc.perform(post("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest());

            verify(userService, never()).create(anyString(), anyString());
        }
    }
}
