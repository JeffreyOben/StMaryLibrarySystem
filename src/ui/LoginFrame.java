package ui;

import database.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.UUID;

public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JCheckBox rememberMe;

    public LoginFrame() {

        setTitle("Library Login");
        setSize(400, 320);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ================= TITLE =================
        JLabel title = new JLabel("Library Login", SwingConstants.CENTER);
        title.setFont(UIStyle.TITLE);
        title.setForeground(Color.BLACK);
        title.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));
        add(title, BorderLayout.NORTH);

        // ================= MAIN PANEL =================
        JPanel panel = new JPanel(new GridLayout(5, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        UIStyle.stylePanel(panel);

        usernameField = new JTextField();
        passwordField = new JPasswordField();

        rememberMe = new JCheckBox("Remember Me");
        rememberMe.setBackground(UIStyle.LIGHT_BG);

        panel.add(new JLabel("Username:"));
        panel.add(usernameField);

        panel.add(new JLabel("Password:"));
        panel.add(passwordField);

        panel.add(rememberMe);

        add(panel, BorderLayout.CENTER);

        // ================= BUTTON =================
        JButton loginBtn = new JButton("Login");
        UIStyle.styleButton(loginBtn);

        loginBtn.setPreferredSize(new Dimension(200, 45));

        JPanel btnPanel = new JPanel(new GridBagLayout());
        btnPanel.setBackground(UIStyle.LIGHT_BG);
        btnPanel.add(loginBtn);

        add(btnPanel, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(loginBtn);

        loginBtn.addActionListener(e -> login());

        revalidate();
        repaint();
    }

    // ================= LOGIN =================
    private void login() {

        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter username and password");
            return;
        }

        new SwingWorker<Object[], Void>() {

            @Override
            protected Object[] doInBackground() {

                String sql = "SELECT id, role FROM users WHERE username=? AND password=?";

                try (Connection conn = DBConnection.connect();
                     PreparedStatement ps = conn.prepareStatement(sql)) {

                    ps.setString(1, username);
                    ps.setString(2, password);

                    ResultSet rs = ps.executeQuery();

                    if (rs.next()) {
                        return new Object[]{
                                rs.getInt("id"),
                                rs.getString("role")
                        };
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void done() {

                try {
                    Object[] result = get();

                    if (result == null) {
                        JOptionPane.showMessageDialog(LoginFrame.this,
                                "Invalid username or password");
                        return;
                    }

                    int userId = (int) result[0];
                    String role = (String) result[1];

                    if (rememberMe.isSelected()) {
                        saveSession(userId);
                    }

                    JOptionPane.showMessageDialog(LoginFrame.this,
                            "Login successful!");

                    dispose();
                    new LibraryDashboard(role).setVisible(true);

                } catch (Exception e) {
                    JOptionPane.showMessageDialog(LoginFrame.this,
                            "Login error: " + e.getMessage());
                }
            }

        }.execute();
    }

    // ================= SAVE SESSION =================
    private void saveSession(int userId) {

        try (Connection conn = DBConnection.connect()) {

            // optional: clear old sessions (single-login system)
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM sessions")) {
                ps.executeUpdate();
            }

            String sql = "INSERT INTO sessions(user_id, token, created_at) VALUES(?, ?, datetime('now'))";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, userId);
                ps.setString(2, UUID.randomUUID().toString());
                ps.executeUpdate();
            }

        } catch (Exception e) {
            System.out.println("Session save error: " + e.getMessage());
        }
    }
}