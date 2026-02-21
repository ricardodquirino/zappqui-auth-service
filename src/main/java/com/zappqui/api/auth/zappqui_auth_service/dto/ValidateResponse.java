// Java
package com.zappqui.api.auth.zappqui_auth_service.web.dto;

public class ValidateResponse {
    private boolean valid;
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