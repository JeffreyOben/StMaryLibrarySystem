package ui;

import database.DBConnection;
import services.ExportService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import java.awt.*;
import java.sql.*;

public class BorrowPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;

    public BorrowPanel() {

        setLayout(new BorderLayout());
        UIStyle.stylePanel(this);

        // ================= TABLE =================
        model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{
                "ID", "Book ID", "Member ID", "Borrow Date", "Due Date", "Status"
        });

        table = new JTable(model);
        UIStyle.styleTable(table);

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        table.setForeground(Color.BLACK);
        table.getTableHeader().setForeground(Color.BLACK);

        add(new JScrollPane(table), BorderLayout.CENTER);

        // ================= BUTTON PANEL =================
        JPanel btnPanel = new JPanel();
        UIStyle.stylePanel(btnPanel);

        JButton loadBtn = new JButton("Load");
        JButton borrowBtn = new JButton("Borrow");
        JButton returnBtn = new JButton("Return");
        JButton overdueBtn = new JButton("Overdue");
        JButton deleteBtn = new JButton("Delete");
        JButton exportBtn = new JButton("📄 Export PDF");

        UIStyle.styleButton(loadBtn);
        UIStyle.styleButton(borrowBtn);
        UIStyle.styleButton(returnBtn);
        UIStyle.styleButton(overdueBtn);
        UIStyle.styleDangerButton(deleteBtn);
        UIStyle.styleButton(exportBtn);

        btnPanel.add(loadBtn);
        btnPanel.add(borrowBtn);
        btnPanel.add(returnBtn);
        btnPanel.add(overdueBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(exportBtn);

        add(btnPanel, BorderLayout.SOUTH);

        // ================= ACTIONS =================

        // THREAD-SAFE LOAD
        loadBtn.addActionListener(e -> {

            new SwingWorker<Void, Void>() {

                @Override
                protected Void doInBackground() {
                    loadBorrowRecords();
                    return null;
                }

                @Override
                protected void done() {
                    JOptionPane.showMessageDialog(BorrowPanel.this,
                            "Borrow records loaded!");
                }

            }.execute();
        });

        borrowBtn.addActionListener(e -> openBorrowDialog());

        returnBtn.addActionListener(e -> returnBook());

        // THREAD-SAFE OVERDUE CHECK
        overdueBtn.addActionListener(e -> {

            new SwingWorker<Void, Void>() {

                @Override
                protected Void doInBackground() {
                    checkOverdue();
                    return null;
                }

            }.execute();
        });

        deleteBtn.addActionListener(e -> deleteBorrow());

        exportBtn.addActionListener(e -> {
            ExportService.exportBorrowsPDF();
            JOptionPane.showMessageDialog(this, "Borrow PDF Exported!");
        });

        // THREAD-SAFE INITIAL LOAD
        new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() {
                loadBorrowRecords();
                return null;
            }

        }.execute();
    }

    // ================= LOAD =================
    private void loadBorrowRecords() {

        model.setRowCount(0);

        String sql = "SELECT * FROM borrows";

        try (Connection conn = DBConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getInt("book_id"),
                        rs.getInt("member_id"),
                        rs.getString("borrow_date"),
                        rs.getString("due_date"),
                        rs.getString("status")
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    // ================= BORROW =================
    private void openBorrowDialog() {

        JTextField bookIdField = new JTextField();
        JTextField memberIdField = new JTextField();
        JTextField borrowDateField = new JTextField();
        JTextField dueDateField = new JTextField();

        Object[] fields = {
                "Book ID:", bookIdField,
                "Member ID:", memberIdField,
                "Borrow Date (YYYY-MM-DD):", borrowDateField,
                "Due Date (YYYY-MM-DD):", dueDateField
        };

        int result = JOptionPane.showConfirmDialog(
                this, fields, "Borrow Book", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {

            String sql = "INSERT INTO borrows(book_id, member_id, borrow_date, due_date, status) VALUES (?, ?, ?, ?, 'Borrowed')";

            try (Connection conn = DBConnection.connect();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                int bookId = Integer.parseInt(bookIdField.getText());

                ps.setInt(1, bookId);
                ps.setInt(2, Integer.parseInt(memberIdField.getText()));
                ps.setString(3, borrowDateField.getText());
                ps.setString(4, dueDateField.getText());

                ps.executeUpdate();

                try (PreparedStatement ps2 = conn.prepareStatement(
                        "UPDATE books SET available = 0 WHERE id = ?")) {

                    ps2.setInt(1, bookId);
                    ps2.executeUpdate();
                }

                loadBorrowRecords();

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }
    }

    // ================= RETURN =================
    private void returnBook() {

        int row = table.getSelectedRow();
        if (row == -1) return;

        int borrowId = (int) model.getValueAt(row, 0);
        int bookId = (int) model.getValueAt(row, 1);

        try (Connection conn = DBConnection.connect()) {

            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE borrows SET status = 'Returned' WHERE id = ?");
            ps.setInt(1, borrowId);
            ps.executeUpdate();

            PreparedStatement ps2 = conn.prepareStatement(
                    "UPDATE books SET available = 1 WHERE id = ?");
            ps2.setInt(1, bookId);
            ps2.executeUpdate();

            loadBorrowRecords();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    // ================= DELETE =================
    private void deleteBorrow() {

        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a record first!");
            return;
        }

        int id = (int) model.getValueAt(row, 0);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete borrow record ID " + id + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        String sql = "DELETE FROM borrows WHERE id=?";

        try (Connection conn = DBConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
            loadBorrowRecords();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    // ================= OVERDUE =================
    private void checkOverdue() {

        String sql = "SELECT * FROM borrows WHERE status='Borrowed' AND due_date < DATE('now')";

        try (Connection conn = DBConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            StringBuilder result = new StringBuilder();

            while (rs.next()) {
                result.append("Borrow ID: ")
                        .append(rs.getInt("id"))
                        .append(" | Due: ")
                        .append(rs.getString("due_date"))
                        .append("\n");
            }

            JOptionPane.showMessageDialog(this,
                    result.length() == 0 ? "No overdue books" : result.toString());

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }
}