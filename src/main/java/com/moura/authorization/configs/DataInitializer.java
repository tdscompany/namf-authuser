package com.moura.authorization.configs;

import com.moura.authorization.users.entities.Credentials;
import com.moura.authorization.users.entities.User;
import com.moura.authorization.users.enums.UserStatus;
import com.moura.authorization.users.repositories.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataInitializer {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    @Transactional
    public void init() {
        if (userRepository.count() == 0) {
            addAdminUser();
        }
    }

    private void addAdminUser() {
        User user = new User();
        user.setName("admin");
        user.setEmail("admin@tds.company");
        user.setUserStatus(UserStatus.ACTIVE);
        Credentials credential = new Credentials(passwordEncoder.encode("password"), user);
        user.setCredentials(Set.of(credential));
        userRepository.save(user);
    }
}
