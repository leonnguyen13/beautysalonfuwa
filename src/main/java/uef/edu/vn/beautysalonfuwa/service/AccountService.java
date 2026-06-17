package uef.edu.vn.beautysalonfuwa.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import org.springframework.stereotype.Service;
import uef.edu.vn.beautysalonfuwa.config.DatabaseConnection;
import uef.edu.vn.beautysalonfuwa.model.AuthUser;

@Service
public class AccountService {
    private static final String FUWA_EMAIL_PATTERN = "^[A-Za-z0-9._%+-]+@fuwa\\.vn$";
    private static final String PHONE_PATTERN = "^0\\d{9}$";
    private static final String PASSWORD_PATTERN = "^\\d{6}$";

    public boolean emailExists(String email) {
        String sql = "SELECT id FROM users WHERE email = ?";
        String normalizedEmail = email == null ? "" : email.trim();

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, normalizedEmail);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    public boolean registerCustomer(String fullName, String email, String phone, String password) {
        String insertUserSql = "INSERT INTO users (full_name, email, password, phone, role) "
                + "VALUES (?, ?, ?, ?, 'CUSTOMER')";
        String insertCustomerSql = "INSERT INTO customers (user_id, full_name, phone, email) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement userStatement = connection.prepareStatement(insertUserSql, Statement.RETURN_GENERATED_KEYS)) {
                userStatement.setString(1, fullName.trim());
                userStatement.setString(2, email.trim());
                userStatement.setString(3, password.trim());
                userStatement.setString(4, phone.trim());
                userStatement.executeUpdate();

                int userId;
                try (ResultSet generatedKeys = userStatement.getGeneratedKeys()) {
                    if (!generatedKeys.next()) {
                        connection.rollback();
                        return false;
                    }
                    userId = generatedKeys.getInt(1);
                }

                try (PreparedStatement customerStatement = connection.prepareStatement(insertCustomerSql)) {
                    customerStatement.setInt(1, userId);
                    customerStatement.setString(2, fullName.trim());
                    customerStatement.setString(3, phone.trim());
                    customerStatement.setString(4, email.trim());
                    customerStatement.executeUpdate();
                }

                connection.commit();
                return true;
            } catch (Exception e) {
                connection.rollback();
                e.printStackTrace();
                return false;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String validateRegistration(String fullName, String email, String phone, String password) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return "Vui lòng nhập họ tên.";
        }

        if (email == null || !email.trim().matches(FUWA_EMAIL_PATTERN)) {
            return "Email phải đúng định dạng và có đuôi @fuwa.vn.";
        }

        if (phone == null || !phone.trim().matches(PHONE_PATTERN)) {
            return "Số điện thoại phải bắt đầu bằng 0 và gồm đúng 10 chữ số.";
        }

        if (password == null || !password.trim().matches(PASSWORD_PATTERN)) {
            return "Mật khẩu phải gồm đúng 6 chữ số.";
        }

        return null;
    }

    public AuthUser login(String email, String password) {
        String sql = "SELECT id, full_name, email, password, role, status FROM users WHERE email = ?";
        String normalizedEmail = email == null ? "" : email.trim();
        String normalizedPassword = password == null ? "" : password.trim();

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, normalizedEmail);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String databasePassword = resultSet.getString("password");
                    String status = resultSet.getString("status");

                    if (!"ACTIVE".equals(status)) {
                        return null;
                    }

                    if (databasePassword == null || !databasePassword.trim().equals(normalizedPassword)) {
                        return null;
                    }

                    return new AuthUser(
                            resultSet.getInt("id"),
                            resultSet.getString("full_name"),
                            resultSet.getString("email"),
                            resultSet.getString("role"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getLoginErrorMessage(String email, String password) {
        String sql = "SELECT password, status, role FROM users WHERE email = ?";
        String normalizedEmail = email == null ? "" : email.trim();
        String normalizedPassword = password == null ? "" : password.trim();

        if (normalizedEmail.isEmpty()) {
            return "Vui lòng nhập email.";
        }

        if (normalizedPassword.isEmpty()) {
            return "Vui lòng nhập mật khẩu.";
        }

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, normalizedEmail);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return "Email này chưa được đăng ký.";
                }

                String databasePassword = resultSet.getString("password");
                String status = resultSet.getString("status");

                if (!"ACTIVE".equals(status)) {
                    return "Tài khoản này đang bị khóa hoặc ngừng hoạt động.";
                }

                if (databasePassword == null) {
                    return "Tài khoản chưa có mật khẩu hợp lệ. Vui lòng liên hệ quản trị viên.";
                }

                if (!databasePassword.trim().equals(normalizedPassword)) {
                    return "Mật khẩu không đúng.";
                }

                return "Đăng nhập hợp lệ. Vui lòng thử tải lại trang.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Không thể kết nối database. Vui lòng thử lại sau.";
        }
    }
}
