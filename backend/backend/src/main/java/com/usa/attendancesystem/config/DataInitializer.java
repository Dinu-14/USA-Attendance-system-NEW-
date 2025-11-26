package com.usa.attendancesystem.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.usa.attendancesystem.model.Admin;
import com.usa.attendancesystem.model.ERole;
import com.usa.attendancesystem.model.Role;
import com.usa.attendancesystem.repository.AdminRepository;
import com.usa.attendancesystem.repository.RoleRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final AdminRepository adminRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(AdminRepository adminRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        initializeDefaultAdmin();
    }

    private void initializeDefaultAdmin() {
        // Check if admin already exists
        if (adminRepository.existsByUsername("admin")) {
            log.info("Default admin user already exists");
            return;
        }

        // Find the SUPER_ADMIN role
        Role superAdminRole = roleRepository.findByName(ERole.ROLE_SUPER_ADMIN)
                .orElseThrow(() -> new RuntimeException("ROLE_SUPER_ADMIN not found in database"));

        // Create default admin user
        Admin defaultAdmin = new Admin(
                "admin",
                passwordEncoder.encode("admin123"), // Default password
                superAdminRole
        );

        adminRepository.save(defaultAdmin);

        log.info("=".repeat(50));
        log.info("DEFAULT ADMIN USER CREATED SUCCESSFULLY!");
        log.info("Username: admin");
        log.info("Password: admin123");
        log.info("PLEASE CHANGE THE PASSWORD AFTER FIRST LOGIN!");
        log.info("=".repeat(50));
    }
}
