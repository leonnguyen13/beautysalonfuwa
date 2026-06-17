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
            "mysql://u6iyzwrkw74bbv25:EYmP7FW3ntrjGUnLsjIg@b66hancuzjarvzrp8bb4-mysql.services.clever-cloud.com:3306/b66hancuzjarvzrp8bb4";
    private static final String USER = "u6iyzwrkw74bbv25";
    private static final String PASSWORD = "EYmP7FW3ntrjGUnLsjIg";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver chưa được nạp. Kiểm tra mysql-connector-j trong pom.xml.", e);
        }

        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
