package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL = "jdbc:sqlite:library.db";

    public static Connection connect() {
        try {
            // Optional in modern Java (but ok to keep)
            Class.forName("org.sqlite.JDBC");

            return DriverManager.getConnection(URL);

        } catch (ClassNotFoundException e) {
            System.out.println("SQLite JDBC driver NOT found. Add sqlite-jdbc jar.");
        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
        }
        return null;
    }
}