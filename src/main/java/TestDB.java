package main.java;
import java.sql.Connection;
import java.sql.DriverManager;

public class TestDB {
    public static void main(String[] args) {

        String url = "jdbc:sqlite:library.db";

        try {
    

            Connection conn = DriverManager.getConnection(url);

            System.out.println("✅ SUCCESS: Connected to SQLite!");
            conn.close();

        } catch (Exception e) {
            System.out.println("❌ FAILED: " + e.getMessage());
        }
    }
}