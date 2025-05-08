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
import com.moura.authorization.utils.MessageUtils;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;

    private final EventPublisher eventPublisher;

    public GroupServiceImpl(GroupRepository groupRepository, EventPublisher eventPublisher) {
        this.groupRepository = groupRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public Group create(Group entity) {
        existsByName(entity.getName(), entity.getId());

        entity.setOrganizationId(TenantContext.getCurrentTenant());
        var group = groupRepository.save(entity);

        eventPublisher.publish(GroupCreatedPayload.builder()
                .groupId(group.getId())
                .build()
        );

        return group;
    }

    public Set<Group> validateGroupIds(Set<UUID> groupIds) {
        if (CollectionUtils.isEmpty(groupIds)) return Set.of();

        Set<Group> existingGroups = groupRepository.findExistingIdsByTenant(groupIds, TenantContext.getCurrentTenant());
        if (existingGroups.size() != groupIds.size()) {
            throw new NotFoundException(MessageUtils.get("error.groupIds_not_found"));
        }

        return existingGroups;
    }

    @Override
    public Page<Group> findAll(Specification<Group> spec, Pageable pageable) {
        Page<Group> page = groupRepository.findAll(spec, pageable);
        List<Group> enriched = enrichUsersWithPermissions(page.getContent());
        return new PageImpl<>(enriched, pageable, page.getTotalElements());
    }

    @Override
    public Group findById(UUID groupId) {
        return groupRepository.findByIdAndTenant(groupId, TenantContext.getCurrentTenant())
                .orElseThrow(() -> new NotFoundException(MessageUtils.get("error.group_not_found")));
    }

    @Override
    public Group update(Group group) {
        existsByName(group.getName(), group.getId());
        return groupRepository.save(group);
    }

    private List<Group> enrichUsersWithPermissions(List<Group> groups) {
        if (groups.isEmpty()) return groups;

        List<UUID> ids = groups.stream().map(Group::getId).toList();
        Map<UUID, Group> enrichedMap = groupRepository.findAllWithPermissionsByIds(ids)
                .stream().collect(Collectors.toMap(Group::getId, Function.identity()));

        return groups.stream()
                .map(u -> enrichedMap.getOrDefault(u.getId(), u))
                .toList();
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
    private void existsByName(String name, UUID currentGroupId) {
        boolean exists = (currentGroupId == null)
                ? groupRepository.existsByName(name,TenantContext.getCurrentTenant())
                : groupRepository.existsByNameAndIdNot(name, currentGroupId,TenantContext.getCurrentTenant());

        if (exists) throw new AlreadyExistsException(MessageUtils.get("conflict.group_already_exists"));
    }
}
