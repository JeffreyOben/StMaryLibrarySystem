package ui;

import java.util.Scanner;
import services.*;
import models.User;

public class ConsoleMenu {

    private final Scanner sc = new Scanner(System.in);

    private final BookService bookService = new BookService();
    private final MemberService memberService = new MemberService();
    private final BorrowService borrowService = new BorrowService();
    private final UserService userService = new UserService();

    private User currentUser;

    // ================= SAFE INPUT =================
    private int readInt(String message) {
        while (true) {
            try {
                System.out.print(message);
                return Integer.parseInt(sc.nextLine().trim());
            } catch (Exception e) {
                System.out.println("❌ Invalid number. Try again.");
            }
        }
    }

    private String input(String msg) {
        System.out.print(msg);
        return sc.nextLine().trim();
    }

    // ================= LOGIN =================
    private boolean login() {

        System.out.println("\n=== LOGIN ===");

        if (userService.getAllUsers().isEmpty()) {
            System.out.println("No users found. Creating default ADMIN account...");
            userService.registerUser("admin", "admin123", "ADMIN");
        }

        int attempts = 0;

        while (attempts < 3) {

            String username = input("Username: ");
            String password = input("Password: ");

            User user = userService.login(username, password);

            if (user != null) {
                currentUser = user;
                System.out.println("✅ Login successful. Welcome " +
                        user.getUsername() + " (" + user.getRole() + ")");
                return true;
            }

            attempts++;
            System.out.println("❌ Invalid login. Attempts left: " + (3 - attempts));
        }

        return false;
    }

    // ================= ROLE CHECKS =================
    private boolean isAdmin() {
        return currentUser != null &&
                "ADMIN".equalsIgnoreCase(currentUser.getRole());
    }

    private boolean isLibrarianOrAdmin() {
        return currentUser != null &&
                ("ADMIN".equalsIgnoreCase(currentUser.getRole()) ||
                 "LIBRARIAN".equalsIgnoreCase(currentUser.getRole()));
    }

    // ================= MENU =================
    public void start() {

        if (!login()) {
            System.out.println("❌ Login failed. Exiting...");
            return;
        }

        while (true) {

            System.out.println("\n==== LIBRARY SYSTEM ====");
            System.out.println("User: " + currentUser.getUsername() +
                    " (" + currentUser.getRole() + ")");

            System.out.println("1. Add Book");
            System.out.println("2. View Books");
            System.out.println("3. Add Member");
            System.out.println("4. View Members");
            System.out.println("5. Borrow Book");
            System.out.println("6. View Borrow Records");
            System.out.println("7. Delete Book");
            System.out.println("8. Exit");
            System.out.println("9. Update Book");
            System.out.println("10. Search Book");
            System.out.println("11. Update Member");
            System.out.println("12. Delete Member");
            System.out.println("13. Search Member");
            System.out.println("14. Return Book");
            System.out.println("15. Check Overdue Books");

            if (isLibrarianOrAdmin()) {
                System.out.println("16. View Users");
            }

            if (isAdmin()) {
                System.out.println("17. Add User (LIBRARIAN)");
                System.out.println("18. Delete User");
            }

            int choice = readInt("\nEnter choice: ");

            switch (choice) {

                case 1 -> bookService.addBook(readInt("ID: "), input("Title: "), input("Author: "), input("Category: "));
                case 2 -> bookService.viewBooks();
                case 3 -> memberService.addMember(readInt("ID: "), input("Name: "), input("Email: "), input("Type: "));
                case 4 -> memberService.viewMembers();
                case 5 -> borrowService.borrowBook(readInt("Book ID: "), readInt("Member ID: "), input("Borrow Date: "), input("Due Date: "));
                case 6 -> borrowService.viewBorrowRecords();

                case 7 -> confirmDelete(() ->
                        bookService.deleteBook(readInt("Book ID: "))
                );

                case 8 -> {
                    System.out.println("👋 Goodbye!");
                    return;
                }

                case 9 -> bookService.updateBook(readInt("Book ID: "), input("New Title: "), input("New Category: "));
                case 10 -> bookService.searchBooks(input("Keyword: "));
                case 11 -> memberService.updateMember(readInt("Member ID: "), input("New Name: "), input("New Email: "));

                case 12 -> confirmDelete(() ->
                        memberService.deleteMember(readInt("Member ID: "))
                );

                case 13 -> memberService.searchMember(input("Keyword: "));
                case 14 -> borrowService.returnBook(readInt("Borrow ID: "));
                case 15 -> borrowService.checkOverdue();

                // ================= ADMIN ONLY =================
                case 16 -> {
                    if (!isLibrarianOrAdmin()) {
                        System.out.println("❌ Access denied.");
                        break;
                    }
                    userService.getAllUsers().forEach(u ->
                            System.out.println(u.getId() + " | " + u.getUsername() + " | " + u.getRole())
                    );
                }

                case 17 -> {
                    if (!isAdmin()) {
                        System.out.println("❌ Admin only.");
                        break;
                    }
                    String username = input("Username: ");
                    String password = input("Password: ");
                    userService.registerUser(username, password, "LIBRARIAN");
                    System.out.println("✅ Librarian created successfully.");
                }

                case 18 -> {
                    if (!isAdmin()) {
                        System.out.println("❌ Admin only.");
                        break;
                    }
                    confirmDelete(() ->
                            userService.deleteUser(readInt("User ID: "))
                    );
                }

                default -> System.out.println("❌ Invalid option");
            }
        }
    }

    // ================= HELPERS =================
    private void confirmDelete(Runnable action) {
        System.out.print("Are you sure? (yes/no): ");
        if (sc.nextLine().trim().equalsIgnoreCase("yes")) {
            action.run();
        } else {
            System.out.println("Cancelled.");
        }
    }
}