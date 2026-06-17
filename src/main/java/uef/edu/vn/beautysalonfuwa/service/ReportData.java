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
import uef.edu.vn.beautysalonfuwa.model.ReportSummary;
import uef.edu.vn.beautysalonfuwa.model.SalonService;

@Service
public class ReportData {

    public List<ReportSummary> getSummaries() {
        List<ReportSummary> summaries = new ArrayList<>();
        String totalNote = "Tất cả dữ liệu";

        summaries.add(new ReportSummary("Tổng doanh thu", getPaidRevenue(), totalNote));
        summaries.add(new ReportSummary("Tổng lịch hẹn", String.valueOf(countAppointments()), totalNote));
        summaries.add(new ReportSummary("Tổng khách hàng", String.valueOf(countCustomers()), totalNote));
        summaries.add(new ReportSummary("Hóa đơn hoàn thành", String.valueOf(countPaidInvoices()), "Đơn đã thanh toán"));

        return summaries;
    }

    public List<SalonService> getPopularServices() {
        List<SalonService> services = new ArrayList<>();
        String sql = "SELECT s.id, s.name, COALESCE(c.name, 'Chưa phân loại') AS category_name, "
                + "COUNT(DISTINCT a.id) AS booking_count, "
                + "COALESCE((SELECT SUM(d.subtotal) "
                + "FROM invoice_details d "
                + "JOIN invoices i ON i.id = d.invoice_id "
                + "WHERE d.service_id = s.id AND i.payment_status = 'PAID'), 0) AS paid_revenue "
                + "FROM services s "
                + "LEFT JOIN service_categories c ON c.id = s.category_id "
                + "LEFT JOIN appointments a ON a.service_id = s.id "
                + "WHERE s.status = 'ACTIVE' "
                + "GROUP BY s.id, s.name, c.name "
                + "ORDER BY booking_count DESC, s.id "
                + "LIMIT 5";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                services.add(new SalonService(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getInt("booking_count") + " lượt đặt",
                        formatPrice(resultSet.getBigDecimal("paid_revenue")),
                        "",
                        resultSet.getString("category_name")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return services;
    }

    private int countAppointments() {
        String sql = "SELECT COUNT(*) FROM ("
                + "SELECT 1 FROM appointments "
                + "GROUP BY COALESCE(booking_code, CONCAT('ID-', id)), full_name, phone, employee_id, "
                + "appointment_date, appointment_time, payment_method, status"
                + ") appointment_groups";
        return queryCount(sql);
    }

    private int countCustomers() {
        return queryCount("SELECT COUNT(*) FROM customers");
    }

    private int countPaidInvoices() {
        return queryCount("SELECT COUNT(*) FROM invoices WHERE payment_status = 'PAID'");
    }

    private String getPaidRevenue() {
        String sql = "SELECT COALESCE(SUM(total_amount), 0) FROM invoices "
                + "WHERE payment_status = 'PAID'";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {

            return resultSet.next() ? formatPrice(resultSet.getBigDecimal(1)) : "0đ";
        } catch (Exception e) {
            e.printStackTrace();
            return "0đ";
        }
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
}
