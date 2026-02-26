// Java
package com.zappqui.api.auth.zappqui_auth_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta com dados do usuário criado")
public class UserResponse {

    @Schema(description = "ID do usuário", example = "1")
    private Long id;

    @Schema(description = "Nome de usuário", example = "joao.silva")
    private String username;

    public UserResponse() {}

    public UserResponse(Long id, String username) {
        this.id = id;
        this.username = username;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}