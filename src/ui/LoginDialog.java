package ui;

import javax.swing.*;
import java.awt.*;

public class LoginDialog extends JDialog {

    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginDialog(JFrame parent) {
        super(parent, "Library Login", true);

        setSize(420, 300);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(createHeader(), BorderLayout.NORTH);
        add(createForm(), BorderLayout.CENTER);
    }

    // ================= HEADER =================
    private JPanel createHeader() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(25, 30, 40));

        JLabel title = new JLabel("Library Login");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));

        panel.add(title);
        return panel;
    }

    // ================= FORM =================
    private JPanel createForm() {

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        usernameField = new JTextField();
        passwordField = new JPasswordField();

        JButton adminBtn = new JButton("Login as Admin");
        JButton userBtn = new JButton("Login as User");

        styleButton(adminBtn, new Color(52, 152, 219));
        styleButton(userBtn, new Color(46, 204, 113));

        adminBtn.addActionListener(e -> login("Admin"));
        userBtn.addActionListener(e -> login("User"));

        panel.add(new JLabel("Username"));
        panel.add(usernameField);

        panel.add(new JLabel("Password"));
        panel.add(passwordField);

        panel.add(adminBtn);
        panel.add(userBtn);

        return panel;
    }

    // ================= BUTTON STYLE =================
    private void styleButton(JButton btn, Color color) {
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
    }

    // ================= LOGIN LOGIC =================
   private void login(String roleIgnored) {

    String username = usernameField.getText().trim();
    String password = new String(passwordField.getPassword()).trim();

    if (username.isEmpty() || password.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Please fill all fields");
        return;
    }

    new SwingWorker<Void, Void>() {

        String role = null;

        @Override
        protected Void doInBackground() {

            String sql = "SELECT role FROM users WHERE username=? AND password=?";

            try (java.sql.Connection conn = database.DBConnection.connect();
                 java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, username);
                ps.setString(2, password);

                java.sql.ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    role = rs.getString("role");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void done() {

            if (role != null) {

                JOptionPane.showMessageDialog(LoginDialog.this, "Login successful!");

                dispose();

                if (role.equalsIgnoreCase("ADMIN")) {
                    new AdminDashboard().setVisible(true);
                } else {
                    new UserDashboard().setVisible(true);
                }

            } else {
                JOptionPane.showMessageDialog(LoginDialog.this,
                        "Invalid username or password");
            }
        }

    }.execute();
}
}