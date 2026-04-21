package ui;

import javax.swing.*;
import java.awt.*;
import utils.RefreshManager;

public class LibraryDashboard extends JFrame {

    private JPanel content;
    private CardLayout layout;

    public LibraryDashboard(String role) {

        setTitle("Library Dashboard");
        setSize(1100, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ================= SIDEBAR =================
        JPanel sidebar = new JPanel(new GridLayout(8, 1, 8, 8));
        sidebar.setBackground(UIStyle.DARK);
        sidebar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnBooks = new JButton("📚 Books");
        JButton btnMembers = new JButton("👥 Members");
        JButton btnBorrow = new JButton("🔄 Borrow");
        JButton btnCharts = new JButton("📊 Charts");
        JButton btnUsers = new JButton("👤 Users");
        JButton btnLogout = new JButton("🚪 Logout");

        // ================= STYLE =================
        JButton[] buttons = {
                btnBooks, btnMembers, btnBorrow,
                btnCharts, btnUsers, btnLogout
        };

        for (JButton b : buttons) {
            UIStyle.styleSidebarButton(b);
            b.setEnabled(true);
            b.setFocusable(false);
            b.setOpaque(true);
            b.setContentAreaFilled(true);
            b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        // ================= ROLE CONTROL =================
        boolean isAdmin = role != null && role.equalsIgnoreCase("ADMIN");

        if (!isAdmin) {
            btnUsers.setEnabled(false);
        }

        // ================= ADD BUTTONS =================
        sidebar.add(btnBooks);
        sidebar.add(btnMembers);
        sidebar.add(btnBorrow);
        sidebar.add(btnCharts);
        sidebar.add(btnUsers);
        sidebar.add(new JLabel());
        sidebar.add(btnLogout);

        add(sidebar, BorderLayout.WEST);

        // ================= CONTENT =================
        layout = new CardLayout();
        content = new JPanel(layout);
        content.setBackground(UIStyle.LIGHT_BG);

        // Panels
        content.add(new BooksPanel(), "BOOKS");
        content.add(new MembersPanel(), "MEMBERS");
        content.add(new BorrowPanel(), "BORROW");

       
        ChartsPanel chartsPanel = new ChartsPanel();
        content.add(chartsPanel, "CHARTS");
        RefreshManager.registerChartsPanel(chartsPanel);

        content.add(new UsersPanel(), "USERS");

        add(content, BorderLayout.CENTER);

        // ================= NAVIGATION =================
        btnBooks.addActionListener(e -> layout.show(content, "BOOKS"));
        btnMembers.addActionListener(e -> layout.show(content, "MEMBERS"));
        btnBorrow.addActionListener(e -> layout.show(content, "BORROW"));
        btnCharts.addActionListener(e -> layout.show(content, "CHARTS"));
        btnUsers.addActionListener(e -> layout.show(content, "USERS"));

        // ================= LOGOUT =================
        btnLogout.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        // DEFAULT VIEW
        layout.show(content, "BOOKS");

        setVisible(true);
    }
}