package com.moura.authorization.groups.mappers.converter;

import com.moura.authorization.groups.entities.Group;
import com.moura.authorization.groups.repositories.GroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.spi.MappingContext;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
class GroupIdToGroupConverterTest {
    @Mock
    private GroupRepository groupRepository;

    private GroupIdToGroupConverter converter;

    @BeforeEach
    void setup() {
        converter = new GroupIdToGroupConverter(groupRepository);
    }

    @Test
    void shouldConvertGroupIdsToGroups() {
        UUID groupId = UUID.randomUUID();
        Group group = new Group();
        group.setId(groupId);
        group.setName("Admin");

        when(groupRepository.findAllById(Set.of(groupId))).thenReturn(List.of(group));

        MappingContext<Set<UUID>, Set<Group>> context = mock(MappingContext.class);
        when(context.getSource()).thenReturn(Set.of(groupId));

        Set<Group> result = converter.convert(context);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(group));
    }

    @Test
    void shouldReturnEmptySetWhenSourceIsNull() {
        MappingContext<Set<UUID>, Set<Group>> context = mock(MappingContext.class);
        when(context.getSource()).thenReturn(null);

        Set<Group> result = converter.convert(context);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnEmptySetWhenSourceIsEmpty() {
        MappingContext<Set<UUID>, Set<Group>> context = mock(MappingContext.class);
        when(context.getSource()).thenReturn(Collections.emptySet());

        Set<Group> result = converter.convert(context);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}