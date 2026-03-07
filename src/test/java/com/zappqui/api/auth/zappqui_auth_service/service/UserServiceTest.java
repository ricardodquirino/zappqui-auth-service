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
import org.springframework.data.domain.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - UserService")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private BCryptPasswordEncoder encoder;

    // Senha válida que atende todas as regras do validatePassword
    private static final String VALID_PASSWORD = "Senha@123";

    @BeforeEach
    void setUp() {
        encoder = new BCryptPasswordEncoder();
    }

    @Test
    @DisplayName("Deve retornar página de usuários")
    void testFindAllSuccess() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 20, Sort.by("id").ascending());
        List<User> users = List.of(
                createUser(1L, "user1"),
                createUser(2L, "user2")
        );
        Page<User> expectedPage = new PageImpl<>(users, pageable, 2);
        when(userRepository.findAll(pageable)).thenReturn(expectedPage);

        // Act
        Page<User> result = userService.findAll(pageable);

        // Assert
        assertEquals(2, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals("user1", result.getContent().get(0).getUsername());
        assertEquals("user2", result.getContent().get(1).getUsername());
        verify(userRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Deve retornar página vazia quando não há usuários")
    void testFindAllEmpty() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 20, Sort.by("id").ascending());
        Page<User> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
        when(userRepository.findAll(pageable)).thenReturn(emptyPage);

        // Act
        Page<User> result = userService.findAll(pageable);

        // Assert
        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
        verify(userRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Deve respeitar paginação com size e page corretos")
    void testFindAllPagination() {
        // Arrange
        Pageable pageable = PageRequest.of(1, 5, Sort.by("id").ascending());
        List<User> users = IntStream.rangeClosed(6, 10)
                .mapToObj(i -> createUser((long) i, "user" + i))
                .toList();
        Page<User> page = new PageImpl<>(users, pageable, 20);
        when(userRepository.findAll(pageable)).thenReturn(page);

        // Act
        Page<User> result = userService.findAll(pageable);

        // Assert
        assertEquals(5, result.getContent().size());
        assertEquals(20, result.getTotalElements());
        assertEquals(4, result.getTotalPages());
        assertEquals(1, result.getNumber());
        verify(userRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Deve criar um novo usuário com sucesso")
    void testCreateUserSuccess() {
        String username = "joao.silva";

        when(userRepository.existsByUsername(username)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        User createdUser = userService.create(username, VALID_PASSWORD);

        assertNotNull(createdUser);
        assertEquals(username, createdUser.getUsername());
        assertNotNull(createdUser.getPasswordHash());
        assertNotEquals(VALID_PASSWORD, createdUser.getPasswordHash());

        verify(userRepository, times(1)).existsByUsername(username);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário já existe")
    void testCreateUserAlreadyExists() {
        String username = "joao.silva";

        when(userRepository.existsByUsername(username)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () ->
                userService.create(username, VALID_PASSWORD));

        verify(userRepository, times(1)).existsByUsername(username);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando senha é nula")
    void testCreateUserNullPassword() {
        when(userRepository.existsByUsername("joao.silva")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () ->
                userService.create("joao.silva", null));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando senha é vazia")
    void testCreateUserEmptyPassword() {
        when(userRepository.existsByUsername("joao.silva")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () ->
                userService.create("joao.silva", ""));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando senha é fraca (sem maiúscula, sem especial)")
    void testCreateUserWeakPassword() {
        when(userRepository.existsByUsername("joao.silva")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () ->
                userService.create("joao.silva", "senha123"));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Deve validar senha corretamente")
    void testPasswordMatches() {
        String rawPassword = "Senha@123";
        String hash = encoder.encode(rawPassword);

        assertTrue(userService.matches(rawPassword, hash));
    }

    @Test
    @DisplayName("Deve rejeitar senha incorreta")
    void testPasswordMismatch() {
        String rawPassword = "Senha@123";
        String hash = encoder.encode(rawPassword);

        assertFalse(userService.matches("SenhaErrada@456", hash));
    }

    @Test
    @DisplayName("Deve rejeitar senha vazia no matches")
    void testEmptyPasswordMismatch() {
        String rawPassword = "Senha@123";
        String hash = encoder.encode(rawPassword);

        assertFalse(userService.matches("", hash));
    }

    private User createUser(Long id, String username) {
        User user = new User(username, "hash_" + username);
        user.setId(id);
        return user;
    }
}
