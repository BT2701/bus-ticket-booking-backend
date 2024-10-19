package com.example.demo.configurations;

import com.example.demo.Model.Role;
import com.example.demo.Repository.RoleRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration
public class RoleInitializer {

    @Bean
    CommandLineRunner initRoles(RoleRepo roleRepo) {
        return args -> {
            Optional<Role> adminRole = Optional.ofNullable(roleRepo.findByName(Role.ROLE_ADMIN));
            if (adminRole.isEmpty()) {
                Role role = new Role();
                role.setName(Role.ROLE_ADMIN);
                roleRepo.save(role);
            }

            Optional<Role> staffRole = Optional.ofNullable(roleRepo.findByName(Role.ROLE_STAFF));
            if (staffRole.isEmpty()) {
                Role role = new Role();
                role.setName(Role.ROLE_STAFF);
                roleRepo.save(role);
            }

            Optional<Role> customerRole = Optional.ofNullable(roleRepo.findByName(Role.ROLE_CUSTOMER));
            if (customerRole.isEmpty()) {
                Role role = new Role();
                role.setName(Role.ROLE_CUSTOMER);
                roleRepo.save(role);
            }
        };
    }
}
