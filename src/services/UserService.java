package services;

import dao.UserDAO;
import database.DBConnection;
import models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class UserService {

    private UserDAO userDAO = new UserDAO();

    // ================= REGISTER USER (FIXED) =================
    public boolean registerUser(String username, String password, String role) {

        String sql = "INSERT INTO users(username, password, role) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, role);

            ps.executeUpdate();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ================= LOGIN USER =================
    public User login(String username, String password) {

        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DBConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role")
                );
            }

        } catch (Exception e) {
            System.out.println("Login Error: " + e.getMessage());
            return userDAO.authenticate(username, password);
        }

        return null;
    }

    // ================= GET ALL USERS =================
    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }

    // ================= DELETE USER =================
    public boolean deleteUser(int id) {
        return userDAO.deleteUser(id);
    }
}