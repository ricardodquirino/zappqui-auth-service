// Java
package com.zappqui.api.auth.zappqui_auth_service.web;

import com.zappqui.api.auth.zappqui_auth_service.service.AuthService;
import com.zappqui.api.auth.zappqui_auth_service.web.dto.LoginRequest;
import com.zappqui.api.auth.zappqui_auth_service.web.dto.TokenResponse;
import com.zappqui.api.auth.zappqui_auth_service.web.dto.ValidateRequest;
import com.zappqui.api.auth.zappqui_auth_service.web.dto.ValidateResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest req) {
        if (!service.authenticate(req.getUsername(), req.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = service.issueToken(req.getUsername(), req.getTtlSeconds());
        return ResponseEntity.ok(new TokenResponse(token));
    }

    @PostMapping("/validate")
    public ResponseEntity<ValidateResponse> validate(@RequestBody ValidateRequest req) {
        boolean valid = service.validateToken(req.getToken());
        String subject = valid ? service.getSubject(req.getToken()) : null;
        return ResponseEntity.ok(new ValidateResponse(valid, subject));
    }
}