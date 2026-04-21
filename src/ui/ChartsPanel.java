package ui;

import database.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class ChartsPanel extends JPanel {

    private JLabel totalBooks = new JLabel();
    private JLabel borrowedBooks = new JLabel();
    private JLabel totalMembers = new JLabel();

    public ChartsPanel() {

        setLayout(new GridLayout(3, 1, 20, 20));
        UIStyle.stylePanel(this);

        totalBooks.setFont(UIStyle.TITLE);
        borrowedBooks.setFont(UIStyle.TITLE);
        totalMembers.setFont(UIStyle.TITLE);

        add(createCard("📚 Total Books", totalBooks));
        add(createCard("🔄 Borrowed Books", borrowedBooks));
        add(createCard("👥 Total Members", totalMembers));

        // ================= THREAD-SAFE INITIAL LOAD =================
        new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() {
                loadStats();
                return null;
            }

        }.execute();
    }

    private JPanel createCard(String title, JLabel value) {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UIStyle.TEXT);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(value, BorderLayout.CENTER);

        return panel;
    }

    // ===================== REFRESH METHOD =====================
    public void refresh() {

    SwingWorker<Void, Void> worker = new SwingWorker<>() {
        @Override
        protected Void doInBackground() {
            loadStats();
            return null;
        }

        @Override
        protected void done() {
            revalidate();
            repaint();
        }
    };

    worker.execute();
}

    // ===================== LOAD STATS =====================
    public void loadStats() {

        try (Connection conn = DBConnection.connect()) {

            // TOTAL BOOKS
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM books")) {

                if (rs.next()) {
                    totalBooks.setText(String.valueOf(rs.getInt(1)));
                }
            }

            // BORROWED BOOKS
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery(
                         "SELECT COUNT(*) FROM borrows WHERE status='Borrowed'")) {

                if (rs.next()) {
                    borrowedBooks.setText(String.valueOf(rs.getInt(1)));
                }
            }

            // MEMBERS
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM members")) {

                if (rs.next()) {
                    totalMembers.setText(String.valueOf(rs.getInt(1)));
                }
            }

        } catch (Exception e) {
            SwingUtilities.invokeLater(() ->
                    JOptionPane.showMessageDialog(this,
                            "Chart Load Error: " + e.getMessage())
            );
        }

        SwingUtilities.invokeLater(() -> {
            revalidate();
            repaint();
        });
    }
}