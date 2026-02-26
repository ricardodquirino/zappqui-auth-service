// Java
package com.zappqui.api.auth.zappqui_auth_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Dados para autenticação")
public class LoginRequest {

    @Schema(description = "Nome de usuário", example = "joao.silva", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @Schema(description = "Senha do usuário", example = "Senha@123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    @Schema(description = "Tempo de vida do token em segundos (padrão: 900)", example = "3600")
    private long ttlSeconds;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getTtlSeconds() {
        return ttlSeconds;
    }

    public void setTtlSeconds(long ttlSeconds) {
        this.ttlSeconds = ttlSeconds;
    }
}