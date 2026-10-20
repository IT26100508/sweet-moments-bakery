package lk.sliit.bakery.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lk.sliit.bakery.model.User;
import lk.sliit.bakery.service.UserService;
import lk.sliit.bakery.web.forms.LoginForm;
import lk.sliit.bakery.web.forms.RegisterForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/shop";
    }

    @GetMapping("/login")
    public String showLogin(@RequestParam(name = "next", required = false) String next, Model model) {
        model.addAttribute("form", new LoginForm());
        model.addAttribute("next", safeNext(next));
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute("form") LoginForm form,
                        @RequestParam(name = "next", required = false) String next,
                        HttpSession session, Model model) {
        User user = userService.authenticate(form.getEmail(), form.getPassword()).orElse(null);
        if (user == null) {
            model.addAttribute("error", "Email or password is incorrect.");
            model.addAttribute("next", safeNext(next));
            return "login";
        }
        session.setAttribute("userId", user.getId());
        return redirectAfterAuthentication(next);
    }

    @GetMapping("/register")
    public String showRegister(@RequestParam(name = "next", required = false) String next, Model model) {
        model.addAttribute("form", new RegisterForm());
        model.addAttribute("next", safeNext(next));
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("form") RegisterForm form,
                           @RequestParam(name = "next", required = false) String next,
                           HttpServletRequest request, Model model) {
        if (form.getPassword() == null || !form.getPassword().equals(form.getConfirmPassword())) {
            model.addAttribute("error", "The passwords do not match.");
            model.addAttribute("next", safeNext(next));
            return "register";
        }
        try {
            User user = userService.register(form.getName(), form.getEmail(), form.getPassword());
            // Create a new session after successful registration/login.
            HttpSession session = request.getSession(true);
            session.setAttribute("userId", user.getId());
            return redirectAfterAuthentication(next);
        } catch (IllegalArgumentException exception) {
            model.addAttribute("error", exception.getMessage());
            model.addAttribute("next", safeNext(next));
            return "register";
        }
    }

    /** Only redirect to known local paths; never trust arbitrary external redirect URLs. */
    private String safeNext(String next) {
        return "/bookings/new".equals(next) ? next : "";
    }

    private String redirectAfterAuthentication(String next) {
        return "/bookings/new".equals(safeNext(next)) ? "redirect:/bookings/new" : "redirect:/dashboard";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?loggedOut";
    }
}
