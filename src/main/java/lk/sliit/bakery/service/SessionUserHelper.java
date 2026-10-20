package lk.sliit.bakery.service;

import jakarta.servlet.http.HttpSession;
import lk.sliit.bakery.model.User;
import org.springframework.stereotype.Component;

@Component
public class SessionUserHelper {
    private final UserService userService;

    public SessionUserHelper(UserService userService) {
        this.userService = userService;
    }

    public User currentUser(HttpSession session) {
        Object storedId = session.getAttribute("userId");
        if (!(storedId instanceof String userId)) return null;
        return userService.findById(userId).orElse(null);
    }
}
