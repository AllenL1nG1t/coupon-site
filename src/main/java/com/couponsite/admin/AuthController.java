package com.couponsite.admin;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthStatusDto> login(@RequestBody LoginRequest request, HttpSession session) {
        if (request == null || request.username() == null || request.password() == null) {
            return ResponseEntity.badRequest().build();
        }
        boolean ok = authService.login(request.username().trim(), request.password(), session);
        if (!ok) {
            return ResponseEntity.status(401).body(new AuthStatusDto(false));
        }
        return ResponseEntity.ok(new AuthStatusDto(true));
    }

    @PostMapping("/logout")
    public AuthStatusDto logout(HttpSession session) {
        authService.logout(session);
        return new AuthStatusDto(false);
    }

    @GetMapping("/status")
    public AuthStatusDto status(HttpSession session) {
        return new AuthStatusDto(authService.isLoggedIn(session));
    }
}
