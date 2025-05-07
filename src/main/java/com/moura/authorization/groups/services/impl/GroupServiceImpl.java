package com.moura.authorization.groups.services.impl;

import com.moura.authorization.context.TenantContext;
import com.moura.authorization.event.publisher.EventPublisher;
import com.moura.authorization.exceptions.AlreadyExistsException;
import com.moura.authorization.exceptions.NotFoundException;
import com.moura.authorization.groups.entities.Group;
import com.moura.authorization.groups.event.GroupCreatedPayload;
import com.moura.authorization.groups.event.GroupDeletedPayload;
import com.moura.authorization.groups.repositories.GroupRepository;
import com.moura.authorization.groups.services.GroupService;
import com.moura.authorization.groups.services.PermissionService;
import com.moura.authorization.utils.MessageUtils;
import jakarta.transaction.Transactional;
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

    private final EventPublisher eventPublisher;

    public GroupServiceImpl(GroupRepository groupRepository, PermissionService permissionService, EventPublisher eventPublisher) {
        this.groupRepository = groupRepository;
        this.permissionService = permissionService;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public Group create(Group entity) {
        validateEntity(entity);

        entity.setOrganizationId(TenantContext.getCurrentTenant());
        var group = groupRepository.save(entity);

        eventPublisher.publish(GroupCreatedPayload.builder()
                .groupId(group.getId())
                .build()
        );

        return group;
    }

    public void validateGroupIds(Set<UUID> groupIds) {
        if (groupIds == null || groupIds.isEmpty())
            throw new NotFoundException(MessageUtils.get("error.groupIds_not_found"));

        Set<UUID> existingIds = groupRepository.findExistingIdsByTenant(groupIds, TenantContext.getCurrentTenant());
        if (!groupIds.removeAll(existingIds))
            throw new NotFoundException(MessageUtils.get("error.group_already_exists"));
    }

    @Override
    public Page<Group> findAll(Specification<Group> spec, Pageable pageable) {
        return groupRepository.findAll(spec, pageable);
    }

    @Override
    public Group findById(UUID groupId) {
        return groupRepository.findByIdAndTenant(groupId, TenantContext.getCurrentTenant())
                .orElseThrow(() -> new NotFoundException(MessageUtils.get("error.group_not_found")));
    }

    @Override
    public Group update(Group group) {
        return null;
    }

    @Transactional
    @Override
    public void inactivate(UUID groupId) {
        findById(groupId);
        groupRepository.deleteGroupAssociations(groupId);
        groupRepository.deleteById(groupId);

        eventPublisher.publish(GroupDeletedPayload.builder()
                .groupId(groupId)
                .build()
        );
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
