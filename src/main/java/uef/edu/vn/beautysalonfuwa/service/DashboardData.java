package uef.edu.vn.beautysalonfuwa.service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;
import uef.edu.vn.beautysalonfuwa.config.DatabaseConnection;
import uef.edu.vn.beautysalonfuwa.model.Appointment;

@Service
public class DashboardData {

    public int countTodayAppointments() {
        String sql = "SELECT COUNT(*) FROM ("
                + "SELECT 1 FROM appointments "
                + "GROUP BY COALESCE(booking_code, CONCAT('ID-', id)), full_name, phone, employee_id, "
                + "appointment_date, appointment_time, payment_method, status"
                + ") appointment_groups";
        return queryCount(sql);
    }

    public int countNewCustomersThisMonth() {
        return queryCount("SELECT COUNT(*) FROM customers");
    }

    public String getPaidRevenueThisMonth() {
        String sql = "SELECT COALESCE(SUM(total_amount), 0) FROM invoices "
                + "WHERE payment_status = 'PAID'";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                return formatPrice(resultSet.getBigDecimal(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "0đ";
    }

    public List<Appointment> findRecentAppointments(int limit) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT MIN(a.id) AS id, a.full_name, a.phone, "
                + "GROUP_CONCAT(DISTINCT s.name ORDER BY a.id SEPARATOR ', ') AS service_name, "
                + "COALESCE(e.full_name, 'Chọn tự động') AS employee_name, "
                + "a.appointment_date, a.appointment_time, a.payment_method, a.status "
                + "FROM appointments a "
                + "JOIN services s ON a.service_id = s.id "
                + "LEFT JOIN employees e ON a.employee_id = e.id "
                + "GROUP BY a.booking_code, a.full_name, a.phone, e.full_name, "
                + "a.appointment_date, a.appointment_time, a.payment_method, a.status "
                + "ORDER BY MAX(a.created_at) DESC, MIN(a.id) DESC "
                + "LIMIT ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, limit);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    appointments.add(new Appointment(
                            resultSet.getInt("id"),
                            resultSet.getString("full_name"),
                            resultSet.getString("phone"),
                            resultSet.getString("service_name"),
                            resultSet.getString("employee_name"),
                            resultSet.getString("appointment_date"),
                            resultSet.getString("appointment_time").substring(0, 5),
                            toVietnamesePayment(resultSet.getString("payment_method")),
                            resultSet.getString("status")));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return appointments;
    }

    private int queryCount(String sql) {
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {

            return resultSet.next() ? resultSet.getInt(1) : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private String formatPrice(BigDecimal price) {
        NumberFormat formatter = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        formatter.setMaximumFractionDigits(0);
        return formatter.format(price == null ? BigDecimal.ZERO : price) + "đ";
    }

    private String toVietnamesePayment(String paymentMethod) {
        if ("BANK_TRANSFER".equals(paymentMethod)) {
            return "Chuyển khoản";
        }
        if ("E_WALLET".equals(paymentMethod)) {
            return "Ví điện tử";
        }

        return "Thanh toán tại salon";
    }
}
