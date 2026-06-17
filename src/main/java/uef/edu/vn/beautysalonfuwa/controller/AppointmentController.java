package uef.edu.vn.beautysalonfuwa.controller;

import jakarta.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uef.edu.vn.beautysalonfuwa.model.Appointment;
import uef.edu.vn.beautysalonfuwa.service.AppointmentData;

@Controller
public class AppointmentController {

    @Autowired
    private AppointmentData appointmentData;

    @GetMapping("/booking")
    public String showBookingForm(Model model, HttpSession session) {
        Appointment appointment = new Appointment();
        appointment.setFullName((String) session.getAttribute("fullName"));
        appointment.setEmail((String) session.getAttribute("email"));
        model.addAttribute("appointment", appointment);
        addBookingOptions(model);
        return "customer/booking";
    }

    @PostMapping("/booking")
    public String submitBooking(
            @ModelAttribute Appointment appointment,
            @RequestParam(value = "services", required = false) List<String> selectedServices,
            Model model,
            HttpSession session) {

        model.addAttribute("appointment", appointment);

        if (selectedServices == null || selectedServices.isEmpty()) {
            model.addAttribute("errorMessage", "Vui lòng chọn ít nhất một dịch vụ.");
            addBookingOptions(model);
            return "customer/booking";
        }

        String validationMessage = appointmentData.validateBooking(appointment);
        if (validationMessage != null) {
            model.addAttribute("errorMessage", validationMessage);
            addBookingOptions(model);
            return "customer/booking";
        }

        Integer userId = (Integer) session.getAttribute("userId");
        String bookingCode = appointmentData.generateBookingCode();
        boolean saved = true;
        for (String serviceName : selectedServices) {
            appointment.setService(serviceName);
            if (!appointmentData.save(appointment, userId, bookingCode)) {
                saved = false;
                break;
            }
        }

        if (saved) {
            model.addAttribute("bookedServices", String.join(", ", selectedServices));
            model.addAttribute("successMessage", "Đặt lịch thành công! Salon sẽ liên hệ xác nhận với "
                    + appointment.getFullName() + ".");
        } else {
            model.addAttribute("errorMessage", "Không thể lưu lịch hẹn. Vui lòng kiểm tra thông tin và thử lại.");
        }

        addBookingOptions(model);
        return "customer/booking";
    }

    @GetMapping("/my-appointments")
    public String myAppointments(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        model.addAttribute("appointments", appointmentData.findByUserId(userId));
        return "customer/history";
    }

    private void addBookingOptions(Model model) {
        model.addAttribute("serviceOptions", appointmentData.findActiveServiceNames());
        model.addAttribute("employeeOptions", appointmentData.findActiveEmployeeNames());
        model.addAttribute("paymentOptions", Arrays.asList(
                "Thanh toán tại salon",
                "Chuyển khoản",
                "Ví điện tử"));
    }
}
