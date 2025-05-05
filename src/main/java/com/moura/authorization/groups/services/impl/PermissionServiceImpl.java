package com.moura.authorization.groups.services.impl;

import com.moura.authorization.exceptions.NotFoundException;
import com.moura.authorization.groups.repositories.PermissionRepository;
import com.moura.authorization.groups.services.PermissionService;
import com.moura.authorization.utils.MessageUtils;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;

    public PermissionServiceImpl(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @Override
    public void validatePermissionIds(Set<UUID> permissionsIds) {
        if (permissionsIds == null || permissionsIds.isEmpty())
            throw new NotFoundException(MessageUtils.get("error.permission_not_found"));

        Set<UUID> existingIds = permissionRepository.findExistingIds(permissionsIds);
        if (!permissionsIds.removeAll(existingIds))
            throw new NotFoundException(MessageUtils.get("error.permission_not_found"));

    }

}
