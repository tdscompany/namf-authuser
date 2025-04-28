package com.moura.authorization.auth.entities;

import com.moura.authorization.groups.entities.Permission;
import org.springframework.security.core.GrantedAuthority;

public class SecurityAuthority implements GrantedAuthority {

    private final Permission permission;

    public SecurityAuthority(Permission permission) {
        this.permission = permission;
    }

    @Override
    public String getAuthority() {
        return permission.getName();
    }
}
