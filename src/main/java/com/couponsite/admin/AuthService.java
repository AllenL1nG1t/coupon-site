package com.couponsite.admin;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    public static final String ADMIN_SESSION_KEY = "admin_logged_in";

    private final AdminUserRepository adminUserRepository;
    private final String defaultAdminUsername;
    private final String defaultAdminPassword;

    public AuthService(
        AdminUserRepository adminUserRepository,
        @Value("${admin.default.username:admin}") String defaultAdminUsername,
        @Value("${admin.default.password:admin123}") String defaultAdminPassword
    ) {
        this.adminUserRepository = adminUserRepository;
        this.defaultAdminUsername = defaultAdminUsername;
        this.defaultAdminPassword = defaultAdminPassword;
    }

    public boolean login(String username, String password, HttpSession session) {
        ensureDefaultAdminExists();
        boolean ok = adminUserRepository.findByUsernameIgnoreCase(username)
            .map(user -> user.getPassword().equals(password))
            .orElse(false);
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

    private void ensureDefaultAdminExists() {
        if (adminUserRepository.count() > 0) {
            return;
        }
        AdminUser user = new AdminUser();
        user.setUsername(defaultAdminUsername);
        user.setPassword(defaultAdminPassword);
        adminUserRepository.save(user);
    }
}
