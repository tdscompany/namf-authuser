package com.moura.authorization.users.services.impl;

import com.moura.authorization.exceptions.AlreadyExistsException;
import com.moura.authorization.groups.entities.Group;
import com.moura.authorization.groups.services.GroupService;
import com.moura.authorization.users.entities.User;
import com.moura.authorization.users.repositories.UserRepository;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CredentialsService credentialsService;

    @Mock
    private GroupService groupService;

    @Mock
    private PasswordEncoder passwordEncoder;

    private User user;

    @BeforeEach
    void setup() {
        user = new User();
        user.setPasswordNotEncoded("123");
        user.setGroups(Set.of(new Group()));
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
}