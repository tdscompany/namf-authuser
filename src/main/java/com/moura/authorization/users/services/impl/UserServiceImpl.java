package com.moura.authorization.users.services.impl;

import com.moura.authorization.configs.security.AuthenticationCurrentUserService;
import com.moura.authorization.context.TenantContext;
import com.moura.authorization.enums.UserStatus;
import com.moura.authorization.users.entities.Credentials;
import com.moura.authorization.users.entities.User;
import com.moura.authorization.users.repositories.UserRepository;
import com.moura.authorization.users.services.UserService;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationCurrentUserService authenticationCurrentUserService;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationCurrentUserService authenticationCurrentUserService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationCurrentUserService = authenticationCurrentUserService;
    }

    @Override
    public Optional<User> findById(UUID userId) {
        return Optional.empty();
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional
    @Override
    public User createUser(User entity) {
        User currentUser = authenticationCurrentUserService.getCurrentUser();
        UUID tenant = TenantContext.getCurrentTenant();

        entity.setOrganizationId(tenant);

        Credentials credentials = new Credentials();
        credentials.setPassword(passwordEncoder.encode(entity.getPasswordNotEncoded()));
        credentials.setActive(true);
        credentials.setCreatedBy(currentUser);
        credentials.setUser(entity);

        entity.setCredentials(Set.of(credentials));
        entity.setUserStatus(UserStatus.ACTIVE);

        return userRepository.save(entity);
    }
}
