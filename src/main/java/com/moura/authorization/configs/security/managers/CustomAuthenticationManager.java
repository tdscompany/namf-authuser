package com.moura.authorization.configs.security.managers;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

public class CustomAuthenticationManager implements AuthenticationManager {

    private final DaoAuthenticationProvider daoAuthenticationProvider;

    public CustomAuthenticationManager(DaoAuthenticationProvider daoAuthenticationProvider) {
        this.daoAuthenticationProvider = daoAuthenticationProvider;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (daoAuthenticationProvider.supports(authentication.getClass())){
            return daoAuthenticationProvider.authenticate(authentication);
        }
        return null;
    }
}
