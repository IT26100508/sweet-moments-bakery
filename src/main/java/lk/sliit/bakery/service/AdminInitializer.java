package lk.sliit.bakery.service;

import lk.sliit.bakery.model.Admin;
import lk.sliit.bakery.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AdminInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        String email = "admin@bakery.com";
        if (userRepository.findByEmail(email).isEmpty()) {
            // IF wanted you can change the admin account in here
            Admin admin = new Admin(UUID.randomUUID().toString(), "Bakery Admin", email,
                    passwordEncoder.encode("Admin123!"), "", "");
            userRepository.save(admin);
        }
    }
}
