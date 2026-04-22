package database;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

public class DatabaseInitializer {

    public static void initializeDatabase() {
        createTables();
        addRoleColumnIfMissing();
        insertDefaultData();
    }

    // ================= CREATE TABLES =================
    private static void createTables() {

        String[] sqlStatements = {

                // BOOKS
                "CREATE TABLE IF NOT EXISTS books (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "title TEXT NOT NULL," +
                        "author TEXT NOT NULL," +
                        "category TEXT NOT NULL," +
                        "available INTEGER DEFAULT 1" +
                        ")",

                // MEMBERS
                "CREATE TABLE IF NOT EXISTS members (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "name TEXT NOT NULL," +
                        "email TEXT NOT NULL UNIQUE," +
                        "type TEXT NOT NULL" +
                        ")",

                // BORROWS
                "CREATE TABLE IF NOT EXISTS borrows (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "book_id INTEGER NOT NULL," +
                        "member_id INTEGER NOT NULL," +
                        "borrow_date TEXT NOT NULL," +
                        "due_date TEXT NOT NULL," +
                        "return_date TEXT," +
                        "status TEXT DEFAULT 'BORROWED'," +
                        "FOREIGN KEY (book_id) REFERENCES books(id)," +
                        "FOREIGN KEY (member_id) REFERENCES members(id)" +
                        ")",

                // USERS
                "CREATE TABLE IF NOT EXISTS users (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "username TEXT NOT NULL UNIQUE," +
                        "password TEXT NOT NULL," +
                        "role TEXT NOT NULL" +
                        ")",

                // SESSIONS
                "CREATE TABLE IF NOT EXISTS sessions (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "user_id INTEGER NOT NULL," +
                        "token TEXT NOT NULL UNIQUE," +
                        "created_at TEXT NOT NULL," +
                        "FOREIGN KEY (user_id) REFERENCES users(id)" +
                        ")",

                // SAMPLE BOOKS
                "INSERT OR IGNORE INTO books (id, title, author, category, available) VALUES " +
                        "(1, 'Introduction to Java', 'John Smith', 'Programming', 1)," +
                        "(2, 'Database Systems', 'Maria Garcia', 'Computer Science', 0)," +
                        "(3, 'Software Engineering Principles', 'Alan Brown', 'Engineering', 1)",

                // SAMPLE MEMBERS
                "INSERT OR IGNORE INTO members (id, name, email, type) VALUES " +
                        "(1, 'Alice Johnson', 'alice.johnson@stmarys.ac.uk', 'Student')," +
                        "(2, 'Michael Lee', 'michael.lee@stmarys.ac.uk', 'Staff')," +
                        "(3, 'Sara Ahmed', 'sara.ahmed@stmarys.ac.uk', 'Student')",

                // SAMPLE BORROWS
                "INSERT OR IGNORE INTO borrows (id, book_id, member_id, borrow_date, due_date, return_date, status) VALUES " +
                        "(1, 2, 1, '2025-03-01', '2025-03-15', NULL, 'BORROWED')," +
                        "(2, 1, 2, '2025-03-02', '2025-03-16', '2025-03-10', 'RETURNED')," +
                        "(3, 3, 3, '2025-03-05', '2025-03-19', NULL, 'BORROWED')",

                "INSERT OR IGNORE INTO users (username, password, role) VALUES " +
                        "('librarian1', 'lib123', 'LIBRARIAN')," +
                        "('student1', 'stud123', 'STUDENT')," +
                        "('student2', 'stud456', 'STUDENT')," +
                        "('guest1', 'guest123', 'GUEST');"
        };

        try (Connection conn = DBConnection.connect();
             Statement stmt = conn.createStatement()) {

            for (String sql : sqlStatements) {
                stmt.execute(sql);
            }

            System.out.println("✅ Database tables initialized successfully");

        } catch (Exception e) {
            System.out.println("❌ Database initialization error: " + e.getMessage());
        }
    }

    // ================= SAFETY MIGRATION =================
    private static void addRoleColumnIfMissing() {

        try (Connection conn = DBConnection.connect();
             Statement stmt = conn.createStatement()) {

            stmt.execute("ALTER TABLE users ADD COLUMN role TEXT");

        } catch (Exception ignored) {
            // already exists
        }
    }

    // ================= DEFAULT DATA =================
    private static void insertDefaultData() {

    String checkSql = "SELECT id FROM users WHERE username = 'admin'";
    String insertSql = "INSERT INTO users(username, password, role) VALUES ('admin', 'admin123', 'ADMIN')";

    try (Connection conn = DBConnection.connect();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(checkSql)) {

        if (!rs.next()) {
            stmt.executeUpdate(insertSql);
            System.out.println("✅ Default admin created");
        } else {
            System.out.println("✅ Admin already exists");
        }

    } catch (Exception e) {
        System.out.println("Database initialization error: " + e.getMessage());
    }
}
}