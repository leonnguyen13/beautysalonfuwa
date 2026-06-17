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
import uef.edu.vn.beautysalonfuwa.model.SalonService;

@Service
public class ServiceData {

    public List<SalonService> findAll() {
        List<SalonService> services = new ArrayList<>();
        String sql = "SELECT s.id, s.name, s.description, s.price, s.image_url, c.name AS category_name "
                + "FROM services s "
                + "LEFT JOIN service_categories c ON s.category_id = c.id "
                + "WHERE s.status = 'ACTIVE' "
                + "ORDER BY s.id";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                services.add(new SalonService(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("description"),
                        formatPrice(resultSet.getBigDecimal("price")),
                        resultSet.getString("image_url"),
                        resultSet.getString("category_name")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return services;
    }

    public List<SalonService> findFeatured() {
        List<SalonService> allServices = findAll();
        List<SalonService> featuredServices = new ArrayList<>();

        for (int index = 0; index < allServices.size() && index < 3; index++) {
            featuredServices.add(allServices.get(index));
        }

        return featuredServices;
    }

    public SalonService findById(int id) {
        String sql = "SELECT s.id, s.name, s.description, s.price, s.image_url, c.name AS category_name "
                + "FROM services s "
                + "LEFT JOIN service_categories c ON s.category_id = c.id "
                + "WHERE s.id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    BigDecimal price = resultSet.getBigDecimal("price");
                    return new SalonService(
                            resultSet.getInt("id"),
                            resultSet.getString("name"),
                            resultSet.getString("description"),
                            price == null ? "0" : price.setScale(0).toPlainString(),
                            resultSet.getString("image_url"),
                            resultSet.getString("category_name"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean save(SalonService service) {
        if (service.getId() > 0) {
            return update(service);
        }

        return create(service);
    }

    public boolean delete(int id) {
        String sql = "UPDATE services SET status = 'INACTIVE' WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean create(SalonService service) {
        String sql = "INSERT INTO services (category_id, name, description, price, image_url, status) "
                + "VALUES (?, ?, ?, ?, ?, 'ACTIVE')";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, findOrCreateCategoryId(connection, service.getCategory()));
            statement.setString(2, service.getName().trim());
            statement.setString(3, service.getDescription().trim());
            statement.setBigDecimal(4, parsePrice(service.getPriceText()));
            statement.setString(5, service.getImageUrl().trim());

            return statement.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean update(SalonService service) {
        String sql = "UPDATE services SET category_id = ?, name = ?, description = ?, price = ?, image_url = ? "
                + "WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, findOrCreateCategoryId(connection, service.getCategory()));
            statement.setString(2, service.getName().trim());
            statement.setString(3, service.getDescription().trim());
            statement.setBigDecimal(4, parsePrice(service.getPriceText()));
            statement.setString(5, service.getImageUrl().trim());
            statement.setInt(6, service.getId());

            return statement.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private int findOrCreateCategoryId(Connection connection, String categoryName) throws Exception {
        String normalizedCategory = categoryName == null || categoryName.trim().isEmpty()
                ? "Other"
                : categoryName.trim();
        String findSql = "SELECT id FROM service_categories WHERE name = ?";

        try (PreparedStatement statement = connection.prepareStatement(findSql)) {
            statement.setString(1, normalizedCategory);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id");
                }
            }
        }

        String insertSql = "INSERT INTO service_categories (name) VALUES (?)";
        try (PreparedStatement statement = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, normalizedCategory);
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        }

        throw new IllegalStateException("Không thể tạo danh mục dịch vụ.");
    }

    private BigDecimal parsePrice(String priceText) {
        if (priceText == null || priceText.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }

        String normalizedPrice = priceText.replace("đ", "").trim();
        if (normalizedPrice.matches("\\d+\\.\\d{1,2}")) {
            normalizedPrice = normalizedPrice.substring(0, normalizedPrice.indexOf("."));
        } else {
            normalizedPrice = normalizedPrice.replace(".", "").replace(",", "");
        }

        return new BigDecimal(normalizedPrice);
    }

    private String formatPrice(BigDecimal price) {
        if (price == null) {
            return "0đ";
        }

        NumberFormat formatter = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        formatter.setMaximumFractionDigits(0);
        return formatter.format(price) + "đ";
    }
}
