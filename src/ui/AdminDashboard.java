package ui;

import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JFrame {

    private JPanel menuPanel;
    private JPanel contentPanel;

    public AdminDashboard() {

        setTitle("Admin Dashboard");
        setSize(1000, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ================= MENU PANEL =================
        menuPanel = new JPanel();
        menuPanel.setLayout(new GridLayout(4, 1, 10, 10));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        menuPanel.setBackground(new Color(30, 30, 30));

        JButton dashboardBtn = new JButton("Dashboard");
        JButton usersBtn = new JButton("Users");
        JButton booksBtn = new JButton("Books");
        JButton logoutBtn = new JButton("Logout");

        styleButton(dashboardBtn);
        styleButton(usersBtn);
        styleButton(booksBtn);
        styleDangerButton(logoutBtn);

        menuPanel.add(dashboardBtn);
        menuPanel.add(usersBtn);
        menuPanel.add(booksBtn);
        menuPanel.add(logoutBtn);

        // ================= CONTENT PANEL =================
        contentPanel = new JPanel(new BorderLayout());

        // default view
        showDashboard();

        // ================= ACTIONS =================
        dashboardBtn.addActionListener(e -> showDashboard());
        usersBtn.addActionListener(e -> showUsers());
        booksBtn.addActionListener(e -> showBooks());

        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        add(menuPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
    }

    // ================= VIEWS =================

    private void showDashboard() {

        JPanel dashboard = new JPanel(new BorderLayout());
        dashboard.setBackground(Color.WHITE);

        JLabel label = new JLabel("ADMIN DASHBOARD", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 26));

        dashboard.add(label, BorderLayout.CENTER);

        switchPanel(dashboard);
    }

    private void showUsers() {
        switchPanel(new UsersPanel());
    }

    private void showBooks() {
        switchPanel(new BooksPanel());
    }

    // ================= PANEL SWITCHER =================

    private void switchPanel(JPanel panel) {
        contentPanel.removeAll();
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // ================= BUTTON STYLE =================

    private void styleButton(JButton btn) {
        btn.setFocusPainted(false);
        btn.setBackground(new Color(52, 152, 219));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void styleDangerButton(JButton btn) {
        btn.setFocusPainted(false);
        btn.setBackground(new Color(231, 76, 60));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}