package ru.glebova.utils;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SecurityUtils {
    public static boolean currentIsAdmin() {
        return hasRole("ROLE_ADMIN");
    }

    public static boolean currentIsManager() {
        return hasRole("ROLE_MANAGER");
    }

    public static boolean currentIsUser() {
        return hasRole("ROLE_USER");
    }

    public static UUID getCurrentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Jwt jwt)
            return UUID.fromString(jwt.getSubject());

        return null;
    }

    private static boolean hasRole(String role) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null)
            return false;

        return auth.getAuthorities()
                .stream()
                .anyMatch(grantedAuthority
                        -> grantedAuthority.getAuthority().equals(role));
    }
}
