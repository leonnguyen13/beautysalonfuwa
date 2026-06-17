package uef.edu.vn.beautysalonfuwa.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import uef.edu.vn.beautysalonfuwa.config.DatabaseConnection;
import uef.edu.vn.beautysalonfuwa.model.Employee;

@Service
public class EmployeeData {
    private static final String PHONE_PATTERN = "^0\\d{9}$";

    public List<Employee> findAll() {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT id, full_name, phone, email, position, specialty, status "
                + "FROM employees WHERE status = 'ACTIVE' ORDER BY id DESC";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                employees.add(mapEmployee(resultSet));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return employees;
    }

    public List<Employee> findFeatured() {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT id, full_name, phone, email, position, specialty, status "
                + "FROM employees WHERE status = 'ACTIVE' ORDER BY id LIMIT 3";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                employees.add(mapEmployee(resultSet));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return employees;
    }

    public Employee findById(int id) {
        String sql = "SELECT id, full_name, phone, email, position, specialty, status FROM employees WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapEmployee(resultSet);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean save(Employee employee) {
        if (employee.getId() > 0) {
            return update(employee);
        }

        return create(employee);
    }

    public String validate(Employee employee) {
        if (trim(employee.getFullName()).isEmpty()) {
            return "Vui lòng nhập họ tên nhân viên.";
        }
        if (!trim(employee.getPhone()).matches(PHONE_PATTERN)) {
            return "Số điện thoại phải bắt đầu bằng 0 và gồm đúng 10 chữ số.";
        }
        if (trim(employee.getEmail()).isEmpty()) {
            return "Vui lòng nhập email nhân viên.";
        }
        if (trim(employee.getPosition()).isEmpty()) {
            return "Vui lòng nhập chức vụ nhân viên.";
        }

        String sql = "SELECT "
                + "(SELECT COUNT(*) FROM employees WHERE email = ? AND id <> ?) AS employee_count, "
                + "(SELECT COUNT(*) FROM users u WHERE u.email = ? "
                + "AND u.id <> COALESCE((SELECT user_id FROM employees WHERE id = ?), 0) "
                + "AND (? = 0 OR u.role NOT IN ('STAFF', 'MANAGER', 'ADMIN') "
                + "OR EXISTS (SELECT 1 FROM employees e WHERE e.user_id = u.id AND e.id <> ?))) AS user_count";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, trim(employee.getEmail()));
            statement.setInt(2, employee.getId());
            statement.setString(3, trim(employee.getEmail()));
            statement.setInt(4, employee.getId());
            statement.setInt(5, employee.getId());
            statement.setInt(6, employee.getId());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next() && (resultSet.getInt("employee_count") > 0 || resultSet.getInt("user_count") > 0)) {
                    return "Email đã được sử dụng. Vui lòng nhập email khác.";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Không thể kiểm tra dữ liệu nhân viên. Vui lòng thử lại.";
        }

        return null;
    }

    public boolean delete(int id) {
        String employeeSql = "UPDATE employees SET status = 'INACTIVE' WHERE id = ?";
        String userSql = "UPDATE users u JOIN employees e ON e.user_id = u.id "
                + "SET u.status = 'INACTIVE' WHERE e.id = ?";

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement employeeStatement = connection.prepareStatement(employeeSql);
                    PreparedStatement userStatement = connection.prepareStatement(userSql)) {

                employeeStatement.setInt(1, id);
                boolean updated = employeeStatement.executeUpdate() > 0;

                userStatement.setInt(1, id);
                userStatement.executeUpdate();

                connection.commit();
                return updated;
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

    private boolean create(Employee employee) {
        String insertUserSql = "INSERT INTO users (full_name, email, password, phone, role, status) VALUES (?, ?, ?, ?, ?, ?)";
        String insertEmployeeSql = "INSERT INTO employees (user_id, full_name, phone, email, position, specialty, status) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement userStatement = connection.prepareStatement(insertUserSql, Statement.RETURN_GENERATED_KEYS)) {
                userStatement.setString(1, trim(employee.getFullName()));
                userStatement.setString(2, trim(employee.getEmail()));
                userStatement.setString(3, normalizePassword(employee.getPassword()));
                userStatement.setString(4, trim(employee.getPhone()));
                userStatement.setString(5, normalizeRole(employee.getRole()));
                userStatement.setString(6, normalizeStatus(employee.getStatus()));
                userStatement.executeUpdate();

                int userId;
                try (ResultSet generatedKeys = userStatement.getGeneratedKeys()) {
                    if (!generatedKeys.next()) {
                        connection.rollback();
                        return false;
                    }
                    userId = generatedKeys.getInt(1);
                }

                try (PreparedStatement employeeStatement = connection.prepareStatement(insertEmployeeSql)) {
                    employeeStatement.setInt(1, userId);
                    setEmployeeFields(employeeStatement, employee, 2);
                    employeeStatement.executeUpdate();
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

    private boolean update(Employee employee) {
        String employeeSql = "UPDATE employees SET full_name = ?, phone = ?, email = ?, position = ?, specialty = ?, status = ? WHERE id = ?";
        String linkUserSql = "UPDATE employees e JOIN users u ON u.email = e.email "
                + "SET e.user_id = u.id "
                + "WHERE e.id = ? AND e.user_id IS NULL AND u.role IN ('STAFF', 'MANAGER', 'ADMIN')";
        String userSql = "UPDATE users u JOIN employees e ON e.user_id = u.id "
                + "SET u.full_name = ?, u.phone = ?, u.email = ?, u.status = ? WHERE e.id = ?";

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement employeeStatement = connection.prepareStatement(employeeSql);
                    PreparedStatement linkUserStatement = connection.prepareStatement(linkUserSql);
                    PreparedStatement userStatement = connection.prepareStatement(userSql)) {

                setEmployeeFields(employeeStatement, employee, 1);
                employeeStatement.setInt(7, employee.getId());
                boolean updated = employeeStatement.executeUpdate() > 0;

                linkUserStatement.setInt(1, employee.getId());
                linkUserStatement.executeUpdate();

                userStatement.setString(1, trim(employee.getFullName()));
                userStatement.setString(2, trim(employee.getPhone()));
                userStatement.setString(3, trim(employee.getEmail()));
                userStatement.setString(4, normalizeStatus(employee.getStatus()));
                userStatement.setInt(5, employee.getId());
                userStatement.executeUpdate();

                connection.commit();
                return updated;
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

    private Employee mapEmployee(ResultSet resultSet) throws Exception {
        return new Employee(
                resultSet.getInt("id"),
                resultSet.getString("full_name"),
                resultSet.getString("phone"),
                resultSet.getString("email"),
                resultSet.getString("position"),
                resultSet.getString("specialty"),
                resultSet.getString("status"));
    }

    private void setEmployeeFields(PreparedStatement statement, Employee employee, int startIndex) throws Exception {
        statement.setString(startIndex, trim(employee.getFullName()));
        statement.setString(startIndex + 1, trim(employee.getPhone()));
        statement.setString(startIndex + 2, trim(employee.getEmail()));
        statement.setString(startIndex + 3, trim(employee.getPosition()));
        statement.setString(startIndex + 4, trim(employee.getSpecialty()));
        statement.setString(startIndex + 5, normalizeStatus(employee.getStatus()));
    }

    private String normalizeStatus(String status) {
        if ("INACTIVE".equals(status)) {
            return "INACTIVE";
        }

        return "ACTIVE";
    }

    private String normalizeRole(String role) {
        if ("MANAGER".equals(role)) {
            return "MANAGER";
        }

        if ("ADMIN".equals(role)) {
            return "ADMIN";
        }

        return "STAFF";
    }

    private String normalizePassword(String password) {
        String value = trim(password);
        return value.isEmpty() ? "123456" : value;
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
