package com.moura.authorization.users.services;

import com.moura.authorization.users.entities.Credentials;
import com.moura.authorization.users.entities.User;

import java.util.Optional;
import java.util.UUID;

public interface UserService {
    Optional<User> findById(UUID userId);
    boolean existsByEmail(String email);
    User createUser(User entity);
}
