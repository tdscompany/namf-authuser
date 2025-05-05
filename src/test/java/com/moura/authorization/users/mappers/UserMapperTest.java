package com.moura.authorization.users.mappers;

import com.moura.authorization.groups.entities.Group;
import com.moura.authorization.groups.mappers.converter.GroupIdToGroupConverter;
import com.moura.authorization.groups.mappers.converter.GroupToGroupDTOConverter;
import com.moura.authorization.groups.repositories.GroupRepository;
import com.moura.authorization.mappers.UserMapperConfigurer;
import com.moura.authorization.users.dtos.UserDTO;
import com.moura.authorization.users.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {

    @Mock
    private GroupRepository groupRepository;

    private ModelMapper modelMapper;

    @BeforeEach
    void setUp() {
        GroupToGroupDTOConverter toDtoConverter = new GroupToGroupDTOConverter();
        GroupIdToGroupConverter toEntityConverter = new GroupIdToGroupConverter(groupRepository);
        UserMapperConfigurer configurer = new UserMapperConfigurer(toEntityConverter, toDtoConverter);

        modelMapper = new ModelMapper();
        modelMapper.addConverter(toDtoConverter);
        modelMapper.addConverter(toEntityConverter);
        configurer.configure(modelMapper);
    }

    @Test
    @DisplayName("Should map User entity to UserDTO excluding sensitive fields")
    void shouldMapUserToUserDTO() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setName("Test");
        user.setEmail("test@example.com");
        user.setTelefone("123");

        UserDTO dto = modelMapper.map(user, UserDTO.class);

        assertEquals("Test", dto.getName());
        assertEquals("test@example.com", dto.getEmail());
        assertEquals("123", dto.getTelefone());
        assertNull(dto.getPassword());
        assertNull(dto.getOldPassword());
        assertNull(dto.getGroupIds());
    }

    @Test
    @DisplayName("Should map UserDTO to User and convert group IDs to Group entities")
    void shouldMapUserDTOToUser() {
        UUID groupId = UUID.randomUUID();
        UserDTO dto = new UserDTO();
        dto.setName("Test");
        dto.setEmail("test@example.com");
        dto.setGroupIds(Set.of(groupId));

        Group group = new Group();
        group.setId(groupId);
        group.setName("Test Group");
        when(groupRepository.findAllById(Set.of(groupId))).thenReturn(List.of(group));

        User entity = modelMapper.map(dto, User.class);

        assertEquals("Test", entity.getName());
        assertEquals("test@example.com", entity.getEmail());
        assertNotNull(entity.getGroups());
        assertEquals(1, entity.getGroups().size());
        assertTrue(entity.getGroups().stream().anyMatch(g -> g.getId().equals(groupId)));
    }

    @Test
    @DisplayName("Should return empty groups when groupIds is null or empty")
    void shouldHandleEmptyGroupIds() {
        UserDTO dtoWithNull = new UserDTO();
        dtoWithNull.setGroupIds(null);

        UserDTO dtoWithEmpty = new UserDTO();
        dtoWithEmpty.setGroupIds(Collections.emptySet());

        User userFromNull = modelMapper.map(dtoWithNull, User.class);
        User userFromEmpty = modelMapper.map(dtoWithEmpty, User.class);

        assertNotNull(userFromNull.getGroups());
        assertTrue(userFromNull.getGroups().isEmpty());

        assertNotNull(userFromEmpty.getGroups());
        assertTrue(userFromEmpty.getGroups().isEmpty());

        verify(groupRepository, never()).findAllById(any());
    }
}