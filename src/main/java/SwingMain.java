package main.java;

import database.DatabaseInitializer;
import javax.swing.*;
import ui.LoginFrame;

public class SwingMain {

    public static void main(String[] args) {

        // Initialize database tables
        DatabaseInitializer.initializeDatabase();

        // Start GUI safely on Swing thread
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}