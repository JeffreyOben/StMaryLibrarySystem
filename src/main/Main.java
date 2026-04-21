package main;

import database.DatabaseInitializer;
import ui.LoginFrame;

public class Main {

    public static void main(String[] args) {

        DatabaseInitializer.initializeDatabase();

        javax.swing.SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}