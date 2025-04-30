package com.moura.authorization.users.services.impl;

import com.moura.authorization.auth.repositories.CredentialsRepository;
import com.moura.authorization.context.TenantContext;
import com.moura.authorization.enums.UserStatus;
import com.moura.authorization.users.entities.Credentials;
import com.moura.authorization.users.entities.User;
import com.moura.authorization.users.repositories.UserRepository;
import com.moura.authorization.users.services.CredentialsService;
import com.moura.authorization.users.services.UserService;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

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
        return Optional.empty();
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
}
