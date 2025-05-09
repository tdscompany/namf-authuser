package com.moura.authorization.groups.mappers.converter;

import com.moura.authorization.groups.dtos.GroupPutDTO;
import com.moura.authorization.groups.entities.Group;
import com.moura.authorization.groups.entities.Permission;
import com.moura.authorization.groups.services.PermissionService;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

@Component
public class GroupPutDTOToGroupConverter implements Converter<GroupPutDTO, Group> {


    private final PermissionService permissionService;

    public GroupPutDTOToGroupConverter(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    private Set<Permission> fetchPermissions(Set<UUID> groupIds) {
        return permissionService.validatePermissionIds(groupIds);
    }

    @Override
    public Group convert(MappingContext<GroupPutDTO, Group> context) {
        GroupPutDTO dto = context.getSource();
        Group group = context.getDestination();

        if (dto.name() != null) group.setName(dto.name());
        if (dto.color() != null) group.setColor(dto.color());
        if (dto.permissionIds() != null) group.setPermissions(fetchPermissions(dto.permissionIds()));

        return group;
    }

}

