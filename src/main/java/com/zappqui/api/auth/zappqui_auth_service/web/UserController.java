// Java
package com.zappqui.api.auth.zappqui_auth_service.web;

import com.zappqui.api.auth.zappqui_auth_service.model.User;
import com.zappqui.api.auth.zappqui_auth_service.service.UserService;
import com.zappqui.api.auth.zappqui_auth_service.dto.UserCreateRequest;
import com.zappqui.api.auth.zappqui_auth_service.dto.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@Tag(name = "Usuários", description = "Endpoint de criação de usuários")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @Operation(summary = "Listar usuários", description = "Lista todos os usuários com paginação. Página padrão: 0, tamanho padrão: 20.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso")
    })
    @GetMapping
    public ResponseEntity<Page<UserResponse>> findAll(
            @Parameter(description = "Número da página (começa em 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Quantidade de registros por página", example = "20")
            @RequestParam(defaultValue = "20") int size) {

        Page<UserResponse> users = service.findAll(PageRequest.of(page, size, Sort.by("id").ascending()))
                .map(user -> new UserResponse(user.getId(), user.getUsername()));

        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Criar usuário", description = "Cria um novo usuário no sistema. "
            + "A senha deve ter no mínimo 8 caracteres, incluindo letra maiúscula, minúscula, número e caractere especial (@$!%*?&).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário criado com sucesso",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos (username duplicado, senha fraca, campos obrigatórios ausentes)",
                    content = @Content)
    })
    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserCreateRequest req) {
        try {
            User user = service.create(req.getUsername(), req.getPassword());
            return ResponseEntity.ok(new UserResponse(user.getId(), user.getUsername()));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}