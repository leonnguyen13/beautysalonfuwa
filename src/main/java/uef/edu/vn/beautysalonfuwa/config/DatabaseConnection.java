/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package uef.edu.vn.beautysalonfuwa.config;


import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;

/**
 *
 * @author PC
 */
public class DatabaseConnection {
    private static final String URL =
            "jdbc:mysql://localhost:3306/beauty_salon_fuwa?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Ho_Chi_Minh";
    private static final String USER = "root";
    private static final String PASSWORD = "123456";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver chưa được nạp. Kiểm tra mysql-connector-j trong pom.xml.", e);
        }

        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
