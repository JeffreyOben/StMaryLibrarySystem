package ui;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import database.DBConnection;

public class ReturnPanel extends JPanel {

    public ReturnPanel() {

        setLayout(new GridLayout(5, 2, 10, 10));

        JLabel title = new JLabel("Return Book", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));

        JTextField borrowIdField = new JTextField();

        JButton returnBtn = new JButton("Return Book");

        add(new JLabel(""));
        add(title);

        add(new JLabel("Borrow ID:"));
        add(borrowIdField);

        add(new JLabel(""));
        add(returnBtn);

        // ================= ACTION (THREAD SAFE) =================
        returnBtn.addActionListener(e -> {

            new SwingWorker<Void, Void>() {

                @Override
                protected Void doInBackground() {

                    try {
                        int borrowId = Integer.parseInt(borrowIdField.getText().trim());

                        String sql = "UPDATE borrows SET status = 'Returned' WHERE id = ?";

                        try (Connection conn = DBConnection.connect();
                             PreparedStatement ps = conn.prepareStatement(sql)) {

                            ps.setInt(1, borrowId);

                            int rows = ps.executeUpdate();

                            if (rows > 0) {
                                JOptionPane.showMessageDialog(ReturnPanel.this,
                                        "Book returned successfully!");
                            } else {
                                JOptionPane.showMessageDialog(ReturnPanel.this,
                                        "Borrow record not found.");
                            }
                        }

                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(ReturnPanel.this,
                                "Borrow ID must be a number.");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(ReturnPanel.this,
                                "Error: " + ex.getMessage());
                    }

                    return null;
                }

            }.execute();
        });
    }
}