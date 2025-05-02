package com.moura.authorization.groups.mappers;

import com.moura.authorization.groups.entities.Group;
import com.moura.authorization.groups.repositories.GroupRepository;
import com.moura.authorization.specifications.SpecificationTemplate;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Component
public class GroupIdMapper {

    private final GroupRepository groupRepository;

    public GroupIdMapper(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    public Set<Group> map(Set<UUID> groupIds) {
        if (groupIds == null || groupIds.isEmpty()) return null;
        return new HashSet<>(groupRepository.findAll(SpecificationTemplate.tenantAndIdIn("id", groupIds)));
    }
}