package com.moura.authorization.users.mappers;

import com.moura.authorization.groups.entities.Group;
import com.moura.authorization.groups.repositories.GroupRepository;
import com.moura.authorization.users.dtos.UserDTO;
import com.moura.authorization.users.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {

    @Mock
    private GroupRepository groupRepository;

    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapper(groupRepository);
    }

    @Test
    @DisplayName("Should map User entity to UserDTO and exclude sensitive fields like password and groups")
    void shouldMapUserEntityToUserDtoWithoutSensitiveFields() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setName("Test");
        user.setEmail("test@example.com");
        user.setTelefone("123");
        user.setGroups(new HashSet<>());

        UserDTO dto = userMapper.toDTO(user);

        assertEquals(user.getName(), dto.getName());
        assertEquals(user.getEmail(), dto.getEmail());
        assertEquals(user.getTelefone(), dto.getTelefone());
        assertNull(dto.getPassword());
        assertNull(dto.getOldPassword());
        assertNull(dto.getGroupIds());
    }

    @Test
    @DisplayName("Should update only non-null fields from UserDTO to User entity")
    void shouldUpdateOnlyNonNullFieldsFromDtoToEntity() {
        UserDTO dto = new UserDTO();
        dto.setName("Updated Name");
        dto.setEmail(null);

        User user = new User();
        user.setName("Old Name");
        user.setEmail("old@example.com");

        userMapper.updateEntityFromDTO(dto, user);

        assertEquals("Updated Name", user.getName());
        assertEquals("old@example.com", user.getEmail());
    }

    @Test
    @DisplayName("Should convert paginated User entities to paginated UserDTOs")
    void shouldConvertUserPageToDtoPage() {
        User user1 = new User();
        user1.setName("User1");

        Page<User> page = new PageImpl<>(List.of(user1));
        Page<UserDTO> dtoPage = userMapper.toDTOPage(page);

        assertEquals(1, dtoPage.getTotalElements());
        assertEquals("User1", dtoPage.getContent().getFirst().getName());
    }

    @Test
    @DisplayName("Should return empty groups when groupIds is null or empty")
    void shouldReturnEmptyGroupsWhenGroupIdsIsNullOrEmpty() {
        UserDTO dtoWithNull = new UserDTO();
        dtoWithNull.setGroupIds(null);

        UserDTO dtoWithEmpty = new UserDTO();
        dtoWithEmpty.setGroupIds(Collections.emptySet());

        User userFromNull = userMapper.toEntity(dtoWithNull);
        User userFromEmpty = userMapper.toEntity(dtoWithEmpty);

        assertNotNull(userFromNull.getGroups());
        assertTrue(userFromNull.getGroups().isEmpty());

        assertNotNull(userFromEmpty.getGroups());
        assertTrue(userFromEmpty.getGroups().isEmpty());

        verify(groupRepository, never()).findAll((Example<Group>) any());
    }
}