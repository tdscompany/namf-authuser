package com.moura.authorization.configs.security;

import com.moura.authorization.users.entities.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationCurrentUserService {

    public User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public Authentication getAuthentication( ) {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}