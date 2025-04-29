package com.moura.authorization.auth.services.impl;

import com.moura.authorization.auth.repositories.CredentialsRepository;
import com.moura.authorization.users.repositories.UserRepository;
import com.moura.authorization.users.entities.User;
import com.moura.authorization.utils.MessageUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserDetailsImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final CredentialsRepository credentialsRepository;

    public UserDetailsImpl(UserRepository userRepository, CredentialsRepository credentialsRepository) {
        this.userRepository = userRepository;
        this.credentialsRepository = credentialsRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(MessageUtils.get("error.user_not_found") +  " " +username));

        var credentials = credentialsRepository.findByUserId(user.getId());
        user.setCredentials(credentials);

        return user;
    }

    public UserDetails loadUserByUserId(UUID uuid) {

        User user = userRepository.findById(uuid)
                .orElseThrow(() -> new UsernameNotFoundException(MessageUtils.get("error.user_not_found") +  " "  + uuid));

        var credentials = credentialsRepository.findByUserId(user.getId());
        user.setCredentials(credentials);

        return user;
    }
}
