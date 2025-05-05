package com.moura.authorization.groups.services;

import jakarta.validation.constraints.NotBlank;

import java.util.Set;
import java.util.UUID;

public interface PermissionService {

    void validatePermissionIds(Set<UUID> permissionsIds);
}
