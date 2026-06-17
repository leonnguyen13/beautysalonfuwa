package uef.edu.vn.beautysalonfuwa.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uef.edu.vn.beautysalonfuwa.model.Customer;
import uef.edu.vn.beautysalonfuwa.model.Employee;
import uef.edu.vn.beautysalonfuwa.model.Invoice;
import uef.edu.vn.beautysalonfuwa.model.ReportSummary;
import uef.edu.vn.beautysalonfuwa.model.SalonService;
import uef.edu.vn.beautysalonfuwa.service.AppointmentData;
import uef.edu.vn.beautysalonfuwa.service.CustomerData;
import uef.edu.vn.beautysalonfuwa.service.DashboardData;
import uef.edu.vn.beautysalonfuwa.service.EmployeeData;
import uef.edu.vn.beautysalonfuwa.service.InvoiceData;
import uef.edu.vn.beautysalonfuwa.service.ReportData;
import uef.edu.vn.beautysalonfuwa.service.ServiceData;

@Controller
public class AdminController {
    @Autowired
    private ServiceData serviceData;

    @Autowired
    private CustomerData customerData;

    @Autowired
    private AppointmentData appointmentData;

    @Autowired
    private EmployeeData employeeData;

    @Autowired
    private InvoiceData invoiceData;

    @Autowired
    private ReportData reportData;

    @Autowired
    private DashboardData dashboardData;

    @GetMapping("/admin/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("todayAppointmentCount", dashboardData.countTodayAppointments());
        model.addAttribute("monthlyRevenue", dashboardData.getPaidRevenueThisMonth());
        model.addAttribute("newCustomerCount", dashboardData.countNewCustomersThisMonth());
        model.addAttribute("recentAppointments", dashboardData.findRecentAppointments(5));
        return "admin/dashboard";
    }

    @GetMapping("/admin/services")
    public String services(Model model) {
        model.addAttribute("services", serviceData.findAll());
        if (!model.containsAttribute("serviceForm")) {
            model.addAttribute("serviceForm", new SalonService());
        }
        return "admin/services";
    }

    @PostMapping("/admin/services/save")
    public String saveService(
            @RequestParam(defaultValue = "0") int id,
            @RequestParam String name,
            @RequestParam String category,
            @RequestParam String priceText,
            @RequestParam String description,
            @RequestParam String imageUrl) {

        SalonService service = new SalonService(id, name, description, priceText, imageUrl, category);
        serviceData.save(service);
        return "redirect:/admin/services";
    }

    @GetMapping("/admin/services/edit")
    public String editService(@RequestParam int id, Model model) {
        SalonService service = serviceData.findById(id);
        model.addAttribute("serviceForm", service == null ? new SalonService() : service);
        model.addAttribute("services", serviceData.findAll());
        return "admin/services";
    }

    @GetMapping("/admin/services/delete")
    public String deleteService(@RequestParam int id) {
        serviceData.delete(id);
        return "redirect:/admin/services";
    }

    @GetMapping("/admin/customers")
    public String customers(Model model) {
        model.addAttribute("customers", customerData.findAll());
        if (!model.containsAttribute("customerForm")) {
            model.addAttribute("customerForm", new Customer());
        }
        return "admin/customers";
    }

    @PostMapping("/admin/customers/save")
    public String saveCustomer(
            @RequestParam(defaultValue = "0") int id,
            @RequestParam String fullName,
            @RequestParam String phone,
            @RequestParam String email,
            @RequestParam String gender,
            @RequestParam String address,
            RedirectAttributes redirectAttributes) {

        Customer customer = new Customer(id, fullName, phone, email, gender, address);
        String validationMessage = customerData.validate(customer);
        if (validationMessage != null) {
            redirectAttributes.addFlashAttribute("customerForm", customer);
            redirectAttributes.addFlashAttribute("errorMessage", validationMessage);
            return "redirect:/admin/customers";
        }

        if (customerData.save(customer)) {
            redirectAttributes.addFlashAttribute("successMessage",
                    id > 0 ? "Cập nhật khách hàng thành công." : "Thêm khách hàng thành công.");
        } else {
            redirectAttributes.addFlashAttribute("customerForm", customer);
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Không thể lưu khách hàng. Vui lòng kiểm tra dữ liệu và thử lại.");
        }

        return "redirect:/admin/customers";
    }

    @GetMapping("/admin/customers/edit")
    public String editCustomer(@RequestParam int id, Model model) {
        Customer customer = customerData.findById(id);
        model.addAttribute("customerForm", customer == null ? new Customer() : customer);
        model.addAttribute("customers", customerData.findAll());
        return "admin/customers";
    }

    @GetMapping("/admin/customers/delete")
    public String deleteCustomer(@RequestParam int id) {
        customerData.delete(id);
        return "redirect:/admin/customers";
    }

    @GetMapping("/admin/appointments")
    public String appointments(Model model) {
        model.addAttribute("appointments", appointmentData.findAll());
        return "admin/appointments";
    }

    @PostMapping("/admin/appointments/status")
    public String updateAppointmentStatus(@RequestParam int id, @RequestParam String status) {
        boolean updated = appointmentData.updateStatus(id, status);
        if (updated && "COMPLETED".equals(status)) {
            invoiceData.createFromAppointment(id);
        }
        return "redirect:/admin/appointments";
    }

    @GetMapping("/admin/employees")
    public String employees(Model model) {
        model.addAttribute("employees", employeeData.findAll());
        if (!model.containsAttribute("employeeForm")) {
            model.addAttribute("employeeForm", new Employee());
        }
        return "admin/employees";
    }

    @PostMapping("/admin/employees/save")
    public String saveEmployee(
            @RequestParam(defaultValue = "0") int id,
            @RequestParam String fullName,
            @RequestParam String phone,
            @RequestParam String email,
            @RequestParam String position,
            @RequestParam String specialty,
            @RequestParam String status,
            @RequestParam(required = false) String password,
            @RequestParam(defaultValue = "STAFF") String role,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        String currentRole = (String) session.getAttribute("role");
        if (!"ADMIN".equals(currentRole) && !"STAFF".equals(role)) {
            role = "STAFF";
        }

        Employee employee = new Employee(id, fullName, phone, email, position, specialty, status);
        employee.setPassword(password);
        employee.setRole(role);

        String validationMessage = employeeData.validate(employee);
        if (validationMessage != null) {
            redirectAttributes.addFlashAttribute("employeeForm", employee);
            redirectAttributes.addFlashAttribute("errorMessage", validationMessage);
            return "redirect:/admin/employees";
        }

        if (employeeData.save(employee)) {
            redirectAttributes.addFlashAttribute("successMessage",
                    id > 0 ? "Cập nhật nhân viên thành công." : "Thêm nhân viên thành công.");
        } else {
            redirectAttributes.addFlashAttribute("employeeForm", employee);
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Không thể lưu nhân viên. Vui lòng kiểm tra dữ liệu và thử lại.");
        }

        return "redirect:/admin/employees";
    }

    @GetMapping("/admin/employees/edit")
    public String editEmployee(@RequestParam int id, Model model) {
        Employee employee = employeeData.findById(id);
        model.addAttribute("employeeForm", employee == null ? new Employee() : employee);
        model.addAttribute("employees", employeeData.findAll());
        return "admin/employees";
    }

    @GetMapping("/admin/employees/delete")
    public String deleteEmployee(@RequestParam int id) {
        employeeData.delete(id);
        return "redirect:/admin/employees";
    }

    @GetMapping("/admin/invoices")
    public String invoices(Model model) {
        model.addAttribute("invoices", invoiceData.findAll());
        model.addAttribute("customers", customerData.findAll());
        model.addAttribute("employees", employeeData.findAll());
        model.addAttribute("services", serviceData.findAll());
        return "admin/invoices";
    }

    @PostMapping("/admin/invoices/save")
    public String saveInvoice(
            @RequestParam String customerName,
            @RequestParam String serviceName,
            @RequestParam String employeeName,
            @RequestParam String totalAmount,
            @RequestParam String paymentMethod,
            @RequestParam String createdDate) {

        invoiceData.create(customerName, serviceName, employeeName, totalAmount, paymentMethod, createdDate);
        return "redirect:/admin/invoices";
    }

    @PostMapping("/admin/invoices/pay")
    public String payInvoice(@RequestParam int id) {
        invoiceData.markPaid(id);
        return "redirect:/admin/invoices/print?id=" + id;
    }

    @GetMapping("/admin/invoices/print")
    public String printInvoice(@RequestParam int id, Model model) {
        Invoice invoice = invoiceData.findById(id);
        if (invoice == null) {
            return "redirect:/admin/invoices";
        }

        model.addAttribute("invoice", invoice);
        return "admin/invoice-print";
    }

    @GetMapping("/admin/reports")
    public String reports(Model model) {
        model.addAttribute("summaries", reportData.getSummaries());
        model.addAttribute("popularServices", reportData.getPopularServices());
        return "admin/reports";
    }

    @GetMapping("/admin/reports/export")
    public void exportReports(HttpServletResponse response) throws IOException {
        List<ReportSummary> summaries = reportData.getSummaries();
        List<SalonService> popularServices = reportData.getPopularServices();

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=fuwa-report.xlsx");

        try (Workbook workbook = new XSSFWorkbook()) {
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            Sheet summarySheet = workbook.createSheet("Tong quan");
            Row titleRow = summarySheet.createRow(0);
            titleRow.createCell(0).setCellValue("Báo cáo FUWA Salon");
            titleRow.createCell(1).setCellValue("Ngày xuất: " + LocalDate.now());

            Row summaryHeader = summarySheet.createRow(2);
            summaryHeader.createCell(0).setCellValue("Nội dung");
            summaryHeader.createCell(1).setCellValue("Giá trị");
            summaryHeader.createCell(2).setCellValue("Ghi chú");
            summaryHeader.getCell(0).setCellStyle(headerStyle);
            summaryHeader.getCell(1).setCellStyle(headerStyle);
            summaryHeader.getCell(2).setCellStyle(headerStyle);

            int summaryRowIndex = 3;
            for (ReportSummary summary : summaries) {
                Row row = summarySheet.createRow(summaryRowIndex++);
                row.createCell(0).setCellValue(summary.getTitle());
                row.createCell(1).setCellValue(summary.getValue());
                row.createCell(2).setCellValue(summary.getNote());
            }

            Sheet serviceSheet = workbook.createSheet("Dich vu pho bien");
            Row serviceHeader = serviceSheet.createRow(0);
            serviceHeader.createCell(0).setCellValue("ID");
            serviceHeader.createCell(1).setCellValue("Dịch vụ");
            serviceHeader.createCell(2).setCellValue("Danh mục");
            serviceHeader.createCell(3).setCellValue("Lượt đặt");
            serviceHeader.createCell(4).setCellValue("Doanh thu");
            for (int i = 0; i < 5; i++) {
                serviceHeader.getCell(i).setCellStyle(headerStyle);
            }

            int serviceRowIndex = 1;
            for (SalonService service : popularServices) {
                Row row = serviceSheet.createRow(serviceRowIndex++);
                row.createCell(0).setCellValue(service.getId());
                row.createCell(1).setCellValue(service.getName());
                row.createCell(2).setCellValue(service.getCategory());
                row.createCell(3).setCellValue(service.getDescription());
                row.createCell(4).setCellValue(service.getPriceText());
            }

            for (int i = 0; i < 3; i++) {
                summarySheet.autoSizeColumn(i);
            }
            for (int i = 0; i < 5; i++) {
                serviceSheet.autoSizeColumn(i);
            }

            workbook.write(response.getOutputStream());
        }
    }
}
