package com.moura.authorization.groups.mappers.converter;

import com.moura.authorization.groups.dtos.GroupDTO;
import com.moura.authorization.groups.entities.Group;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.spi.MappingContext;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GroupToGroupDTOConverterTest {

    private GroupToGroupDTOConverter converter;

    @BeforeEach
    void setup() {
        converter = new GroupToGroupDTOConverter();
    }

    @Test
    void shouldConvertGroupsToGroupDTOs() {
        UUID groupId = UUID.randomUUID();
        Group group = new Group();
        group.setId(groupId);
        group.setName("Finance");

        MappingContext<Set<Group>, Set<GroupDTO>> context = mock(MappingContext.class);
        when(context.getSource()).thenReturn(Set.of(group));

        Set<GroupDTO> result = converter.convert(context);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.stream().anyMatch(dto -> dto.getId().equals(groupId) && dto.getName().equals("Finance")));
    }

    @Test
    void shouldReturnEmptySetWhenSourceIsNull() {
        MappingContext<Set<Group>, Set<GroupDTO>> context = mock(MappingContext.class);
        when(context.getSource()).thenReturn(null);

        Set<GroupDTO> result = converter.convert(context);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}