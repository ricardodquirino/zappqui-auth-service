// Java
package com.zappqui.api.auth.zappqui_auth_service.web;

import com.zappqui.api.auth.zappqui_auth_service.service.AuthService;
import com.zappqui.api.auth.zappqui_auth_service.dto.LoginRequest;
import com.zappqui.api.auth.zappqui_auth_service.dto.TokenResponse;
import com.zappqui.api.auth.zappqui_auth_service.dto.ValidateRequest;
import com.zappqui.api.auth.zappqui_auth_service.dto.ValidateResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticação", description = "Endpoints de login e validação de token JWT")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @Operation(summary = "Login", description = "Autentica o usuário e retorna um token JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login bem-sucedido",
                    content = @Content(schema = @Schema(implementation = TokenResponse.class))),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas", content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest req) {
        if (!service.authenticate(req.getUsername(), req.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = service.issueToken(req.getUsername(), req.getTtlSeconds());
        return ResponseEntity.ok(new TokenResponse(token));
    }

    @Operation(summary = "Validar token", description = "Valida um token JWT e retorna o subject (username)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resultado da validação",
                    content = @Content(schema = @Schema(implementation = ValidateResponse.class)))
    })
    @PostMapping("/validate")
    public ResponseEntity<ValidateResponse> validate(@RequestBody ValidateRequest req) {
        boolean valid = service.validateToken(req.getToken());
        String subject = valid ? service.getSubject(req.getToken()) : null;
        return ResponseEntity.ok(new ValidateResponse(valid, subject));
    }
}