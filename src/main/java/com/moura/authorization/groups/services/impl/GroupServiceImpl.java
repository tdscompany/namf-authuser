package com.moura.authorization.groups.services.impl;

import com.moura.authorization.context.TenantContext;
import com.moura.authorization.exceptions.AlreadyExistsException;
import com.moura.authorization.exceptions.NotFoundException;
import com.moura.authorization.groups.entities.Group;
import com.moura.authorization.groups.repositories.GroupRepository;
import com.moura.authorization.groups.services.GroupService;
import com.moura.authorization.groups.services.PermissionService;
import com.moura.authorization.users.entities.User;
import com.moura.authorization.utils.MessageUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;

    private final PermissionService permissionService;

    public GroupServiceImpl(GroupRepository groupRepository, PermissionService permissionService) {
        this.groupRepository = groupRepository;
        this.permissionService = permissionService;
    }

    @Override
    public Group create(Group entity) {
        validateEntity(entity);

        entity.setOrganizationId(TenantContext.getCurrentTenant());
        return groupRepository.save(entity);
    }

    public void validateGroupIds(Set<UUID> groupIds) {
        if (groupIds == null || groupIds.isEmpty())
            throw new NotFoundException(MessageUtils.get("error.group_not_found"));

        Set<UUID> existingIds = groupRepository.findExistingIdsByTenant(groupIds, TenantContext.getCurrentTenant());
        if (!groupIds.removeAll(existingIds))
            throw new NotFoundException(MessageUtils.get("error.group_already_exists"));
    }

    @Override
    public Page<Group> findAll(Specification<Group> spec, Pageable pageable) {
        return groupRepository.findAll(spec, pageable);
    }

    private void existsByName(String name) {
        var exists = groupRepository.existsByName(name,TenantContext.getCurrentTenant());
        if (exists) throw new AlreadyExistsException(MessageUtils.get("conflict.group_already_exists"));
    }

    private void validateEntity(Group entity) {
        existsByName(entity.getName());
        permissionService.validatePermissionIds(entity.getPermissionsIds());
    }
}
