package lk.sliit.bakery.web;

import jakarta.servlet.http.HttpSession;
import lk.sliit.bakery.model.User;
import lk.sliit.bakery.service.SessionUserHelper;
import lk.sliit.bakery.service.AdminService;
import lk.sliit.bakery.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminUserController {
    private final SessionUserHelper sessionUserHelper;
    private final AdminService adminService;
    private final UserService userService;

    public AdminUserController(SessionUserHelper sessionUserHelper, UserService userService, AdminService adminService) {
        this.sessionUserHelper = sessionUserHelper;
        this.userService = userService;
        this.adminService = adminService;
    }

    @GetMapping("/admin/users")
    public String listUsers(@RequestParam(name = "q", defaultValue = "") String query,
                            HttpSession session, Model model) {
        User currentUser = sessionUserHelper.currentUser(session);
        if (currentUser == null) return "redirect:/login";
        if (!adminService.hasAdminAccess(currentUser)) return "redirect:/dashboard?forbidden";

        model.addAttribute("user", currentUser);
        model.addAttribute("users", userService.searchUsers(query));
        model.addAttribute("query", query);
        return "admin-users";
    }

    @PostMapping("/admin/users/{id}/delete")
    public String deleteUser(@PathVariable String id, HttpSession session,
                             RedirectAttributes redirectAttributes) {
        User currentUser = sessionUserHelper.currentUser(session);
        if (currentUser == null) return "redirect:/login";
        if (!adminService.hasAdminAccess(currentUser)) return "redirect:/dashboard?forbidden";
        if (currentUser.getId().equals(id)) {
            redirectAttributes.addFlashAttribute("message", "You cannot delete the account you are currently using.");
            return "redirect:/admin/users";
        }
        userService.deleteUser(id);
        redirectAttributes.addFlashAttribute("message", "User deleted, if that account existed.");
        return "redirect:/admin/users";
    }
}
