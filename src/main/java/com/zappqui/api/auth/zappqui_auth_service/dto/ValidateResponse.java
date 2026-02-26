// Java
package com.zappqui.api.auth.zappqui_auth_service.dto;

 import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resultado da validação de token")
public class ValidateResponse {
    @Schema(description = "Indica se o token é válido", example = "true")
    private boolean valid;

    @Schema(description = "Subject (username) extraído do token. Null se inválido", example = "joao.silva")
    private String subject;

    public ValidateResponse() { }

    public ValidateResponse(boolean valid, String subject) {
        this.valid = valid;
        this.subject = subject;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}