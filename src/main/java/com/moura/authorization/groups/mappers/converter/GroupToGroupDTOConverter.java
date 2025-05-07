package com.moura.authorization.groups.mappers.converter;

import com.moura.authorization.groups.dtos.GroupDTO;
import com.moura.authorization.groups.entities.Group;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class GroupToGroupDTOConverter {

    public Set<GroupDTO> convert(Set<Group> groups) {
        if (groups == null || groups.isEmpty()) return Collections.emptySet();
        return groups.stream()
                .map(g -> new GroupDTO(g.getId(), g.getName()))
                .collect(Collectors.toSet());
    }
}