package com.moura.authorization.auth.repositories;

import com.moura.authorization.auth.entities.Credentials;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface CredentialsRepository extends JpaRepository<Credentials, UUID> {
    Set<Credentials> findByUserId(UUID userId);
}
