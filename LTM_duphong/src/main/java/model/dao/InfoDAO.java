package model.dao;

import java.sql.*;
import model.bean.Account;
import config.DatabaseConnection;

public class InfoDAO {
    
    public static Account getAccount(String username, String password) {
        // Hardcoded cho test nhanh
        if ("admin".equals(username) && "admin123".equals(password)) {
            Account a = new Account();
            a.setId(1);
            a.setUsername("admin");
            a.setPassword("admin123");
            return a;
        }
        
        // Database query
        String sql = "SELECT * FROM accounts WHERE username=? AND password=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, username);
            ps.setString(2, password);
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Account account = new Account();
                account.setId(rs.getInt("id"));
                account.setUsername(rs.getString("username"));
                account.setPassword(rs.getString("password"));
                return account;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}