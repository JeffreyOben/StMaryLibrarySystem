package services;

import dao.BorrowDAO;
import models.Borrow;

public class BorrowService {

    private BorrowDAO borrowDAO = new BorrowDAO();

    // ================= BORROW BOOK =================
    public void borrowBook(int bookId, int memberId, String borrowDate, String dueDate) {
        Borrow borrow = new Borrow(0, bookId, memberId, borrowDate, dueDate, "BORROWED");
        borrowDAO.borrowBook(borrow);
    }

    // ================= VIEW BORROW RECORDS =================
    public void viewBorrowRecords() {
        borrowDAO.viewBorrowRecords();
    }

    // ================= RETURN BOOK =================
    public void returnBook(int borrowId) {
        borrowDAO.returnBook(borrowId);
    }

    // ================= OVERDUE CHECK =================
    public void checkOverdue() {
        borrowDAO.checkOverdue();
    }
}