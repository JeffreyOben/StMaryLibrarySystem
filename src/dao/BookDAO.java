package dao;

import database.DBConnection;
import utils.RefreshManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.Book;
import models.Borrow;

public class BookDAO {

    // ================= ADD BOOK =================
    public void addBook(int id, String title, String author, String category) {

        String sql = "INSERT INTO books(id, title, author, category, available) VALUES (?, ?, ?, ?, 1)";

        try (Connection conn = DBConnection.connect();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.setString(2, title);
            ps.setString(3, author);
            ps.setString(4, category);

            ps.executeUpdate();
            System.out.println("Book added successfully");

            // REFRESH CHART
            RefreshManager.refreshCharts();

        } catch (Exception e) {
            System.out.println("Add Book Error: " + e.getMessage());
        }
    }

    // ================= VIEW BOOKS =================
    public void getAllBooks() {

        String sql = "SELECT * FROM books";

        try (Connection conn = DBConnection.connect();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                System.out.println(
                    rs.getInt("id") + " | " +
                    rs.getString("title") + " | " +
                    rs.getString("author") + " | " +
                    rs.getString("category") + " | " +
                    rs.getInt("available")
                );
            }

        } catch (Exception e) {
            System.out.println("View Error: " + e.getMessage());
        }
    }

    // ================= DELETE BOOK =================
    public void deleteBook(int id) {

        String sql = "DELETE FROM books WHERE id=?";

        try (Connection conn = DBConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Book deleted successfully");

                // 🔥 REFRESH CHART
                RefreshManager.refreshCharts();

            } else {
                System.out.println("Book not found");
            }

        } catch (Exception e) {
            System.out.println("Delete Error: " + e.getMessage());
        }
    }
        // ================= ADVANCED SEARCH =================
    public List<Book> advancedSearch(String keyword) {

        List<Book> list = new ArrayList<>();

        String sql = "SELECT * FROM books WHERE title LIKE ? OR author LIKE ? OR category LIKE ?";

        try (Connection conn = DBConnection.connect();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            String search = "%" + keyword + "%";

            ps.setString(1, search);
            ps.setString(2, search);
            ps.setString(3, search);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new Book(
            rs.getInt("id"),
            rs.getString("title"),
            rs.getString("author"),
            rs.getString("category"),
            rs.getInt("available") == 1
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // ================= UPDATE BOOK =================
    public void updateBook(int id, String newTitle, String newCategory) {

        String sql = "UPDATE books SET title = ?, category = ? WHERE id = ?";

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newTitle);
            stmt.setString(2, newCategory);
            stmt.setInt(3, id);

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Book updated successfully");

                // 🔥 REFRESH CHART
                RefreshManager.refreshCharts();

            } else {
                System.out.println("Book not found");
            }

        } catch (Exception e) {
            System.out.println("Update Error: " + e.getMessage());
        }
    }

    // ================= SEARCH BOOKS =================
    public void searchBook(String keyword) {

        String sql = "SELECT * FROM books WHERE title LIKE ? OR author LIKE ?";

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + keyword + "%");
            stmt.setString(2, "%" + keyword + "%");

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                System.out.println(
                    rs.getInt("id") + " | " +
                    rs.getString("title") + " | " +
                    rs.getString("author") + " | " +
                    rs.getString("category")
                );
            }

        } catch (Exception e) {
            System.out.println("Search Error: " + e.getMessage());
        }
    }

    // ================= CHECK AVAILABILITY =================
    public boolean isBookAvailable(int id) {

        String sql = "SELECT available FROM books WHERE id = ?";

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("available") == 1;
            }

        } catch (Exception e) {
            System.out.println("Availability Error: " + e.getMessage());
        }

        return false;
    }

    // ================= UPDATE AVAILABILITY =================
    public void updateAvailability(int id, int status) {

        String sql = "UPDATE books SET available = ? WHERE id = ?";

        try (Connection conn = DBConnection.connect();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, status);
            stmt.setInt(2, id);

            stmt.executeUpdate();

            // REFRESH CHART (borrow/return trigger)
            RefreshManager.refreshCharts();

        } catch (Exception e) {
            System.out.println("Availability Update Error: " + e.getMessage());
        }
    }



// ================= FILTER BORROWS BY DATE =================
public List<Borrow> filterByDate(String start, String end) {

    List<Borrow> list = new ArrayList<>();

    String sql = "SELECT * FROM borrows WHERE borrow_date BETWEEN ? AND ?";

    try (Connection conn = DBConnection.connect();
        PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, start);
        ps.setString(2, end);

        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            list.add(new Borrow(
                    rs.getInt("id"),
                    rs.getInt("book_id"),
                    rs.getInt("member_id"),
                    rs.getString("borrow_date"),
                    rs.getString("due_date"),
                    rs.getString("status")
            ));
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return list;
}
}