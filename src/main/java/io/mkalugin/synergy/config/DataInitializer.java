package io.mkalugin.synergy.config;

import io.mkalugin.synergy.model.Role;
import io.mkalugin.synergy.model.User;
import io.mkalugin.synergy.repository.RoleRepository;
import io.mkalugin.synergy.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    @Transactional
    public void init() {
        log.info("Starting data initialization users and roles.");
        createDefaultRoles();
        createDefaultUsers();
        log.info("Data initialization completed");
    }

    private void createDefaultRoles() {
        if (roleRepository.findByName("ROLE_USER").isEmpty()) {
            roleRepository.save(new Role(null, "ROLE_USER"));
            log.info("Created ROLE_USER");
        }
        if (roleRepository.findByName("ROLE_ADMIN").isEmpty()) {
            roleRepository.save(new Role(null, "ROLE_ADMIN"));
            log.info("Created ROLE_ADMIN");
        }
    }

    private void createDefaultUsers() {
        if (userRepository.findByUsername("user").isEmpty()) {
            Role userRole = roleRepository.findByName("ROLE_USER")
                    .orElseThrow(() -> new RuntimeException("ROLE_USER not found"));

            User user = new User();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("password"));
            user.setEnabled(true);
            user.setRoles(Set.of(userRole));
            userRepository.save(user);
            log.info("User 'user' created with role ROLE_USER");
        }

        if (userRepository.findByUsername("admin").isEmpty()) {
            Role userRole = roleRepository.findByName("ROLE_USER")
                    .orElseThrow(() -> new RuntimeException("ROLE_USER not found"));
            Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                    .orElseThrow(() -> new RuntimeException("ROLE_ADMIN not found"));

            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("password"));
            admin.setEnabled(true);
            admin.setRoles(Set.of(userRole, adminRole));
            userRepository.save(admin);
            log.info("User 'admin' created with roles ROLE_USER and ROLE_ADMIN");
        }
    }
}
