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
    // Đã cập nhật URL trỏ đến máy chủ Clever Cloud, hỗ trợ Unicode tiếng Việt đầy đủ
    private static final String URL = 
            "jdbc:mysql://b66hancuzjarvzrp8bb4-mysql.services.clever-cloud.com:3306/b66hancuzjarvzrp8bb4?useUnicode=true&characterEncoding=UTF-8&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Ho_Chi_Minh";
    
    // Tài khoản kết nối do Clever Cloud cấp phát
    private static final String USER = "u6iyzwrkw74bbv25";
    
    // BẠN LƯU Ý: Hãy mở trang Clever Cloud, copy mật khẩu thật ở dòng 'Password' rồi dán đè thay thế cho chữ dưới đây nha!
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
