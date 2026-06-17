package uef.edu.vn.beautysalonfuwa.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uef.edu.vn.beautysalonfuwa.model.AuthUser;
import uef.edu.vn.beautysalonfuwa.service.AccountService;
import uef.edu.vn.beautysalonfuwa.service.AppointmentData;
import uef.edu.vn.beautysalonfuwa.service.EmployeeData;
import uef.edu.vn.beautysalonfuwa.service.ServiceData;

@Controller
public class HomeController {
    @Autowired
    private ServiceData serviceData;

    @Autowired
    private AccountService accountService;

    @Autowired
    private EmployeeData employeeData;

    @Autowired
    private AppointmentData appointmentData;

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        model.addAttribute("featuredServices", serviceData.findFeatured());
        model.addAttribute("featuredEmployees", employeeData.findFeatured());
        model.addAttribute("activeServiceNames", appointmentData.findActiveServiceNames());
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String loginSubmit(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            Model model) {

        AuthUser authUser = accountService.login(email, password);
        if (authUser == null) {
            model.addAttribute("errorMessage", accountService.getLoginErrorMessage(email, password));
            model.addAttribute("email", email);
            return "login";
        }

        session.setAttribute("authUser", authUser);
        session.setAttribute("userId", authUser.getId());
        session.setAttribute("fullName", authUser.getFullName());
        session.setAttribute("email", authUser.getEmail());
        session.setAttribute("role", authUser.getRole());

        if ("ADMIN".equals(authUser.getRole())) {
            return "redirect:/admin/dashboard";
        }
        if ("MANAGER".equals(authUser.getRole())) {
            return "redirect:/admin/services";
        }
        if ("STAFF".equals(authUser.getRole())) {
            return "redirect:/admin/appointments";
        }

        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    public String registerSubmit(
            @RequestParam String fullName,
            @RequestParam String email,
            @RequestParam String phone,
            @RequestParam String password,
            Model model) {

        model.addAttribute("fullName", fullName);
        model.addAttribute("email", email);
        model.addAttribute("phone", phone);

        String validationMessage = accountService.validateRegistration(fullName, email, phone, password);
        if (validationMessage != null) {
            model.addAttribute("errorMessage", validationMessage);
            return "register";
        }

        if (accountService.emailExists(email)) {
            model.addAttribute("errorMessage", "Email đã được sử dụng.");
            return "register";
        }

        boolean registered = accountService.registerCustomer(fullName, email, phone, password);
        if (!registered) {
            model.addAttribute("errorMessage", "Không thể tạo tài khoản. Vui lòng thử lại.");
            return "register";
        }

        model.addAttribute("successMessage", "Đăng ký thành công. Vui lòng đăng nhập.");
        return "login";
    }

}
