package uef.edu.vn.beautysalonfuwa.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import uef.edu.vn.beautysalonfuwa.config.DatabaseConnection;
import uef.edu.vn.beautysalonfuwa.model.Customer;

@Service
public class CustomerData {
    private static final String PHONE_PATTERN = "^0\\d{9}$";

    public List<Customer> findAll() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT id, full_name, phone, email, gender, address FROM customers ORDER BY id DESC";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                customers.add(new Customer(
                        resultSet.getInt("id"),
                        resultSet.getString("full_name"),
                        resultSet.getString("phone"),
                        resultSet.getString("email"),
                        resultSet.getString("gender"),
                        resultSet.getString("address")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return customers;
    }

    public Customer findById(int id) {
        String sql = "SELECT id, full_name, phone, email, gender, address FROM customers WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new Customer(
                            resultSet.getInt("id"),
                            resultSet.getString("full_name"),
                            resultSet.getString("phone"),
                            resultSet.getString("email"),
                            resultSet.getString("gender"),
                            resultSet.getString("address"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean save(Customer customer) {
        if (customer.getId() > 0) {
            return update(customer);
        }

        return create(customer);
    }

    public String validate(Customer customer) {
        if (trim(customer.getFullName()).isEmpty()) {
            return "Vui lòng nhập họ tên khách hàng.";
        }
        if (!trim(customer.getPhone()).matches(PHONE_PATTERN)) {
            return "Số điện thoại phải bắt đầu bằng 0 và gồm đúng 10 chữ số.";
        }

        return null;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM customers WHERE id = ? AND id NOT IN (SELECT customer_id FROM appointments WHERE customer_id IS NOT NULL)";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean create(Customer customer) {
        String sql = "INSERT INTO customers (full_name, phone, email, gender, address) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            setCustomerFields(statement, customer);
            return statement.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean update(Customer customer) {
        String sql = "UPDATE customers SET full_name = ?, phone = ?, email = ?, gender = ?, address = ? WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            setCustomerFields(statement, customer);
            statement.setInt(6, customer.getId());
            return statement.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void setCustomerFields(PreparedStatement statement, Customer customer) throws Exception {
        statement.setString(1, trim(customer.getFullName()));
        statement.setString(2, trim(customer.getPhone()));
        statement.setString(3, trim(customer.getEmail()));
        statement.setString(4, normalizeGender(customer.getGender()));
        statement.setString(5, trim(customer.getAddress()));
    }

    private String normalizeGender(String gender) {
        if ("MALE".equals(gender) || "FEMALE".equals(gender) || "OTHER".equals(gender)) {
            return gender;
        }

        return "FEMALE";
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
