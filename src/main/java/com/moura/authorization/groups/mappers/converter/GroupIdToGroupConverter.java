package com.moura.authorization.groups.mappers.converter;

import com.moura.authorization.groups.entities.Group;
import com.moura.authorization.groups.repositories.GroupRepository;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Component
public class GroupIdToGroupConverter implements Converter<Set<UUID>, Set<Group>> {

    private final GroupRepository groupRepository;

    public GroupIdToGroupConverter(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    @Override
    public Set<Group> convert(MappingContext<Set<UUID>, Set<Group>> context) {
        System.out.println("aaaaaaaaaaaaaaainw");
        Set<UUID> groupIds = context.getSource();
        if (groupIds == null || groupIds.isEmpty()) {
            return Collections.emptySet();
        }
        return new HashSet<>(groupRepository.findAllById(groupIds));
    }
}