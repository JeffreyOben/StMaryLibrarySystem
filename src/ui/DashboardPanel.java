package ui;

import database.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

public class DashboardPanel extends JPanel {

    public DashboardPanel() {

        setLayout(new GridLayout(1, 2, 20, 20));

        UIStyle.stylePanel(this);

        // ================= PLACEHOLDER PANELS =================
        JPanel booksHolder = new JPanel(new BorderLayout());
        JPanel borrowHolder = new JPanel(new BorderLayout());

        add(booksHolder);
        add(borrowHolder);

        // ================= THREAD-SAFE LOADING =================
        new SwingWorker<JPanel[], Void>() {

            @Override
            protected JPanel[] doInBackground() {
                return new JPanel[]{
                        createBooksChart(),
                        createBorrowChart()
                };
            }

            @Override
            protected void done() {
                try {
                    JPanel[] panels = get();

                    booksHolder.removeAll();
                    booksHolder.add(panels[0]);

                    borrowHolder.removeAll();
                    borrowHolder.add(panels[1]);

                    booksHolder.revalidate();
                    booksHolder.repaint();

                    borrowHolder.revalidate();
                    borrowHolder.repaint();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }.execute();
    }

    // ================= BOOKS STATUS CHART =================
    private JPanel createBooksChart() {

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        try (Connection conn = DBConnection.connect();
             Statement stmt = conn.createStatement()) {

            ResultSet rs1 = stmt.executeQuery("SELECT COUNT(*) FROM books WHERE available = 1");
            int available = rs1.next() ? rs1.getInt(1) : 0;

            ResultSet rs2 = stmt.executeQuery("SELECT COUNT(*) FROM books WHERE available = 0");
            int borrowed = rs2.next() ? rs2.getInt(1) : 0;

            dataset.addValue(available, "Books", "Available");
            dataset.addValue(borrowed, "Books", "Borrowed");

        } catch (Exception e) {
            e.printStackTrace();
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Books Overview",
                "Category",
                "Count",
                dataset
        );

        return new ChartPanel(chart);
    }

    // ================= BORROW ACTIVITY CHART =================
    private JPanel createBorrowChart() {

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        try (Connection conn = DBConnection.connect();
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery(
                    "SELECT substr(borrow_date,1,7) AS month, COUNT(*) as total " +
                    "FROM borrows GROUP BY month ORDER BY month"
            );

            while (rs.next()) {
                dataset.addValue(
                        rs.getInt("total"),
                        "Borrows",
                        rs.getString("month")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        JFreeChart chart = ChartFactory.createLineChart(
                "Borrow Trends",
                "Month",
                "Borrows",
                dataset
        );

        return new ChartPanel(chart);
    }
}