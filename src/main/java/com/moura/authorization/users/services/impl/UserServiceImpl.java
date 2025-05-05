package com.moura.authorization.users.services.impl;

import com.moura.authorization.context.TenantContext;
import com.moura.authorization.exceptions.AlreadyExistsException;
import com.moura.authorization.exceptions.NotFoundException;
import com.moura.authorization.groups.services.GroupService;
import com.moura.authorization.users.enums.UserStatus;
import com.moura.authorization.users.entities.Credentials;
import com.moura.authorization.users.entities.User;
import com.moura.authorization.users.repositories.UserRepository;
import com.moura.authorization.users.services.CredentialsService;
import com.moura.authorization.users.services.UserService;
import com.moura.authorization.utils.MessageUtils;
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

    private final GroupService groupService;

    private final PasswordEncoder passwordEncoder;


    public UserServiceImpl(UserRepository userRepository, CredentialsService credentialsService, GroupService groupService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.credentialsService = credentialsService;
        this.groupService = groupService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User findById(UUID userId) {
        return userRepository.findByIdAndTenant(userId, TenantContext.getCurrentTenant())
                .orElseThrow(() -> new NotFoundException(MessageUtils.get("error.user_not_found")));
    }

    @Transactional
    @Override
    public User create(User entity) {
        validateEntity(entity);

        entity.setOrganizationId(TenantContext.getCurrentTenant());
        entity.setUserStatus(UserStatus.ACTIVE);

        var user = userRepository.save(entity);

        credentialsService.create(new Credentials(
                passwordEncoder.encode(entity.getPasswordNotEncoded()), user)
        );

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
        if (entity.getUserStatus() == UserStatus.DELETED) return;

        entity.setUserStatus(UserStatus.DELETED);
        userRepository.save(entity);
    }

    @Transactional
    public User update(User entity) {
        validateEntity(entity);
        return userRepository.save(entity);
    }

    private void validateEntity(User entity) {
        validateEmail(entity.getEmail(), entity.getId());
        groupService.validateGroupIds(entity.getGroupIds());
    }

    private void validateEmail(String email, UUID currentUserId) {
        boolean exists = (currentUserId == null)
                ? userRepository.existsByEmail(email)
                : userRepository.existsByEmailAndIdNot(email, currentUserId);

        if (exists) throw new AlreadyExistsException(MessageUtils.get("conflict.email_already_exists"));
    }
}
