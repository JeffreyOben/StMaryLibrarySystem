package ui;

import database.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class OverduePanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;

    public OverduePanel() {

        setLayout(new BorderLayout());
        UIStyle.stylePanel(this);

        // ================= TABLE =================
        model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{
                "Borrow ID", "Book ID", "Member ID", "Due Date", "Status"
        });

        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // ================= BUTTON =================
        JButton loadBtn = new JButton("Load Overdue");
        add(loadBtn, BorderLayout.SOUTH);

        // ================= ACTION (THREAD SAFE) =================
        loadBtn.addActionListener(e -> {

            new SwingWorker<Void, Void>() {

                @Override
                protected Void doInBackground() {
                    loadOverdue();
                    return null;
                }

                @Override
                protected void done() {
                    JOptionPane.showMessageDialog(OverduePanel.this,
                            "Overdue records loaded!");
                }

            }.execute();
        });

        // auto load
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                loadOverdue();
                return null;
            }
        }.execute();
    }

    // ================= LOAD OVERDUE =================
    private void loadOverdue() {

        model.setRowCount(0);

        String sql = "SELECT * FROM borrows WHERE status='Borrowed' AND due_date < DATE('now')";

        try (Connection conn = DBConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getInt("book_id"),
                        rs.getInt("member_id"),
                        rs.getString("due_date"),
                        rs.getString("status")
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }
}