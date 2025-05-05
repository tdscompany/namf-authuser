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
public class GroupToGroupDTOConverter implements Converter<Set<Group>, Set<GroupDTO>> {
    @Override
    public Set<GroupDTO> convert(MappingContext<Set<Group>, Set<GroupDTO>> context) {
        if (context.getSource() == null) return Collections.emptySet();
        return context.getSource().stream()
                .map(g -> new GroupDTO(g.getId(), g.getName()))
                .collect(Collectors.toSet());
    }
}