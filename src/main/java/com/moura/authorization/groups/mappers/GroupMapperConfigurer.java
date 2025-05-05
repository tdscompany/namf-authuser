package com.moura.authorization.groups.mappers;

import com.moura.authorization.configs.TypeMapConfigurer;
import com.moura.authorization.groups.dtos.GroupDTO;
import com.moura.authorization.groups.entities.Group;
import com.moura.authorization.groups.mappers.converter.IdToPermissionConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class GroupMapperConfigurer implements TypeMapConfigurer<GroupDTO, Group> {

    private final IdToPermissionConverter idToPermissionConverter;

    public GroupMapperConfigurer(IdToPermissionConverter idToPermissionConverter) {
        this.idToPermissionConverter = idToPermissionConverter;
    }

    @Override
    public void configure(ModelMapper modelMapper) {
        modelMapper.typeMap(GroupDTO.class, Group.class)
                .addMappings(mapper -> {
                    mapper.using(idToPermissionConverter)
                            .map(GroupDTO::getPermissionIds, Group::setPermissions);

                    mapper.skip(Group::setId);
                    mapper.skip(Group::setCreatedAt);
                    mapper.skip(Group::setUpdatedAt);
                    mapper.skip(Group::setCreatedBy);
                    mapper.skip(Group::setUpdatedBy);
                    mapper.skip(Group::setVersion);
                });

        modelMapper.typeMap(Group.class, GroupDTO.class)
                .addMappings(mapper -> {
                    mapper.map(Group::getPermissions, GroupDTO::setPermissions);
                });
    }
}
