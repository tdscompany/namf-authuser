package com.moura.authorization.users.mappers;

import com.moura.authorization.groups.mappers.converter.GroupIdToGroupConverter;
import com.moura.authorization.users.dtos.UserDTO;
import com.moura.authorization.users.entities.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserMapperConfigurerTest {

    @Mock
    private GroupIdToGroupConverter groupIdToGroupConverter;

    private final ModelMapper modelMapper = new ModelMapper();

    @Test
    void shouldConfigureUserDTOToUserMapping() {
        UserMapperConfigurer configurer = new UserMapperConfigurer(groupIdToGroupConverter);
        configurer.configure(modelMapper);

        TypeMap<UserDTO, User> typeMap = modelMapper.getTypeMap(UserDTO.class, User.class);
        assertNotNull(typeMap);
    }
}