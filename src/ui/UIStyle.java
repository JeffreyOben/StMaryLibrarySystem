package ui;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class UIStyle {

    // ================= COLORS =================
    public static final Color PRIMARY = new Color(33, 150, 243);
    public static final Color PRIMARY_DARK = new Color(25, 118, 210);
    public static final Color DARK = new Color(30, 30, 30);
    public static final Color LIGHT_BG = new Color(245, 245, 245);
    public static final Color SUCCESS = new Color(76, 175, 80);
    public static final Color DANGER = new Color(244, 67, 54);

    // ================= FONTS =================
    public static final Font TITLE = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font TEXT = new Font("Segoe UI", Font.PLAIN, 13);

    // ================= BUTTON STYLE =================
    public static void styleButton(JButton btn) {
        btn.setBackground(PRIMARY);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(TEXT);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);

        // SAFE hover (no listener removal)
        btn.getModel().addChangeListener(e -> {
            ButtonModel model = btn.getModel();
            if (model.isRollover()) {
                btn.setBackground(PRIMARY_DARK);
            } else {
                btn.setBackground(PRIMARY);
            }
        });
    }

    // ================= DANGER BUTTON =================
    public static void styleDangerButton(JButton btn) {
        btn.setBackground(DANGER);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(TEXT);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
    }

    // ================= TABLE STYLE =================
    public static void styleTable(JTable table) {
        table.setFont(TEXT);
        table.setRowHeight(28);
        table.setSelectionBackground(PRIMARY);
        table.setSelectionForeground(Color.WHITE);

        JTableHeader header = table.getTableHeader();
        if (header != null) {
            header.setFont(TEXT);
            header.setBackground(PRIMARY);
            header.setForeground(Color.WHITE);
            header.setOpaque(true);
        }
    }

    // ================= PANEL =================
    public static void stylePanel(JPanel panel) {
        panel.setBackground(LIGHT_BG);
    }

    // ================= SIDEBAR =================
    public static void styleSidebar(JPanel panel) {
        panel.setBackground(DARK);
    }

    public static void styleSidebarButton(JButton btn) {
        btn.setBackground(DARK);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(TEXT);
        btn.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);

        // SAFE hover using model (NO listener removal)
        btn.getModel().addChangeListener(e -> {
            ButtonModel model = btn.getModel();
            if (model.isRollover()) {
                btn.setBackground(PRIMARY);
            } else {
                btn.setBackground(DARK);
            }
        });
    }
}