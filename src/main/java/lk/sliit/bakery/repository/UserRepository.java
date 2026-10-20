package lk.sliit.bakery.repository;

import lk.sliit.bakery.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<User> findAll();
    Optional<User> findById(String id);
    Optional<User> findByEmail(String email);
    void save(User user);
    void deleteById(String id);
}
