package com.moura.authorization.groups.mappers.converter;

import com.moura.authorization.groups.dtos.GroupRegistrationDTO;
import com.moura.authorization.groups.entities.Group;
import com.moura.authorization.groups.entities.Permission;
import com.moura.authorization.groups.services.PermissionService;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

@Component
public class GroupRegistrationDTOToGroupConverter implements Converter<GroupRegistrationDTO, Group> {

    private final PermissionService permissionService;

    public GroupRegistrationDTOToGroupConverter(PermissionService permissionService) {
        this.permissionService = permissionService;
    }


    private Set<Permission> fetchGroups(Set<UUID> groupIds) {
        return permissionService.validatePermissionIds(groupIds);
    }

    @Override
    public Group convert(MappingContext<GroupRegistrationDTO, Group> context) {
        GroupRegistrationDTO dto = context.getSource();

        return Group.builder()
                .name(dto.name())
                .color(dto.color())
                .permissions(fetchGroups(dto.permissionIds()))
                .build();
    }
}