// Java
package com.zappqui.api.auth.zappqui_auth_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Dados para criação de usuário")
public class UserCreateRequest {

    @NotBlank(message = "username é obrigatório")
    @Schema(description = "Nome de usuário único", example = "joao.silva", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @NotBlank(message = "password é obrigatório")
    @Schema(description = "Senha (mín. 8 caracteres, maiúscula, minúscula, número e especial @$!%*?&)", example = "Senha@123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    public UserCreateRequest() {}

    public UserCreateRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}