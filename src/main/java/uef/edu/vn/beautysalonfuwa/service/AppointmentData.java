package uef.edu.vn.beautysalonfuwa.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import uef.edu.vn.beautysalonfuwa.config.DatabaseConnection;
import uef.edu.vn.beautysalonfuwa.model.Appointment;

@Service
public class AppointmentData {
    private static final LocalTime OPEN_TIME = LocalTime.of(8, 0);
    private static final LocalTime CLOSE_TIME = LocalTime.of(20, 0);
    private static final String PHONE_PATTERN = "^0\\d{9}$";

    public List<Appointment> findAll() {
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
                + "ORDER BY a.appointment_date DESC, a.appointment_time DESC, MIN(a.id) DESC";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {

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
        } catch (Exception e) {
            e.printStackTrace();
        }

        return appointments;
    }

    public boolean save(Appointment appointment) {
        return save(appointment, null, generateBookingCode());
    }

    public boolean save(Appointment appointment, Integer userId) {
        return save(appointment, userId, generateBookingCode());
    }

    public boolean save(Appointment appointment, Integer userId, String bookingCode) {
        String sql = "INSERT INTO appointments "
                + "(booking_code, customer_id, employee_id, service_id, full_name, email, phone, appointment_date, "
                + "appointment_time, payment_method, status, note) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'PENDING', ?)";

        try (Connection connection = DatabaseConnection.getConnection()) {
            Integer customerId = findOrCreateCustomer(connection, appointment, userId);
            Integer employeeId = findEmployeeId(connection, appointment.getEmployee());
            Integer serviceId = findServiceId(connection, appointment.getService());

            if (serviceId == null) {
                return false;
            }

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, bookingCode);
                setNullableInt(statement, 2, customerId);
                setNullableInt(statement, 3, employeeId);
                statement.setInt(4, serviceId);
                statement.setString(5, appointment.getFullName().trim());
                statement.setString(6, trimToNull(appointment.getEmail()));
                statement.setString(7, appointment.getPhone().trim());
                statement.setString(8, appointment.getAppointmentDate());
                statement.setString(9, appointment.getAppointmentTime());
                statement.setString(10, toDatabasePayment(appointment.getPaymentMethod()));
                statement.setString(11, trimToNull(appointment.getNote()));

                return statement.executeUpdate() > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String validateBooking(Appointment appointment) {
        LocalDate appointmentDate;
        LocalTime appointmentTime;

        if (appointment.getPhone() == null || !appointment.getPhone().trim().matches(PHONE_PATTERN)) {
            return "Số điện thoại phải bắt đầu bằng 0 và gồm đúng 10 chữ số.";
        }

        try {
            appointmentDate = LocalDate.parse(appointment.getAppointmentDate());
            appointmentTime = LocalTime.parse(appointment.getAppointmentTime());
        } catch (DateTimeParseException | NullPointerException e) {
            return "Vui lòng chọn ngày và giờ hẹn hợp lệ.";
        }

        if (appointmentTime.isBefore(OPEN_TIME) || appointmentTime.isAfter(CLOSE_TIME)) {
            return "Salon chỉ nhận lịch từ 08:00 đến 20:00.";
        }

        if (appointmentTime.getMinute() != 0 && appointmentTime.getMinute() != 30) {
            return "Vui lòng chọn khung giờ theo mỗi 30 phút, ví dụ 09:00 hoặc 09:30.";
        }

        if (hasEmployeeConflict(appointment)) {
            return "Nhân viên này đã có lịch ở khung giờ đã chọn. Vui lòng chọn giờ khác hoặc chọn tự động.";
        }

        return null;
    }

    public List<Appointment> findByUserId(int userId) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT MIN(a.id) AS id, a.full_name, a.phone, "
                + "GROUP_CONCAT(DISTINCT s.name ORDER BY a.id SEPARATOR ', ') AS service_name, "
                + "COALESCE(e.full_name, 'Chọn tự động') AS employee_name, "
                + "a.appointment_date, a.appointment_time, a.payment_method, a.status "
                + "FROM appointments a "
                + "JOIN customers c ON a.customer_id = c.id "
                + "JOIN services s ON a.service_id = s.id "
                + "LEFT JOIN employees e ON a.employee_id = e.id "
                + "WHERE c.user_id = ? "
                + "GROUP BY a.booking_code, a.full_name, a.phone, e.full_name, "
                + "a.appointment_date, a.appointment_time, a.payment_method, a.status "
                + "ORDER BY a.appointment_date DESC, a.appointment_time DESC, MIN(a.id) DESC";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);

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

    public boolean updateStatus(int id, String status) {
        String sql = "UPDATE appointments SET status = ? "
                + "WHERE booking_code = (SELECT booking_code FROM "
                + "(SELECT booking_code FROM appointments WHERE id = ?) selected_appointment)";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, normalizeStatus(status));
            statement.setInt(2, id);
            return statement.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String generateBookingCode() {
        return "BK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public List<String> findActiveServiceNames() {
        return findNames("SELECT name FROM services WHERE status = 'ACTIVE' ORDER BY name");
    }

    public List<String> findActiveEmployeeNames() {
        List<String> employeeNames = new ArrayList<>();
        employeeNames.add("Chọn tự động");
        employeeNames.addAll(findNames("SELECT full_name FROM employees WHERE status = 'ACTIVE' ORDER BY full_name"));
        return employeeNames;
    }

    private List<String> findNames(String sql) {
        List<String> names = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                names.add(resultSet.getString(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return names;
    }

    private Integer findServiceId(Connection connection, String serviceName) throws Exception {
        return findIdByName(connection, "SELECT id FROM services WHERE name = ? AND status = 'ACTIVE'", serviceName);
    }

    private Integer findEmployeeId(Connection connection, String employeeName) throws Exception {
        if (employeeName == null || employeeName.trim().isEmpty() || "Chọn tự động".equals(employeeName.trim())) {
            return null;
        }
        return findIdByName(connection, "SELECT id FROM employees WHERE full_name = ? AND status = 'ACTIVE'", employeeName);
    }

    private boolean hasEmployeeConflict(Appointment appointment) {
        String sql = "SELECT COUNT(*) FROM appointments "
                + "WHERE employee_id = ? AND appointment_date = ? AND appointment_time = ? "
                + "AND status <> 'CANCELLED'";

        try (Connection connection = DatabaseConnection.getConnection()) {
            Integer employeeId = findEmployeeId(connection, appointment.getEmployee());
            if (employeeId == null) {
                return false;
            }

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, employeeId);
                statement.setString(2, appointment.getAppointmentDate());
                statement.setString(3, appointment.getAppointmentTime());

                try (ResultSet resultSet = statement.executeQuery()) {
                    return resultSet.next() && resultSet.getInt(1) > 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    private Integer findIdByName(Connection connection, String sql, String name) throws Exception {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name.trim());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id");
                }
            }
        }

        return null;
    }

    private Integer findOrCreateCustomer(Connection connection, Appointment appointment, Integer userId) throws Exception {
        if (userId != null) {
            Integer customerId = findCustomerIdByUserId(connection, userId);
            if (customerId != null) {
                updateCustomerFromAppointment(connection, customerId, appointment);
                return customerId;
            }
        }

        String phone = appointment.getPhone().trim();
        String findSql = "SELECT id FROM customers WHERE phone = ? LIMIT 1";

        try (PreparedStatement statement = connection.prepareStatement(findSql)) {
            statement.setString(1, phone);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Integer customerId = resultSet.getInt("id");
                    if (userId != null) {
                        linkCustomerToUser(connection, customerId, userId);
                    }
                    return customerId;
                }
            }
        }

        String insertSql = "INSERT INTO customers (user_id, full_name, phone, email) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            if (userId == null) {
                statement.setNull(1, java.sql.Types.INTEGER);
            } else {
                statement.setInt(1, userId);
            }
            statement.setString(2, appointment.getFullName().trim());
            statement.setString(3, phone);
            statement.setString(4, trimToNull(appointment.getEmail()));
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        }

        return null;
    }

    private Integer findCustomerIdByUserId(Connection connection, int userId) throws Exception {
        String sql = "SELECT id FROM customers WHERE user_id = ? LIMIT 1";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id");
                }
            }
        }

        return null;
    }

    private void linkCustomerToUser(Connection connection, int customerId, int userId) throws Exception {
        String sql = "UPDATE customers SET user_id = ? WHERE id = ? AND user_id IS NULL";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            statement.setInt(2, customerId);
            statement.executeUpdate();
        }
    }

    private void updateCustomerFromAppointment(Connection connection, int customerId, Appointment appointment) throws Exception {
        String sql = "UPDATE customers SET full_name = ?, phone = ?, email = ? WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, appointment.getFullName().trim());
            statement.setString(2, appointment.getPhone().trim());
            statement.setString(3, trimToNull(appointment.getEmail()));
            statement.setInt(4, customerId);
            statement.executeUpdate();
        }
    }

    private void setNullableInt(PreparedStatement statement, int index, Integer value) throws Exception {
        if (value == null) {
            statement.setNull(index, java.sql.Types.INTEGER);
            return;
        }

        statement.setInt(index, value);
    }

    private String trimToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        return value.trim();
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

    private String normalizeStatus(String status) {
        if ("CONFIRMED".equals(status)
                || "PROCESSING".equals(status)
                || "COMPLETED".equals(status)
                || "CANCELLED".equals(status)) {
            return status;
        }

        return "PENDING";
    }

}
