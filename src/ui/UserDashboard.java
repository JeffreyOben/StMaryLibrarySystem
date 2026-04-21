package ui;

import javax.swing.*;

public class UserDashboard extends JFrame {

    public UserDashboard() {
        setTitle("User Dashboard");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        add(new JLabel("USER DASHBOARD", SwingConstants.CENTER));
    }
}