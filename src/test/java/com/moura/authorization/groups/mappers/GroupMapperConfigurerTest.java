package com.moura.authorization.groups.mappers;

import com.moura.authorization.groups.dtos.GroupDTO;
import com.moura.authorization.groups.entities.Group;
import com.moura.authorization.groups.mappers.converter.IdToPermissionConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GroupMapperConfigurerTest {

    @Mock
    private IdToPermissionConverter idToPermissionConverter;

    private final ModelMapper modelMapper = new ModelMapper();

    @Test
    void shouldConfigureGroupMappings() {
        GroupMapperConfigurer configurer = new GroupMapperConfigurer(idToPermissionConverter);
        configurer.configure(modelMapper);

        assertNotNull(modelMapper.getTypeMap(GroupDTO.class, Group.class));
        assertNotNull(modelMapper.getTypeMap(Group.class, GroupDTO.class));
    }
}