package com.moura.authorization.groups.services.impl;

import com.moura.authorization.exceptions.EmptyCollectionException;
import com.moura.authorization.exceptions.NotFoundException;
import com.moura.authorization.groups.entities.Permission;
import com.moura.authorization.groups.repositories.PermissionRepository;
import com.moura.authorization.groups.services.PermissionService;
import com.moura.authorization.utils.MessageUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Set;
import java.util.UUID;

@Service
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;

    public PermissionServiceImpl(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @Override
    public Set<Permission> validatePermissionIds(Set<UUID> permissionsIds) {
        if (CollectionUtils.isEmpty(permissionsIds)) throw new EmptyCollectionException(MessageUtils.get("error.permissionIds_empty"));

        Set<Permission> existingPermissions = permissionRepository.findExistingIds(permissionsIds);
        if (existingPermissions.size() != permissionsIds.size())
            throw new NotFoundException(MessageUtils.get("error.permission_not_found"));

        return existingPermissions;
    }

}
