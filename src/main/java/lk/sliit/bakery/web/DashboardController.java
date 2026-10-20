package lk.sliit.bakery.web;

import jakarta.servlet.http.HttpSession;
import lk.sliit.bakery.model.User;
import lk.sliit.bakery.service.ProductService;
import lk.sliit.bakery.service.OrderService;
import lk.sliit.bakery.service.BookingService;
import lk.sliit.bakery.service.CustomCakeRequestService;
import lk.sliit.bakery.service.SessionUserHelper;
import lk.sliit.bakery.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {
    private final SessionUserHelper sessionUserHelper;
    private final UserService userService;
    private final ProductService productService;
    private final OrderService orderService;
    private final BookingService bookingService;
    private final CustomCakeRequestService customCakeRequestService;

    public DashboardController(SessionUserHelper sessionUserHelper, UserService userService,
                               ProductService productService, OrderService orderService,
                               BookingService bookingService, CustomCakeRequestService customCakeRequestService) {
        this.sessionUserHelper = sessionUserHelper;
        this.userService = userService;
        this.productService = productService;
        this.orderService = orderService;
        this.bookingService = bookingService;
        this.customCakeRequestService = customCakeRequestService;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = sessionUserHelper.currentUser(session);
        if (user == null) return "redirect:/login";

        model.addAttribute("user", user);
        model.addAttribute("dashboardTitle", user.getDashboardTitle());
        model.addAttribute("totalUsers", userService.countUsers());
        model.addAttribute("totalProducts", productService.countAvailable());
        model.addAttribute("totalCakes", productService.countCakes());
        model.addAttribute("totalBakeryItems", productService.countBakeryItems());
        model.addAttribute("totalOrders", user.isAdmin() ? orderService.countAll() : orderService.findForCustomer(user.getId()).size());
        model.addAttribute("totalBookings", user.isAdmin() ? bookingService.countAll() : bookingService.findForCustomer(user.getId()).size());
        model.addAttribute("totalCustomCakeRequests", customCakeRequestService.countAll());
        model.addAttribute("pendingOrders", orderService.countWithStatus("PENDING"));
        return "dashboard";
    }
}
