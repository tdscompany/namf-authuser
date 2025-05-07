package com.moura.authorization.users.mappers.converter;

import com.moura.authorization.event.entities.Event;
import com.moura.authorization.event.enums.EventGroup;
import com.moura.authorization.event.enums.EventType;
import com.moura.authorization.event.repositories.EventRepository;
import com.moura.authorization.groups.dtos.GroupDTO;
import com.moura.authorization.groups.entities.Group;
import com.moura.authorization.groups.mappers.converter.GroupToGroupDTOConverter;
import com.moura.authorization.users.dtos.UserDTO;
import com.moura.authorization.users.dtos.UserOutputDTO;
import com.moura.authorization.users.entities.User;
import com.moura.authorization.users.enums.UserStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.spi.MappingContext;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserToUserDTOConverterTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private GroupToGroupDTOConverter groupToGroupDTOConverter;

    @InjectMocks
    private UserToUserDTOConverter converter;

    @Test
    void shouldConvertUserToUserDTOWithAllFields() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setEmail("test@example.com");
        user.setName("Test User");
        user.setTelefone("123456789");
        user.setDescription("Some description");
        user.setUserStatus(UserStatus.ACTIVE);
        user.setCreatedAt(LocalDateTime.now());
        user.setGroups(Set.of(new Group(UUID.randomUUID(), "Admin")));

        LocalDateTime lastAccess = LocalDateTime.now().minusDays(1);
        Set<GroupDTO> groupDTOs = Set.of(new GroupDTO(UUID.randomUUID(), "Admin"));

        when(eventRepository.findLatestByAuthorIdAndEventGroupAndEvent(
                eq(userId), eq(EventGroup.AUTH.name()), eq(EventType.AUTH_SUCCESS.name())
        )).thenReturn(Optional.of(new Event(lastAccess)));

        when(groupToGroupDTOConverter.convert(any())).thenReturn(groupDTOs);

        MappingContext<User, UserOutputDTO> context = mock(MappingContext.class);
        when(context.getSource()).thenReturn(user);

        UserOutputDTO dto = converter.convert(context);

        assertEquals(user.getId(), dto.id());
        assertEquals(user.getEmail(), dto.email());
        assertEquals(user.getName(), dto.name());
        assertEquals(user.getTelefone(), dto.telefone());
        assertEquals(user.getDescription(), dto.description());
        assertEquals(user.getUserStatus(), dto.userStatus());
        assertEquals(user.getCreatedAt(), dto.createdAt());
        assertEquals(groupDTOs, dto.groups());
        assertEquals(lastAccess, dto.lastAccess());
    }

    @Test
    void shouldHandleNoLastAccessEvent() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setGroups(Collections.emptySet());

        when(eventRepository.findLatestByAuthorIdAndEventGroupAndEvent(
                eq(userId), any(), any())).thenReturn(Optional.empty());

        when(groupToGroupDTOConverter.convert(any())).thenReturn(Collections.emptySet());

        MappingContext<User, UserOutputDTO> context = mock(MappingContext.class);
        when(context.getSource()).thenReturn(user);

        UserOutputDTO dto = converter.convert(context);

        assertNull(dto.lastAccess());
    }
}