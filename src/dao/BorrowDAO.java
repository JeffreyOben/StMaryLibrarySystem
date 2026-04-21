package dao;

import database.DBConnection;
import models.Borrow;
import utils.RefreshManager;

import java.sql.*;

public class BorrowDAO {

    // ==============================
    // BORROW BOOK
    // ==============================
    public void borrowBook(Borrow borrow) {

        String sql = "INSERT INTO borrows(book_id, member_id, borrow_date, due_date, status) VALUES(?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, borrow.getBookId());
            stmt.setInt(2, borrow.getMemberId());
            stmt.setString(3, borrow.getBorrowDate());
            stmt.setString(4, borrow.getDueDate());
            stmt.setString(5, borrow.getStatus());

            stmt.executeUpdate();
            System.out.println("Book borrowed successfully");

            // REFRESH CHART
            RefreshManager.refreshCharts();

        } catch (Exception e) {
            System.out.println("Borrow Error: " + e.getMessage());
        }
    }

    // ==============================
    // VIEW BORROW RECORDS
    // ==============================
    public void viewBorrowRecords() {

        String sql = "SELECT * FROM borrows";

        try (Connection conn = DBConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                System.out.println(
                        rs.getInt("id") + " | Book: " + rs.getInt("book_id") +
                        " | Member: " + rs.getInt("member_id") +
                        " | Borrowed: " + rs.getString("borrow_date") +
                        " | Due: " + rs.getString("due_date") +
                        " | Status: " + rs.getString("status")
                );
            }

        } catch (Exception e) {
            System.out.println("View Error: " + e.getMessage());
        }
    }

    // ==============================
    // RETURN BOOK
    // ==============================
    public void returnBook(int borrowId) {

        String sql = "UPDATE borrows SET status = 'RETURNED' WHERE id = ?";

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, borrowId);
            int rows = stmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Book returned successfully");

                // get book id
                String getBook = "SELECT book_id FROM borrows WHERE id = ?";
                PreparedStatement ps = conn.prepareStatement(getBook);
                ps.setInt(1, borrowId);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    int bookId = rs.getInt("book_id");

                    // mark book available again
                    String updateBook = "UPDATE books SET available = 1 WHERE id = ?";
                    PreparedStatement ps2 = conn.prepareStatement(updateBook);
                    ps2.setInt(1, bookId);
                    ps2.executeUpdate();
                }

                // REFRESH CHART
                RefreshManager.refreshCharts();

            } else {
                System.out.println("Record not found");
            }

        } catch (Exception e) {
            System.out.println("Return Error: " + e.getMessage());
        }
    }

    // ==============================
    // OVERDUE CHECK
    // ==============================
    public void checkOverdue() {

        String sql = "SELECT * FROM borrows WHERE status = 'BORROWED'";

        try (Connection conn = DBConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("=== OVERDUE BOOKS ===");

            boolean found = false;
            String today = java.time.LocalDate.now().toString();

            while (rs.next()) {

                String dueDate = rs.getString("due_date");

                if (dueDate.compareTo(today) < 0) {

                    found = true;

                    System.out.println(
                            "Borrow ID: " + rs.getInt("id") +
                            " | Book ID: " + rs.getInt("book_id") +
                            " | Member ID: " + rs.getInt("member_id") +
                            " | Due Date: " + dueDate
                    );
                }
            }

            if (!found) {
                System.out.println("No overdue books found.");
            }

        } catch (Exception e) {
            System.out.println("Overdue check error: " + e.getMessage());
        }
    }
}