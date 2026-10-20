package lk.sliit.bakery.web;

import jakarta.servlet.http.HttpSession;
import lk.sliit.bakery.model.User;
import lk.sliit.bakery.service.SessionUserHelper;
import lk.sliit.bakery.service.UserService;
import lk.sliit.bakery.web.forms.ProfileForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ProfileController {
    private final SessionUserHelper sessionUserHelper;
    private final UserService userService;

    public ProfileController(SessionUserHelper sessionUserHelper, UserService userService) {
        this.sessionUserHelper = sessionUserHelper;
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String showProfile(HttpSession session, Model model) {
        User user = sessionUserHelper.currentUser(session);
        if (user == null) return "redirect:/login";
        ProfileForm form = new ProfileForm();
        form.setName(user.getName());
        form.setPhone(user.getPhone());
        form.setAddress(user.getAddress());
        model.addAttribute("user", user);
        model.addAttribute("form", form);
        return "profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@ModelAttribute("form") ProfileForm form,
                                HttpSession session, Model model) {
        User user = sessionUserHelper.currentUser(session);
        if (user == null) return "redirect:/login";
        try {
            userService.updateProfile(user.getId(), form.getName(), form.getPhone(), form.getAddress());
            return "redirect:/profile?saved";
        } catch (IllegalArgumentException exception) {
            model.addAttribute("user", user);
            model.addAttribute("error", exception.getMessage());
            return "profile";
        }
    }
}
