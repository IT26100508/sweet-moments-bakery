package lk.sliit.bakery.repository;

import jakarta.annotation.PostConstruct;
import lk.sliit.bakery.model.Admin;
import lk.sliit.bakery.model.Customer;
import lk.sliit.bakery.model.Role;
import lk.sliit.bakery.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class UserFileRepository implements UserRepository {
    private final Path dataFile;

    public UserFileRepository(@Value("${bakery.data-file:data/users.txt}") String fileName) {
        this.dataFile = Path.of(fileName);
    }

    @PostConstruct
    public void prepareFile() {
        try {
            Path parent = dataFile.toAbsolutePath().getParent();
            if (parent != null) Files.createDirectories(parent);
            if (Files.notExists(dataFile)) Files.createFile(dataFile);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not prepare the user data file: " + dataFile, exception);
        }
    }

    @Override
    public synchronized List<User> findAll() {
        List<User> users = new ArrayList<>();
        try {
            for (String line : Files.readAllLines(dataFile, StandardCharsets.UTF_8)) {
                if (line.isBlank() || line.startsWith("#")) continue;
                User user = parseLine(line);
                if (user != null) users.add(user);
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Could not read user data from " + dataFile, exception);
        }
        return users;
    }

    @Override
    public synchronized Optional<User> findById(String id) {
        return findAll().stream().filter(user -> user.getId().equals(id)).findFirst();
    }

    @Override
    public synchronized Optional<User> findByEmail(String email) {
        if (email == null) return Optional.empty();
        String wanted = email.trim();
        return findAll().stream()
                .filter(user -> user.getEmail().equalsIgnoreCase(wanted))
                .findFirst();
    }

    @Override
    public synchronized void save(User user) {
        List<User> users = findAll();
        boolean replaced = false;
        for (int index = 0; index < users.size(); index++) {
            if (users.get(index).getId().equals(user.getId())) {
                users.set(index, user);
                replaced = true;
                break;
            }
        }
        if (!replaced) users.add(user);
        writeAll(users);
    }

    @Override
    public synchronized void deleteById(String id) {
        List<User> users = findAll();
        users.removeIf(user -> user.getId().equals(id));
        writeAll(users);
    }

    private User parseLine(String line) {
        String[] fields = line.split("\\|", -1);
        if (fields.length != 7) {
            System.err.println("Skipping malformed row in " + dataFile + ": expected 7 fields.");
            return null;
        }
        try {
            String id = fields[0];
            Role role = Role.valueOf(fields[1]);
            String name = fields[2];
            String email = fields[3];
            String passwordHash = fields[4];
            String phone = fields[5];
            String address = fields[6];
            if (role == Role.ADMIN) {
                return new Admin(id, name, email, passwordHash, phone, address);
            }
            return new Customer(id, name, email, passwordHash, phone, address);
        } catch (IllegalArgumentException exception) {
            System.err.println("Skipping invalid user row in " + dataFile + ".");
            return null;
        }
    }

    private void writeAll(List<User> users) {
        List<String> lines = users.stream().map(this::toLine).toList();
        try {
            Files.write(dataFile, lines, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not save user data to " + dataFile, exception);
        }
    }

    private String toLine(User user) {
        // Keep this simple text-file format one record per line. Replace delimiters/newlines in user input.
        return String.join("|",
                clean(user.getId()),
                user.getRole().name(),
                clean(user.getName()),
                clean(user.getEmail()),
                clean(user.getPasswordHash()),
                clean(user.getPhone()),
                clean(user.getAddress()));
    }

    private String clean(String value) {
        if (value == null) return "";
        return value.replace('|', ' ').replace('\n', ' ').replace('\r', ' ').trim();
    }
}
