package com.moura.authorization.groups.services.impl;

import com.moura.authorization.context.TenantContext;
import com.moura.authorization.groups.repositories.GroupRepository;
import com.moura.authorization.groups.services.GroupService;
import com.moura.authorization.utils.MessageUtils;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;

    public GroupServiceImpl(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    @Override
    public boolean validateGroupIds(Set<UUID> groupIds) {
        if (groupIds == null || groupIds.isEmpty()) return true;

        UUID currentTenant = TenantContext.getCurrentTenant();
        Set<UUID> existingIds = groupRepository.findExistingIdsByTenant(groupIds, currentTenant);

        Set<UUID> invalidIds = new HashSet<>(groupIds);
        invalidIds.removeAll(existingIds);

        return invalidIds.isEmpty();

    }
}
