package com.moura.authorization.auth.entities;

import com.moura.authorization.groups.entities.Permission;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@AllArgsConstructor
public class SecurityAuthority implements GrantedAuthority {

    private final Permission permission;

    @Override
    public String getAuthority() {
        return permission.getName();
    }
}
