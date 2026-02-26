// Java
package com.zappqui.api.auth.zappqui_auth_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta com token JWT")
public class TokenResponse {
    @Schema(description = "Token JWT gerado", example = "eyJhbGciOiJIUzUxMiJ9...")
    private String token;

    public TokenResponse() { }

    public TokenResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}