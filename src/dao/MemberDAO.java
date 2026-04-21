package dao;

import database.DBConnection;
import models.Member;
import utils.RefreshManager;

import java.sql.*;

public class MemberDAO {

    // ================= ADD MEMBER =================
    public void addMember(Member member) {
        String sql = "INSERT INTO members(id, name, email, type) VALUES(?, ?, ?, ?)";

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, member.getId());
            stmt.setString(2, member.getName());
            stmt.setString(3, member.getEmail());
            stmt.setString(4, member.getType());

            stmt.executeUpdate();
            System.out.println("Member added successfully");

            // REFRESH CHART
            RefreshManager.refreshCharts();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // ================= VIEW MEMBERS =================
    public void getAllMembers() {
        String sql = "SELECT * FROM members";

        try (Connection conn = DBConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                System.out.println(
                    rs.getInt("id") + " | " +
                    rs.getString("name") + " | " +
                    rs.getString("email") + " | " +
                    rs.getString("type")
                );
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // ================= UPDATE MEMBER =================
    public void updateMember(int id, String name, String email) {
        String sql = "UPDATE members SET name = ?, email = ? WHERE id = ?";

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setInt(3, id);

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Member updated successfully");

                // REFRESH CHART
                RefreshManager.refreshCharts();

            } else {
                System.out.println("Member not found");
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // ================= DELETE MEMBER =================
    public void deleteMember(int id) {
        String sql = "DELETE FROM members WHERE id = ?";

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Member deleted successfully");

                // REFRESH CHART
                RefreshManager.refreshCharts();

            } else {
                System.out.println("Member not found");
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // ================= SEARCH MEMBER =================
    public void searchMember(String keyword) {
        String sql = "SELECT * FROM members WHERE name LIKE ?";

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + keyword + "%");

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                System.out.println(
                    rs.getInt("id") + " | " +
                    rs.getString("name") + " | " +
                    rs.getString("email") + " | " +
                    rs.getString("type")
                );
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}