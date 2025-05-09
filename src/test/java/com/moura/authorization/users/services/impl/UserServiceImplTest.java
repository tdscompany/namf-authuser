package com.moura.authorization.users.services.impl;

import com.moura.authorization.context.TenantContext;
import com.moura.authorization.exceptions.AlreadyExistsException;
import com.moura.authorization.groups.entities.Group;
import com.moura.authorization.groups.services.GroupService;
import com.moura.authorization.users.dtos.UserFilterDTO;
import com.moura.authorization.users.entities.Credentials;
import com.moura.authorization.users.entities.User;
import com.moura.authorization.users.enums.UserStatus;
import com.moura.authorization.users.repositories.UserRepository;
import com.moura.authorization.users.repositories.specification.UserSpecification;
import com.moura.authorization.users.services.CredentialsService;
import com.moura.authorization.utils.MessageUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CredentialsService credentialsService;

    @Mock
    private PasswordEncoder passwordEncoder;

    private User user;

    @BeforeEach
    void setup() {
        user = new User();
        user.setPasswordNotEncoded("123");
        user.setGroups(Set.of(new Group()));
        TenantContext.setCurrentTenant(UUID.fromString("00000000-0000-0000-0000-000000000001"));
    }

    @Test
    @DisplayName("Should throw exception when creating a user with an existing email")
    void shouldThrow_whenCreatingUserWithExistingEmail() {
        String email = "existing@email.com";
        user.setEmail(email);

        when(userRepository.existsByEmail(email)).thenReturn(true);

        try (MockedStatic<MessageUtils> mocked = mockStatic(MessageUtils.class)) {
            mocked.when(() -> MessageUtils.get(anyString()))
                    .thenReturn("any");

            assertThrows(AlreadyExistsException.class, () -> userService.create(user));
        }
    }

    @Test
    @DisplayName("Should not throw exception when creating a user with a unique email")
    void shouldNotThrow_whenCreatingUserWithUniqueEmail() {
        String email = "unique@email.com";
        user.setEmail(email);

        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(userRepository.save(any())).thenReturn(user);

        assertDoesNotThrow(() -> userService.create(user));
    }

    @Test
    @DisplayName("Should throw exception when updating a user with an email already used by another user")
    void shouldThrow_whenUpdatingUserWithEmailUsedByOther() {
        UUID id = UUID.randomUUID();
        String email = "used@other.com";

        when(userRepository.existsByEmailAndIdNot(email, id)).thenReturn(true);

        User user = new User();
        user.setId(id);
        user.setEmail(email);

        try (MockedStatic<MessageUtils> mocked = mockStatic(MessageUtils.class)) {
            mocked.when(() -> MessageUtils.get(anyString()))
                    .thenReturn("any");

            assertThrows(AlreadyExistsException.class, () -> userService.update(user));
        }
    }

    @Test
    @DisplayName("Should not throw exception when updating a user with their own email")
    void shouldNotThrow_whenUpdatingUserWithSameEmail() {
        UUID id = UUID.randomUUID();
        String email = "user@own.com";

        user.setId(id);
        user.setEmail(email);

        when(userRepository.existsByEmailAndIdNot(email, id)).thenReturn(false);
        when(userRepository.save(any())).thenReturn(user);

        assertDoesNotThrow(() -> userService.update(user));
    }

    @Test
    @DisplayName("Should return paged users with groups when filters are applied")
    void findAll_shouldReturnPagedUsersWithGroups() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setName("João");
        user.setEmail("joao@example.com");
        user.setUserStatus(UserStatus.ACTIVE);
        user.setOrganizationId(TenantContext.getCurrentTenant());

        List<User> pageContent = List.of(user);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        Page<User> page = new PageImpl<>(pageContent, pageable, 1);

        when(userRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(page);

        when(userRepository.findAllWithGroupsByIds(anyList()))
                .thenReturn(pageContent);

        UserFilterDTO filter = new UserFilterDTO();
        filter.setName("João");
        filter.setUserStatus(UserStatus.ACTIVE);

        Page<User> result = userService.findAll(UserSpecification.of(filter), pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("João", result.getContent().get(0).getName());

        verify(userRepository).findAll(any(Specification.class), eq(pageable));
        verify(userRepository).findAllWithGroupsByIds(List.of(user.getId()));
    }


    @Test
    @DisplayName("Should filter users by group ID")
    void findAll_shouldFilterByGroupId() {
        UUID userId = UUID.randomUUID();
        UUID groupId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);
        user.setName("Maria");
        user.setEmail("maria@example.com");
        user.setUserStatus(UserStatus.ACTIVE);
        user.setOrganizationId(TenantContext.getCurrentTenant());

        Page<User> page = new PageImpl<>(List.of(user));
        Pageable pageable = PageRequest.of(0, 10);

        when(userRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(page);
        when(userRepository.findAllWithGroupsByIds(List.of(userId)))
                .thenReturn(List.of(user));

        UserFilterDTO filter = new UserFilterDTO();
        filter.setGroupId(groupId);

        Page<User> result = userService.findAll(UserSpecification.of(filter), pageable);

        assertEquals(1, result.getTotalElements());
        verify(userRepository).findAll(any(Specification.class), eq(pageable));
        verify(userRepository).findAllWithGroupsByIds(List.of(userId));
    }

    @Test
    @DisplayName("Should filter users by email")
    void findAll_shouldFilterByEmail() {
        // Arrange
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setName("Carlos");
        user.setEmail("carlos@dominio.com");
        user.setUserStatus(UserStatus.ACTIVE);
        user.setOrganizationId(TenantContext.getCurrentTenant());

        Page<User> page = new PageImpl<>(List.of(user));
        Pageable pageable = PageRequest.of(0, 10);

        when(userRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(page);
        when(userRepository.findAllWithGroupsByIds(List.of(userId)))
                .thenReturn(List.of(user));

        UserFilterDTO filter = new UserFilterDTO();
        filter.setEmail("carlos");

        Page<User> result = userService.findAll(UserSpecification.of(filter), pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Carlos", result.getContent().get(0).getName());
    }

    @Test
    @DisplayName("Should return empty page when no user is found")
    void findAll_shouldReturnEmptyPageWhenNoUserFound() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> emptyPage = Page.empty();

        when(userRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(emptyPage);

        UserFilterDTO filter = new UserFilterDTO();
        filter.setName("Não existe");

        Page<User> result = userService.findAll(UserSpecification.of(filter), pageable);

        assertTrue(result.isEmpty());
        verify(userRepository).findAll(any(Specification.class), eq(pageable));
        verify(userRepository, never()).findAllWithGroupsByIds(any());
    }

    @Test
    @DisplayName("Should save user and create credentials")
    void create_shouldSaveUserAndCreateCredentials() {
        User userToSave = new User();
        userToSave.setEmail("joao@example.com");
        userToSave.setName("João");
        userToSave.setPasswordNotEncoded("123456");
        userToSave.setGroups(Set.of());

        User savedUser = new User();
        savedUser.setId(UUID.randomUUID());
        savedUser.setEmail("joao@example.com");
        savedUser.setName("João");
        savedUser.setUserStatus(UserStatus.ACTIVE);
        savedUser.setOrganizationId(TenantContext.getCurrentTenant());

        when(userRepository.existsByEmail("joao@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(passwordEncoder.encode("123456")).thenReturn("encodedPassword");

        User created = userService.create(userToSave);

        assertEquals(savedUser.getEmail(), created.getEmail());
        assertEquals(UserStatus.ACTIVE, created.getUserStatus());
        assertEquals(TenantContext.getCurrentTenant(), created.getOrganizationId());

        verify(userRepository).save(any(User.class));
        verify(credentialsService).create(any(Credentials.class));
        verify(passwordEncoder).encode("123456");
    }

    @Test
    @DisplayName("Should validate and update user details")
    void update_shouldValidateAndUpdateUser() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setEmail("atualizar@example.com");
        user.setGroups(Set.of());

        when(userRepository.existsByEmailAndIdNot("atualizar@example.com", userId)).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);

        User updated = userService.update(user);

        assertEquals(user.getEmail(), updated.getEmail());
        verify(userRepository).save(user);
        verify(userRepository).existsByEmailAndIdNot(user.getEmail(), user.getId());
    }

    @Test
    @DisplayName("Should set user status to deleted and save when inactivated")
    void inactivate_shouldSetUserStatusToDeletedAndSave() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUserStatus(UserStatus.ACTIVE);

        userService.inactivate(user);

        assertEquals(UserStatus.DELETED, user.getUserStatus());
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Should return empty page when no users are found")
    void findAll_shouldReturnEmptyPage_whenNoUsersFound() {
        Specification<User> spec = mock(Specification.class);
        Pageable pageable = PageRequest.of(0, 10);

        when(userRepository.findAll(spec, pageable)).thenReturn(Page.empty());

        Page<User> result = userService.findAll(spec, pageable);

        assertTrue(result.isEmpty());
        verify(userRepository, never()).findAllWithGroupsByIds(anyList());
    }

    @Test
    @DisplayName("Should return a page with users with groups when available")
    void findAll_shouldReturnPageWithUsersWithGroups() {
        Specification<User> spec = mock(Specification.class);
        Pageable pageable = PageRequest.of(0, 10);

        User user1 = new User();
        user1.setId(UUID.randomUUID());
        User user2 = new User();
        user2.setId(UUID.randomUUID());

        Page<User> userPage = new PageImpl<>(List.of(user1, user2), pageable, 2);

        when(userRepository.findAll(spec, pageable)).thenReturn(userPage);

        User enriched1 = new User();
        enriched1.setId(user1.getId());
        enriched1.setGroups(Set.of(new Group())); // Simula grupo

        User enriched2 = new User();
        enriched2.setId(user2.getId());
        enriched2.setGroups(Set.of(new Group()));

        when(userRepository.findAllWithGroupsByIds(List.of(user1.getId(), user2.getId())))
                .thenReturn(List.of(enriched1, enriched2));

        Page<User> result = userService.findAll(spec, pageable);

        assertEquals(2, result.getContent().size());
        assertTrue(result.getContent().stream().allMatch(u -> !u.getGroups().isEmpty()));
        verify(userRepository).findAllWithGroupsByIds(List.of(user1.getId(), user2.getId()));
    }

    @Test
    @DisplayName("Should preserve user order from original page when enriched")
    void findAll_shouldPreserveUserOrderFromOriginalPage() {
        Specification<User> spec = mock(Specification.class);
        Pageable pageable = PageRequest.of(0, 10);

        User user1 = new User(); user1.setId(UUID.randomUUID());
        User user2 = new User(); user2.setId(UUID.randomUUID());

        Page<User> originalPage = new PageImpl<>(List.of(user1, user2), pageable, 2);
        when(userRepository.findAll(spec, pageable)).thenReturn(originalPage);

        User enriched2 = new User(); enriched2.setId(user2.getId());
        User enriched1 = new User(); enriched1.setId(user1.getId());

        when(userRepository.findAllWithGroupsByIds(anyList()))
                .thenReturn(List.of(enriched2, enriched1));

        Page<User> result = userService.findAll(spec, pageable);

        assertEquals(user1.getId(), result.getContent().get(0).getId());
        assertEquals(user2.getId(), result.getContent().get(1).getId());
    }

    @Test
    @DisplayName("Should not save when user is already deleted")
    void inactivate_shouldNotSave_whenUserIsAlreadyDeleted() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUserStatus(UserStatus.DELETED);

        userService.inactivate(user);

        assertEquals(UserStatus.DELETED, user.getUserStatus());
        verify(userRepository, never()).save(any());
    }
}