package com.moura.authorization.groups.mappers.converter;

import com.moura.authorization.groups.entities.Permission;
import com.moura.authorization.groups.repositories.PermissionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.spi.MappingContext;
import org.springframework.data.mapping.context.AbstractMappingContext;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IdToPermissionConverterTest {

    @Mock
    private PermissionRepository permissionRepository;

    private IdToPermissionConverter converter;

    @BeforeEach
    void setup() {
        converter = new IdToPermissionConverter(permissionRepository);
    }

    @Test
    void shouldReturnPermissionsFromIds() {
        UUID id = UUID.randomUUID();
        Permission permission = new Permission();
        permission.setId(id);
        permission.setName("PERMISSION_TEST");

        when(permissionRepository.findAllById(Set.of(id))).thenReturn(List.of(permission));

        MappingContext<Set<UUID>, Set<Permission>> context = mock(MappingContext.class);
        when(context.getSource()).thenReturn(Set.of(id));

        Set<Permission> result = converter.convert(context);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(permission));
    }

    @Test
    void shouldReturnEmptySetWhenInputIsNull() {
        MappingContext<Set<UUID>, Set<Permission>> context = mock(MappingContext.class);
        when(context.getSource()).thenReturn(null);

        Set<Permission> result = converter.convert(context);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnEmptySetWhenInputIsEmpty() {
        MappingContext<Set<UUID>, Set<Permission>> context = mock(MappingContext.class);
        when(context.getSource()).thenReturn(Collections.emptySet());

        Set<Permission> result = converter.convert(context);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}