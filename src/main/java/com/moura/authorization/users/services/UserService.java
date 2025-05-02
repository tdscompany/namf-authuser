package com.moura.authorization.users.services;

import com.moura.authorization.users.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;
import java.util.UUID;

public interface UserService {
    Optional<User> findById(UUID userId);
    boolean existsByEmail(String email);
    User create(User entity);
    Page<User> findAll(Specification<User> spec, Pageable pageable);
    void inactivate(User entity);
    User update(User entity);
}
