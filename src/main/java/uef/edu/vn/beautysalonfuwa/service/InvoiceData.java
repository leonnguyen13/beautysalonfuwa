package uef.edu.vn.beautysalonfuwa.service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;
import uef.edu.vn.beautysalonfuwa.config.DatabaseConnection;
import uef.edu.vn.beautysalonfuwa.model.Invoice;

@Service
public class InvoiceData {

    public List<Invoice> findAll() {
        List<Invoice> invoices = new ArrayList<>();
        String sql = "SELECT i.id, COALESCE(i.appointment_id, 0) AS appointment_id, "
                + "COALESCE(c.full_name, a.full_name, 'Khách lẻ') AS customer_name, "
                + "COALESCE(GROUP_CONCAT(DISTINCT s.name ORDER BY d.id SEPARATOR ', '), 'Dịch vụ khác') AS service_name, "
                + "COALESCE(e.full_name, 'Chọn tự động') AS employee_name, "
                + "i.total_amount, i.payment_method, i.payment_status, DATE(i.created_date) AS created_date "
                + "FROM invoices i "
                + "LEFT JOIN appointments a ON a.id = i.appointment_id "
                + "LEFT JOIN customers c ON c.id = COALESCE(i.customer_id, a.customer_id) "
                + "LEFT JOIN employees e ON e.id = COALESCE(i.employee_id, a.employee_id) "
                + "LEFT JOIN invoice_details d ON d.invoice_id = i.id "
                + "LEFT JOIN services s ON s.id = d.service_id "
                + "GROUP BY i.id, i.appointment_id, c.full_name, a.full_name, e.full_name, "
                + "i.total_amount, i.payment_method, i.payment_status, DATE(i.created_date) "
                + "ORDER BY i.id DESC";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                invoices.add(mapInvoice(resultSet));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return invoices;
    }

    public Invoice create(String customerName, String serviceName, String employeeName,
            String totalAmount, String paymentMethod, String createdDate) {
        String invoiceSql = "INSERT INTO invoices "
                + "(customer_id, employee_id, total_amount, payment_method, payment_status, created_date) "
                + "VALUES (?, ?, ?, ?, 'UNPAID', COALESCE(?, CURRENT_TIMESTAMP))";
        String detailSql = "INSERT INTO invoice_details "
                + "(invoice_id, service_id, quantity, unit_price, subtotal) VALUES (?, ?, 1, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);

            try {
                Integer customerId = findOptionalId(connection,
                        "SELECT id FROM customers WHERE full_name = ? ORDER BY id DESC LIMIT 1",
                        customerName);
                Integer employeeId = findOptionalId(connection,
                        "SELECT id FROM employees WHERE full_name = ? ORDER BY id DESC LIMIT 1",
                        employeeName);
                Integer serviceId = findOptionalId(connection,
                        "SELECT id FROM services WHERE name = ? AND status = 'ACTIVE' LIMIT 1",
                        serviceName);

                if (serviceId == null) {
                    connection.rollback();
                    return null;
                }

                BigDecimal amount = parsePrice(totalAmount);
                int invoiceId;
                try (PreparedStatement statement = connection.prepareStatement(invoiceSql, Statement.RETURN_GENERATED_KEYS)) {
                    setNullableInt(statement, 1, customerId);
                    setNullableInt(statement, 2, employeeId);
                    statement.setBigDecimal(3, amount);
                    statement.setString(4, toDatabasePayment(paymentMethod));
                    statement.setString(5, trimToNull(createdDate));
                    statement.executeUpdate();
                    invoiceId = getGeneratedId(statement);
                }

                try (PreparedStatement statement = connection.prepareStatement(detailSql)) {
                    statement.setInt(1, invoiceId);
                    statement.setInt(2, serviceId);
                    statement.setBigDecimal(3, amount);
                    statement.setBigDecimal(4, amount);
                    statement.executeUpdate();
                }

                connection.commit();
                return findById(invoiceId);
            } catch (Exception e) {
                connection.rollback();
                e.printStackTrace();
                return null;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Invoice createFromAppointment(int appointmentId) {
        String findSql = "SELECT i.id FROM invoices i "
                + "JOIN appointments invoiced_appointment ON invoiced_appointment.id = i.appointment_id "
                + "JOIN appointments selected_appointment ON selected_appointment.id = ? "
                + "WHERE invoiced_appointment.booking_code = selected_appointment.booking_code "
                + "LIMIT 1";
        String invoiceSql = "INSERT INTO invoices "
                + "(appointment_id, customer_id, employee_id, total_amount, payment_method, payment_status) "
                + "SELECT MIN(a.id), MIN(a.customer_id), MIN(a.employee_id), SUM(s.price), "
                + "MIN(a.payment_method), 'UNPAID' "
                + "FROM appointments selected_appointment "
                + "JOIN appointments a ON a.booking_code = selected_appointment.booking_code "
                + "JOIN services s ON s.id = a.service_id "
                + "WHERE selected_appointment.id = ? "
                + "GROUP BY selected_appointment.booking_code";
        String detailSql = "INSERT INTO invoice_details "
                + "(invoice_id, service_id, quantity, unit_price, subtotal) "
                + "SELECT ?, a.service_id, COUNT(*), s.price, SUM(s.price) "
                + "FROM appointments selected_appointment "
                + "JOIN appointments a ON a.booking_code = selected_appointment.booking_code "
                + "JOIN services s ON s.id = a.service_id "
                + "WHERE selected_appointment.id = ? "
                + "GROUP BY a.service_id, s.price";

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);

            try {
                Integer existingId = findInvoiceIdByAppointment(connection, findSql, appointmentId);
                if (existingId != null) {
                    connection.rollback();
                    return findById(existingId);
                }

                int invoiceId;
                try (PreparedStatement statement = connection.prepareStatement(invoiceSql, Statement.RETURN_GENERATED_KEYS)) {
                    statement.setInt(1, appointmentId);
                    if (statement.executeUpdate() == 0) {
                        connection.rollback();
                        return null;
                    }
                    invoiceId = getGeneratedId(statement);
                }

                try (PreparedStatement statement = connection.prepareStatement(detailSql)) {
                    statement.setInt(1, invoiceId);
                    statement.setInt(2, appointmentId);
                    statement.executeUpdate();
                }

                connection.commit();
                return findById(invoiceId);
            } catch (Exception e) {
                connection.rollback();
                e.printStackTrace();
                return null;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean markPaid(int id) {
        String sql = "UPDATE invoices SET payment_status = 'PAID' WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Invoice findById(int id) {
        String sql = "SELECT i.id, COALESCE(i.appointment_id, 0) AS appointment_id, "
                + "COALESCE(c.full_name, a.full_name, 'Khách lẻ') AS customer_name, "
                + "COALESCE(GROUP_CONCAT(DISTINCT s.name ORDER BY d.id SEPARATOR ', '), 'Dịch vụ khác') AS service_name, "
                + "COALESCE(e.full_name, 'Chọn tự động') AS employee_name, "
                + "i.total_amount, i.payment_method, i.payment_status, DATE(i.created_date) AS created_date "
                + "FROM invoices i "
                + "LEFT JOIN appointments a ON a.id = i.appointment_id "
                + "LEFT JOIN customers c ON c.id = COALESCE(i.customer_id, a.customer_id) "
                + "LEFT JOIN employees e ON e.id = COALESCE(i.employee_id, a.employee_id) "
                + "LEFT JOIN invoice_details d ON d.invoice_id = i.id "
                + "LEFT JOIN services s ON s.id = d.service_id "
                + "WHERE i.id = ? "
                + "GROUP BY i.id, i.appointment_id, c.full_name, a.full_name, e.full_name, "
                + "i.total_amount, i.payment_method, i.payment_status, DATE(i.created_date)";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapInvoice(resultSet);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private Invoice mapInvoice(ResultSet resultSet) throws Exception {
        return new Invoice(
                resultSet.getInt("id"),
                resultSet.getInt("appointment_id"),
                resultSet.getString("customer_name"),
                resultSet.getString("service_name"),
                resultSet.getString("employee_name"),
                formatPrice(resultSet.getBigDecimal("total_amount")),
                toVietnamesePayment(resultSet.getString("payment_method")),
                resultSet.getString("payment_status"),
                resultSet.getString("created_date"));
    }

    private Integer findInvoiceIdByAppointment(Connection connection, String sql, int appointmentId) throws Exception {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, appointmentId);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getInt("id") : null;
            }
        }
    }

    private Integer findOptionalId(Connection connection, String sql, String value) throws Exception {
        String normalizedValue = trimToNull(value);
        if (normalizedValue == null) {
            return null;
        }

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, normalizedValue);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getInt("id") : null;
            }
        }
    }

    private int getGeneratedId(PreparedStatement statement) throws Exception {
        try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }
        }

        throw new IllegalStateException("Không thể lấy mã hóa đơn vừa tạo.");
    }

    private void setNullableInt(PreparedStatement statement, int index, Integer value) throws Exception {
        if (value == null) {
            statement.setNull(index, java.sql.Types.INTEGER);
            return;
        }

        statement.setInt(index, value);
    }

    private BigDecimal parsePrice(String value) {
        String normalizedValue = value == null ? "" : value.replace("đ", "").trim();
        normalizedValue = normalizedValue.replace(".", "").replace(",", "");
        return normalizedValue.isEmpty() ? BigDecimal.ZERO : new BigDecimal(normalizedValue);
    }

    private String formatPrice(BigDecimal price) {
        if (price == null) {
            return "0đ";
        }

        NumberFormat formatter = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        formatter.setMaximumFractionDigits(0);
        return formatter.format(price) + "đ";
    }

    private String toDatabasePayment(String paymentMethod) {
        if ("Chuyển khoản".equals(paymentMethod)) {
            return "BANK_TRANSFER";
        }
        if ("Ví điện tử".equals(paymentMethod)) {
            return "E_WALLET";
        }

        return "CASH";
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

    private String trimToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        return value.trim();
    }
}
