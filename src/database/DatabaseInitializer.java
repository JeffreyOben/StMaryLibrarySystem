package database;

import utils.PasswordUtil;

import java.sql.Connection;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initializeDatabase() {
        createTables();
        addRoleColumnIfMissing();
        insertDefaultData();
    }

    // ================= CREATE TABLES =================
    private static void createTables() {

        String[] createTableStatements = {

                "CREATE TABLE IF NOT EXISTS books (" +
                        "id INTEGER PRIMARY KEY," +
                        "title TEXT NOT NULL," +
                        "author TEXT NOT NULL," +
                        "category TEXT NOT NULL," +
                        "available INTEGER DEFAULT 1" +
                        ")",

                "CREATE TABLE IF NOT EXISTS members (" +
                        "id INTEGER PRIMARY KEY," +
                        "name TEXT NOT NULL," +
                        "email TEXT NOT NULL UNIQUE," +
                        "type TEXT NOT NULL" +
                        ")",

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

                "CREATE TABLE IF NOT EXISTS users (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "username TEXT NOT NULL UNIQUE," +
                        "password TEXT NOT NULL," +
                        "role TEXT NOT NULL" +
                        ")",

                "CREATE TABLE IF NOT EXISTS sessions (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "user_id INTEGER NOT NULL," +
                        "token TEXT NOT NULL UNIQUE," +
                        "created_at TEXT NOT NULL," +
                        "FOREIGN KEY (user_id) REFERENCES users(id)" +
                        ")"
        };

        try (Connection conn = DBConnection.connect();
             Statement stmt = conn.createStatement()) {

            for (String sql : createTableStatements) {
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

            // safe attempt (ignored if already exists)
            stmt.execute("ALTER TABLE users ADD COLUMN role TEXT");

        } catch (Exception ignored) {
            // column already exists → ignore
        }
    }

    // ================= DEFAULT DATA =================
    private static void insertDefaultData() {

        try (Connection conn = DBConnection.connect();
             Statement stmt = conn.createStatement()) {

            stmt.execute(
                    "INSERT OR IGNORE INTO users(username, password, role) " +
                    "VALUES ('admin', '" + PasswordUtil.hash("admin123") + "', 'ADMIN')"
            );

            System.out.println("✅ Default admin user ready");

        } catch (Exception e) {
            System.out.println("❌ Default data error: " + e.getMessage());
        }
    }
}