package com.couponsite.config;

import com.couponsite.admin.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminAuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();
        if (path.startsWith("/api/admin/auth/")) {
            return true;
        }

        HttpSession session = request.getSession(false);
        Object value = session == null ? null : session.getAttribute(AuthService.ADMIN_SESSION_KEY);
        boolean loggedIn = value instanceof Boolean bool && bool;
        if (!loggedIn) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        return true;
    }
}
