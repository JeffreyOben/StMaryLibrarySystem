package services;

import database.DBConnection;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.FileOutputStream;
import java.sql.*;

public class ExportService {

    // ================= COMMON STYLING =================
    private static Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
    private static Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
    private static Font bodyFont = new Font(Font.FontFamily.HELVETICA, 11);

    // ================= BOOKS PDF =================
    public static void exportBooksPDF() {

        Document doc = new Document();

        try {
            PdfWriter.getInstance(doc, new FileOutputStream("Books_Report.pdf"));
            doc.open();

            doc.add(new Paragraph("📚 St Mary's Library - Books Report", titleFont));
            doc.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);

            addHeader(table, "ID", "Title", "Author", "Category", "Status");

            String sql = "SELECT * FROM books";

            try (Connection conn = DBConnection.connect();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                while (rs.next()) {
                    table.addCell(cell(rs.getInt("id") + ""));
                    table.addCell(cell(rs.getString("title")));
                    table.addCell(cell(rs.getString("author")));
                    table.addCell(cell(rs.getString("category")));

                    String status = rs.getInt("available") == 1 ? "AVAILABLE" : "BORROWED";
                    table.addCell(cell(status));
                }
            }

            doc.add(table);
            doc.close();

            System.out.println("Books PDF generated!");

        } catch (Exception e) {
            System.out.println("PDF Error: " + e.getMessage());
        }
    }

    // ================= MEMBERS PDF =================
    public static void exportMembersPDF() {

        Document doc = new Document();

        try {
            PdfWriter.getInstance(doc, new FileOutputStream("Members_Report.pdf"));
            doc.open();

            doc.add(new Paragraph("👤 Members Report", titleFont));
            doc.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);

            addHeader(table, "ID", "Name", "Email", "Type");

            String sql = "SELECT * FROM members";

            try (Connection conn = DBConnection.connect();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                while (rs.next()) {
                    table.addCell(cell(rs.getInt("id") + ""));
                    table.addCell(cell(rs.getString("name")));
                    table.addCell(cell(rs.getString("email")));
                    table.addCell(cell(rs.getString("type")));
                }
            }

            doc.add(table);
            doc.close();

            System.out.println("Members PDF generated!");

        } catch (Exception e) {
            System.out.println("PDF Error: " + e.getMessage());
        }
    }

    // ================= BORROWS PDF =================
    public static void exportBorrowsPDF() {

        Document doc = new Document();

        try {
            PdfWriter.getInstance(doc, new FileOutputStream("Borrows_Report.pdf"));
            doc.open();

            doc.add(new Paragraph("📖 Borrow Records Report", titleFont));
            doc.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);

            addHeader(table, "ID", "Book ID", "Member ID", "Borrow Date", "Due Date", "Status");

            String sql = "SELECT * FROM borrows";

            try (Connection conn = DBConnection.connect();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                while (rs.next()) {
                    table.addCell(cell(rs.getInt("id") + ""));
                    table.addCell(cell(rs.getInt("book_id") + ""));
                    table.addCell(cell(rs.getInt("member_id") + ""));
                    table.addCell(cell(rs.getString("borrow_date")));
                    table.addCell(cell(rs.getString("due_date")));
                    table.addCell(cell(rs.getString("status")));
                }
            }

            doc.add(table);
            doc.close();

            System.out.println("Borrows PDF generated!");

        } catch (Exception e) {
            System.out.println("PDF Error: " + e.getMessage());
        }
    }

    // ================= HELPER METHODS =================
    private static void addHeader(PdfPTable table, String... headers) {
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(cell);
        }
    }

    private static PdfPCell cell(String text) {
        return new PdfPCell(new Phrase(text, bodyFont));
    }
}