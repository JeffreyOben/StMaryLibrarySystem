package ui;

import database.DBConnection;
import services.ExportService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.sql.*;

public class BooksPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private JTextField searchField;
    private JLabel loading;

    public BooksPanel() {

        setLayout(new BorderLayout());

        UIStyle.stylePanel(this);
        loading = new JLabel("Loading...");
        add(loading, BorderLayout.NORTH);

        // ================= TABLE =================
        model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{
                "ID", "Title", "Author", "Category", "Status"
        });

        table = new JTable(model);
        UIStyle.styleTable(table);

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        table.setForeground(Color.BLACK);
        table.getTableHeader().setForeground(Color.BLACK);

        // ================= STATUS COLOR =================
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                try {
                    String status = table.getValueAt(row, 4).toString();

                    if (!isSelected) {
                        if ("BORROWED".equals(status)) {
                            c.setBackground(new Color(255, 220, 220));
                        } else {
                            c.setBackground(new Color(220, 255, 220));
                        }
                    }
                } catch (Exception ignored) {}

                return c;
            }
        });

        add(new JScrollPane(table), BorderLayout.CENTER);

        // ================= TOP PANEL =================
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        UIStyle.stylePanel(topPanel);

        searchField = new JTextField(20);

        JButton loadBtn = new JButton("Load");
        JButton addBtn = new JButton("Add Book");
        JButton deleteBtn = new JButton("Delete");
        JButton updateBtn = new JButton("Update Book");
        JButton exportBtn = new JButton("Export PDF");

        UIStyle.styleButton(loadBtn);
        UIStyle.styleButton(addBtn);
        UIStyle.styleDangerButton(deleteBtn);
        UIStyle.styleButton(updateBtn);
        UIStyle.styleButton(exportBtn);

        topPanel.add(new JLabel("Search: "));
        topPanel.add(searchField);
        topPanel.add(loadBtn);
        topPanel.add(addBtn);
        topPanel.add(deleteBtn);
        topPanel.add(updateBtn);
        topPanel.add(exportBtn);

        add(topPanel, BorderLayout.NORTH);

        // ================= ACTIONS =================

        loadBtn.addActionListener(e -> loadBooksAsync());
        addBtn.addActionListener(e -> openAddBookDialog());
        deleteBtn.addActionListener(e -> deleteBook());
        updateBtn.addActionListener(e -> updateBook());

        exportBtn.addActionListener(e -> {
            ExportService.exportBooksPDF();
            JOptionPane.showMessageDialog(this, "Books PDF Exported!");
        });

        // SEARCH
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { searchBooks(); }
            public void removeUpdate(DocumentEvent e) { searchBooks(); }
            public void changedUpdate(DocumentEvent e) { searchBooks(); }
        });

        // INITIAL LOAD
        loadBooksAsync();
    }

    // ================= ASYNC LOADER =================
    private void loadBooksAsync() {

        new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() {
                loading.setVisible(true);
                loadBooks();
                return null;
            }

            @Override
            protected void done() {
                loading.setVisible(false);
                System.out.println("Books loaded in background ✅");
            }

        }.execute();
    }

    // ================= LOAD =================
    private void loadBooks() {

        model.setRowCount(0);

        String sql = "SELECT * FROM books";

        try (Connection conn = DBConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {

                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("category"),
                        rs.getInt("available") == 1 ? "AVAILABLE" : "BORROWED"
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Load Error: " + e.getMessage());
        }
    }

    // ================= SEARCH =================
    private void searchBooks() {

        String keyword = searchField.getText();
        model.setRowCount(0);

        String sql = "SELECT * FROM books WHERE title LIKE ? OR author LIKE ?";

        try (Connection conn = DBConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("category"),
                        rs.getInt("available") == 1 ? "AVAILABLE" : "BORROWED"
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Search Error: " + e.getMessage());
        }
    }

    // ================= ADD =================
    private void openAddBookDialog() {

        JTextField id = new JTextField();
        JTextField title = new JTextField();
        JTextField author = new JTextField();
        JTextField category = new JTextField();

        Object[] fields = {
                "ID:", id,
                "Title:", title,
                "Author:", author,
                "Category:", category
        };

        int result = JOptionPane.showConfirmDialog(this, fields, "Add Book", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {

            String sql = "INSERT INTO books(id, title, author, category, available) VALUES (?, ?, ?, ?, 1)";

            try (Connection conn = DBConnection.connect();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, Integer.parseInt(id.getText()));
                ps.setString(2, title.getText());
                ps.setString(3, author.getText());
                ps.setString(4, category.getText());

                ps.executeUpdate();

                JOptionPane.showMessageDialog(this, "Book added!");
                loadBooksAsync();

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Add Error: " + e.getMessage());
            }
        }
    }

    // ================= UPDATE =================
    private void updateBook() {

        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a book first!");
            return;
        }

        int id = (int) model.getValueAt(row, 0);

        JTextField title = new JTextField(model.getValueAt(row, 1).toString());

        Object[] fields = {
                "Title:", title
        };

        int result = JOptionPane.showConfirmDialog(
                this, fields, "Update Book", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {

            String sql = "UPDATE books SET title=? WHERE id=?";

            try (Connection conn = DBConnection.connect();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, title.getText());
                ps.setInt(2, id);
                ps.executeUpdate();

                loadBooksAsync();

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Update Error: " + e.getMessage());
            }
        }
    }

    // ================= DELETE =================
    private void deleteBook() {

        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a book first!");
            return;
        }

        int id = Integer.parseInt(model.getValueAt(row, 0).toString());

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete book ID " + id + "?",
                "Confirm",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {

            String sql = "DELETE FROM books WHERE id = ?";

            try (Connection conn = DBConnection.connect();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, id);
                ps.executeUpdate();

                loadBooksAsync();

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Delete Error: " + e.getMessage());
            }
        }
    }
}