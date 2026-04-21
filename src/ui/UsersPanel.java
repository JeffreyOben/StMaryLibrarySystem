package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import database.DBConnection;
import services.UserService;

public class UsersPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;

    public UsersPanel() {

        setLayout(new BorderLayout());

        // ================= TABLE =================
        model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"ID", "Username"});

        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // ================= BUTTON PANEL =================
        JPanel panel = new JPanel();

        JButton loadBtn = new JButton("Load Users");
        JButton addBtn = new JButton("Add User");
        JButton deleteBtn = new JButton("Delete User");

        panel.add(loadBtn);
        panel.add(addBtn);
        panel.add(deleteBtn);

        add(panel, BorderLayout.SOUTH);

        // ================= ACTIONS =================

        loadBtn.addActionListener(e -> loadUsers());

        addBtn.addActionListener(e -> addUser());

        deleteBtn.addActionListener(e -> deleteUser());

        // auto load
        loadUsers();
    }

    // ================= LOAD USERS =================
    private void loadUsers() {

        model.setRowCount(0);

        String sql = "SELECT id, username FROM users";

        try (Connection conn = DBConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("username")
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    // ================= ADD USER =================
    private void addUser() {

        JTextField usernameField = new JTextField();
        JTextField passwordField = new JTextField();

        Object[] fields = {
                "Username:", usernameField,
                "Password:", passwordField
        };

        int option = JOptionPane.showConfirmDialog(
                this, fields, "Add User", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {

            String username = usernameField.getText();
            String password = passwordField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Fields cannot be empty!");
                return;
            }

            UserService service = new UserService();
            boolean success = service.registerUser(username, password, "LIBRARIAN");

            if (success) {
                JOptionPane.showMessageDialog(this, "User added!");
                loadUsers();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add user!");
            }
        }
    }

    // ================= DELETE USER =================
    private void deleteUser() {

        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a user first!");
            return;
        }

        int id = (int) model.getValueAt(row, 0);

        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection conn = DBConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "User deleted!");
            loadUsers();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }
}