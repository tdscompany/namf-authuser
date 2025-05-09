package com.moura.authorization.groups.services.impl;

import com.moura.authorization.context.TenantContext;
import com.moura.authorization.event.publisher.EventPublisher;
import com.moura.authorization.exceptions.AlreadyExistsException;
import com.moura.authorization.exceptions.NotFoundException;
import com.moura.authorization.groups.entities.Group;
import com.moura.authorization.groups.repositories.GroupRepository;
import com.moura.authorization.utils.MessageUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupServiceImplTest {

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private MessageSource messageSource;

    @Mock
    private EventPublisher eventPublisher;

    @InjectMocks
    private GroupServiceImpl groupService;

    private final UUID TENANT_ID = UUID.randomUUID();

    @BeforeEach
    void setup() {
        TenantContext.setCurrentTenant(TENANT_ID);
        MessageUtils.messageSource = messageSource;

        lenient().when(messageSource.getMessage(anyString(), any(), any())).thenReturn("MOCK");
    }

    @Test
    void shouldCreateGroupSuccessfully() {
        Group input = new Group();
        input.setName("Admin");
        input.setColor("red");

        Group saved = new Group();
        saved.setId(UUID.randomUUID());
        saved.setName("Admin");
        saved.setColor("red");

        when(groupRepository.existsByName(anyString(), any())).thenReturn(false);
        when(groupRepository.save(any())).thenReturn(saved);

        Group result = groupService.create(input);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo("Admin");
        verify(groupRepository).save(input);
    }

    @Test
    void shouldThrowAlreadyExistsExceptionWhenGroupNameAlreadyExists() {
        Group input = new Group();
        input.setName("Admin");

        when(groupRepository.existsByName(anyString(), any())).thenReturn(true);

        assertThatThrownBy(() -> groupService.create(input))
                .isInstanceOf(AlreadyExistsException.class);
    }

    @Test
    void shouldReturnEnrichedPageWhenFindAll() {
        Group group = new Group();
        group.setId(UUID.randomUUID());

        Page<Group> page = new PageImpl<>(List.of(group));

        when(groupRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
        when(groupRepository.findAllWithPermissionsByIds(any())).thenReturn(List.of(group));

        Page<Group> result = groupService.findAll(mock(Specification.class), Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(group.getId());
    }

    @Test
    void shouldReturnGroupById() {
        UUID id = UUID.randomUUID();
        Group group = new Group();
        group.setId(id);

        when(groupRepository.findByIdAndTenant(id, TENANT_ID)).thenReturn(Optional.of(group));

        Group result = groupService.findById(id);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenGroupNotFoundById() {
        UUID id = UUID.randomUUID();

        when(groupRepository.findByIdAndTenant(id, TENANT_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> groupService.findById(id))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldUpdateGroupSuccessfully() {
        Group group = new Group();
        group.setId(UUID.randomUUID());
        group.setName("Admin");

        when(groupRepository.existsByNameAndIdNot(any(), any(), any())).thenReturn(false);
        when(groupRepository.save(group)).thenReturn(group);

        Group result = groupService.update(group);

        assertThat(result).isEqualTo(group);
        verify(groupRepository).save(group);
    }

    @Test
    void shouldThrowAlreadyExistsExceptionOnUpdate() {
        Group group = new Group();
        group.setId(UUID.randomUUID());
        group.setName("Admin");

        when(groupRepository.existsByNameAndIdNot(any(), any(), any())).thenReturn(true);

        assertThatThrownBy(() -> groupService.update(group))
                .isInstanceOf(AlreadyExistsException.class);
    }

    @Test
    void shouldValidateGroupIdsSuccessfully() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        Set<UUID> ids = Set.of(id1, id2);

        Group g1 = new Group(); g1.setId(id1);
        Group g2 = new Group(); g2.setId(id2);

        when(groupRepository.findExistingIdsByTenant(ids, TENANT_ID)).thenReturn(Set.of(g1, g2));

        Set<Group> result = groupService.validateGroupIds(ids);

        assertThat(result).containsExactlyInAnyOrder(g1, g2);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenInvalidGroupIds() {
        UUID id1 = UUID.randomUUID();
        Set<UUID> ids = Set.of(id1);

        when(groupRepository.findExistingIdsByTenant(ids, TENANT_ID)).thenReturn(Set.of());

        assertThatThrownBy(() -> groupService.validateGroupIds(ids))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldReturnEmptySetWhenGroupIdsIsNullOrEmpty() {
        Set<Group> result = groupService.validateGroupIds(Collections.emptySet());
        assertThat(result).isEmpty();
    }

    @Test
    void shouldInactivateGroupSuccessfully() {
        UUID groupId = UUID.randomUUID();
        Group group = new Group();
        group.setId(groupId);

        when(groupRepository.findByIdAndTenant(groupId, TENANT_ID)).thenReturn(Optional.of(group));

        groupService.inactivate(groupId);

        verify(groupRepository).deleteGroupAssociations(groupId);
        verify(groupRepository).deleteById(groupId);
    }
}