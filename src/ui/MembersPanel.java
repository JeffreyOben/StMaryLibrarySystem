package ui;

import database.DBConnection;
import services.ExportService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class MembersPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;

    public MembersPanel() {

        setLayout(new BorderLayout());
        UIStyle.stylePanel(this);

        // ================= TABLE =================
        model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{
                "ID", "Name", "Email", "Type"
        });

        table = new JTable(model);
        UIStyle.styleTable(table);

        table.setForeground(Color.BLACK);
        table.getTableHeader().setForeground(Color.BLACK);

        add(new JScrollPane(table), BorderLayout.CENTER);

        // ================= BUTTON PANEL =================
        JPanel btnPanel = new JPanel();
        UIStyle.stylePanel(btnPanel);

        JButton loadBtn = new JButton("Load");
        JButton addBtn = new JButton("Add");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");
        JButton exportBtn = new JButton("📄 Export PDF");

        UIStyle.styleButton(loadBtn);
        UIStyle.styleButton(addBtn);
        UIStyle.styleButton(updateBtn);
        UIStyle.styleDangerButton(deleteBtn);
        UIStyle.styleButton(exportBtn);

        btnPanel.add(loadBtn);
        btnPanel.add(addBtn);
        btnPanel.add(updateBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(exportBtn);

        add(btnPanel, BorderLayout.SOUTH);

        // ================= ACTIONS =================

        // THREAD-SAFE LOAD BUTTON
        loadBtn.addActionListener(e -> {

            new SwingWorker<Void, Void>() {

                @Override
                protected Void doInBackground() {
                    loadMembers();
                    return null;
                }

                @Override
                protected void done() {
                    JOptionPane.showMessageDialog(MembersPanel.this, "Members loaded!");
                }

            }.execute();
        });

        addBtn.addActionListener(e -> addMember());
        updateBtn.addActionListener(e -> updateMember());
        deleteBtn.addActionListener(e -> deleteMember());

        exportBtn.addActionListener(e -> {
            ExportService.exportMembersPDF();
            JOptionPane.showMessageDialog(this, "Members PDF Exported!");
        });

        // THREAD-SAFE INITIAL LOAD
        new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() {
                loadMembers();
                return null;
            }

            @Override
            protected void done() {
                // no popup on startup
            }

        }.execute();
    }

    // ================= LOAD =================
    private void loadMembers() {

        model.setRowCount(0);

        try (Connection conn = DBConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM members")) {

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("type")
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    // ================= ADD =================
    private void addMember() {

        JTextField id = new JTextField();
        JTextField name = new JTextField();
        JTextField email = new JTextField();
        JTextField type = new JTextField();

        Object[] fields = {
                "ID:", id,
                "Name:", name,
                "Email:", email,
                "Type:", type
        };

        if (JOptionPane.showConfirmDialog(this, fields, "Add Member",
                JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {

            int memberId;
            try {
                memberId = Integer.parseInt(id.getText());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "ID must be a number");
                return;
            }

            String emailText = email.getText();
            if (emailText == null || !emailText.contains("@")) {
                JOptionPane.showMessageDialog(this, "Invalid email!");
                return;
            }

            try (Connection conn = DBConnection.connect();
                 PreparedStatement ps = conn.prepareStatement(
                         "INSERT INTO members VALUES (?, ?, ?, ?)")) {

                ps.setInt(1, memberId);
                ps.setString(2, name.getText());
                ps.setString(3, emailText);
                ps.setString(4, type.getText());

                ps.executeUpdate();
                loadMembers();

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }
    }

    // ================= UPDATE =================
    private void updateMember() {

        int row = table.getSelectedRow();
        if (row == -1) return;

        int id = (int) model.getValueAt(row, 0);

        JTextField name = new JTextField(model.getValueAt(row, 1).toString());
        JTextField email = new JTextField(model.getValueAt(row, 2).toString());
        JTextField type = new JTextField(model.getValueAt(row, 3).toString());

        Object[] fields = {
                "Name:", name,
                "Email:", email,
                "Type:", type
        };

        if (JOptionPane.showConfirmDialog(this, fields, "Update Member",
                JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {

            try (Connection conn = DBConnection.connect();
                 PreparedStatement ps = conn.prepareStatement(
                         "UPDATE members SET name=?, email=?, type=? WHERE id=?")) {

                ps.setString(1, name.getText());
                ps.setString(2, email.getText());
                ps.setString(3, type.getText());
                ps.setInt(4, id);

                ps.executeUpdate();
                loadMembers();

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }
    }

    // ================= DELETE =================
    private void deleteMember() {

        int row = table.getSelectedRow();
        if (row == -1) return;

        int id = (int) model.getValueAt(row, 0);

        try (Connection conn = DBConnection.connect();
             PreparedStatement ps = conn.prepareStatement(
                     "DELETE FROM members WHERE id=?")) {

            ps.setInt(1, id);
            ps.executeUpdate();
            loadMembers();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }
}