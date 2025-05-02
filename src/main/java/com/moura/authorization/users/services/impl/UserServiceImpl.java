package com.moura.authorization.users.services.impl;

import com.moura.authorization.context.TenantContext;
import com.moura.authorization.users.enums.UserStatus;
import com.moura.authorization.users.entities.Credentials;
import com.moura.authorization.users.entities.User;
import com.moura.authorization.users.repositories.UserRepository;
import com.moura.authorization.users.services.CredentialsService;
import com.moura.authorization.users.services.UserService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final CredentialsService credentialsService;

    private final PasswordEncoder passwordEncoder;


    public UserServiceImpl(UserRepository userRepository, CredentialsService credentialsService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.credentialsService = credentialsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Optional<User> findById(UUID userId) {
        var tenantId = TenantContext.getCurrentTenant();
        return userRepository.findByIdAndTenant(userId, tenantId);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional
    @Override
    public User create(User entity) {
        entity.setOrganizationId(TenantContext.getCurrentTenant());
        entity.setUserStatus(UserStatus.ACTIVE);

        var user = userRepository.save(entity);

        credentialsService.create(new Credentials(
                passwordEncoder.encode(entity.getPasswordNotEncoded()), user
        ));

        return user;
    }

    @Override
    public Page<User> findAll(Specification<User> spec, Pageable pageable) {
        Page<User> page = userRepository.findAll(spec, pageable);

        if (page.isEmpty()) return page;

        List<UUID> userIds = page.getContent().stream()
                .map(User::getId)
                .toList();

        List<User> userWithGroups = userIds.isEmpty()
                ? Collections.emptyList()
                : userRepository.findAllWithGroupsByIds(userIds);

        Map<UUID, User> usersEnrichedMap = userWithGroups.stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        List<User> orderedUsers = page.getContent().stream()
                .map(u -> usersEnrichedMap.getOrDefault(u.getId(), u))
                .toList();

        return new PageImpl<>(orderedUsers, pageable, page.getTotalElements());
    }

    @Transactional
    @Override
    public void inactivate(User entity) {
        entity.setUserStatus(UserStatus.DELETED);
        userRepository.save(entity);
    }

    @Transactional
    public User update(User entity) {
        return userRepository.save(entity);
    }
}
