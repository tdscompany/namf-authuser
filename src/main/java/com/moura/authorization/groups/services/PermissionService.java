package com.moura.authorization.groups.services;

import com.moura.authorization.groups.entities.Permission;
import jakarta.validation.constraints.NotBlank;

import java.util.Set;
import java.util.UUID;

public interface PermissionService {

    Set<Permission> validatePermissionIds(Set<UUID> permissionsIds);
}
