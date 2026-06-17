package uef.edu.vn.beautysalonfuwa.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

public class AdminAuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("authUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }

        String role = (String) session.getAttribute("role");
        if (!"ADMIN".equals(role) && !"MANAGER".equals(role) && !"STAFF".equals(role)) {
            response.sendRedirect(request.getContextPath() + "/");
            return false;
        }

        String path = request.getRequestURI().substring(request.getContextPath().length());
        if (!canAccess(role, path)) {
            response.sendRedirect(request.getContextPath() + getDefaultAdminPath(role));
            return false;
        }

        return true;
    }

    private boolean canAccess(String role, String path) {
        if ("ADMIN".equals(role)) {
            return true;
        }

        if ("MANAGER".equals(role)) {
            return path.startsWith("/admin/services")
                    || path.startsWith("/admin/employees")
                    || path.startsWith("/admin/reports");
        }

        if ("STAFF".equals(role)) {
            return path.startsWith("/admin/appointments")
                    || path.startsWith("/admin/invoices");
        }

        return false;
    }

    private String getDefaultAdminPath(String role) {
        if ("MANAGER".equals(role)) {
            return "/admin/services";
        }

        if ("STAFF".equals(role)) {
            return "/admin/appointments";
        }

        return "/admin/dashboard";
    }
}
