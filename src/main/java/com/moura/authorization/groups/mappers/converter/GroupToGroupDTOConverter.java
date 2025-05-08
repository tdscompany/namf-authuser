package com.moura.authorization.groups.mappers.converter;

import com.moura.authorization.groups.dtos.GroupOutputDTO;
import com.moura.authorization.groups.entities.Group;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.springframework.stereotype.Component;

@Component
public class GroupToGroupDTOConverter implements Converter<Group, GroupOutputDTO> {

    @Override
    public GroupOutputDTO convert(MappingContext<Group, GroupOutputDTO> context) {
        Group group = context.getSource();

        return GroupOutputDTO.builder()
                .id(group.getId())
                .name(group.getName())
                .color(group.getColor())
                .permission(group.getPermissions())
                .createdAt(group.getCreatedAt())
                .updatedAt(group.getUpdatedAt())
                .build();
    }
}