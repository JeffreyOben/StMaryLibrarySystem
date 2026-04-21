package dao;

import database.DBConnection;
import models.User;
import utils.RefreshManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    // ================= ADD USER =================
    public boolean addUser(String username, String password, String role) {

        String sql = "INSERT INTO users(username, password, role) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, role);

            ps.executeUpdate();

            // REFRESH CHARTS
            RefreshManager.refreshCharts();

            return true;

        } catch (Exception e) {
            System.out.println("Add User Error: " + e.getMessage());
            return false;
        }
    }

    // ================= AUTHENTICATE =================
    public User authenticate(String username, String password) {

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
            System.out.println("Authentication Error: " + e.getMessage());
        }

        return null;
    }

    // ================= GET ALL USERS =================
    public List<User> getAllUsers() {

        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Connection conn = DBConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role")
                ));
            }

        } catch (Exception e) {
            System.out.println("Get Users Error: " + e.getMessage());
        }

        return users;
    }

    // ================= DELETE USER =================
    public boolean deleteUser(int id) {

        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection conn = DBConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

            // REFRESH CHARTS
            RefreshManager.refreshCharts();

            return true;

        } catch (Exception e) {
            System.out.println("Delete User Error: " + e.getMessage());
            return false;
        }
    }
}