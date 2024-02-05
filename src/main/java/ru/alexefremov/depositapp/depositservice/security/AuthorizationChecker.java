package ru.alexefremov.depositapp.depositservice.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthorizationChecker {
    public boolean isOwnerOfResource(long userId) {
        try {
            long authenticatedUserId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
            return userId == authenticatedUserId;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
