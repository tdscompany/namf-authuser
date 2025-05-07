package com.moura.authorization.groups.mappers.converter;

import com.moura.authorization.groups.dtos.GroupDTO;
import com.moura.authorization.groups.entities.Group;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.spi.MappingContext;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GroupToGroupDTOConverterTest {

    private GroupToGroupDTOConverter converter;

    @BeforeEach
    void setup() {
        converter = new GroupToGroupDTOConverter();
    }

    @Test
    void shouldConvertGroupSetToGroupDTOSet() {
        Group group = new Group();
        group.setId(UUID.randomUUID());
        group.setName("Admin");
        Group group2 = new Group();
        group2.setId(UUID.randomUUID());
        group2.setName("User");

        Set<Group> groups = Set.of(group, group2);
        Set<GroupDTO> result = converter.convert(groups);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(GroupDTO::getName).containsExactlyInAnyOrder("Admin", "User");
    }

    @Test
    void shouldReturnEmptySetWhenNull() {
        assertThat(converter.convert(null)).isEmpty();
    }
}