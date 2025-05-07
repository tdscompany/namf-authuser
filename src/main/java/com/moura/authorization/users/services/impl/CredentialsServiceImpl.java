package com.moura.authorization.users.services.impl;

import com.moura.authorization.auth.repositories.CredentialsRepository;
import com.moura.authorization.users.entities.Credentials;
import com.moura.authorization.users.services.CredentialsService;
import org.springframework.stereotype.Service;

@Service
public class CredentialsServiceImpl implements CredentialsService {

    private final CredentialsRepository credentialsRepository;

    public CredentialsServiceImpl(CredentialsRepository credentialsRepository) {
        this.credentialsRepository = credentialsRepository;
    }

    @Override
    public Credentials create(Credentials credentials) {

        if (credentials == null) {
            throw new IllegalArgumentException("Credentials cannot be null");
        }

        return credentialsRepository.save(credentials);
    }
}
