// Java
package com.zappqui.api.auth.zappqui_auth_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Dados para validação de token")
public class ValidateRequest {

    @Schema(description = "Token JWT a ser validado", example = "eyJhbGciOiJIUzUxMiJ9...", requiredMode = Schema.RequiredMode.REQUIRED)
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}