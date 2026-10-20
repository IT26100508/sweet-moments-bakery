package lk.sliit.bakery.service;

import lk.sliit.bakery.model.Customer;
import lk.sliit.bakery.model.User;
import lk.sliit.bakery.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Customer register(String name, String email, String password) {
        String cleanedName = requireText(name, "Name");
        String cleanedEmail = requireText(email, "Email").toLowerCase();
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password is required.");
        }
        // Do not trim a password: leading/trailing spaces are part of what the user typed.
        String rawPassword = password;
        if (!cleanedEmail.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new IllegalArgumentException("Enter a valid email address.");
        }
        if (rawPassword.length() < 8) {
            throw new IllegalArgumentException("Password must contain at least 8 characters.");
        }
        if (userRepository.findByEmail(cleanedEmail).isPresent()) {
            throw new IllegalArgumentException("An account with this email already exists.");
        }

        Customer customer = new Customer(
                UUID.randomUUID().toString(), cleanedName, cleanedEmail,
                passwordEncoder.encode(rawPassword), "", "");
        userRepository.save(customer);
        return customer;
    }

    public Optional<User> authenticate(String email, String password) {
        if (email == null || password == null) return Optional.empty();
        return userRepository.findByEmail(email.trim()).filter(
                user -> passwordEncoder.matches(password, user.getPasswordHash()));
    }

    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }

    public List<User> searchUsers(String query) {
        String search = query == null ? "" : query.trim().toLowerCase();
        return userRepository.findAll().stream()
                .filter(user -> search.isEmpty()
                        || user.getName().toLowerCase().contains(search)
                        || user.getEmail().toLowerCase().contains(search))
                .sorted(Comparator.comparing(User::getName, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    public int countUsers() {
        return userRepository.findAll().size();
    }

    public void updateProfile(String userId, String name, String phone, String address) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User account was not found."));
        user.setName(requireText(name, "Name"));
        user.setPhone(phone == null ? "" : phone.trim());
        user.setAddress(address == null ? "" : address.trim());
        userRepository.save(user);
    }

    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }

    private String requireText(String value, String label) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(label + " is required.");
        }
        return value.trim();
    }
}
