package com.couponsite.admin;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    public static final String ADMIN_SESSION_KEY = "admin_logged_in";

    private final String adminUsername;
    private final String adminPassword;

    public AuthService(
        @Value("${admin.username:admin}") String adminUsername,
        @Value("${admin.password:admin123}") String adminPassword
    ) {
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
    }

    public boolean login(String username, String password, HttpSession session) {
        boolean ok = adminUsername.equals(username) && adminPassword.equals(password);
        if (ok) {
            session.setAttribute(ADMIN_SESSION_KEY, true);
        }
        return ok;
    }

    public void logout(HttpSession session) {
        session.invalidate();
    }

    public boolean isLoggedIn(HttpSession session) {
        Object value = session.getAttribute(ADMIN_SESSION_KEY);
        return value instanceof Boolean bool && bool;
    }
}
