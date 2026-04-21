package models;

public class Borrow {

    private int bookId;
    private int memberId;
    private String borrowDate;
    private String dueDate;
    private String status;

    public Borrow(int bookId, int memberId, String borrowDate, String dueDate, String status) {
        this.bookId = bookId;
        this.memberId = memberId;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.status = status;
    }

    // ================= GETTERS =================
    public int getBookId() {
        return bookId;
    }

    public int getMemberId() {
        return memberId;
    }

    public String getBorrowDate() {
        return borrowDate;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getStatus() {
        return status;
    }

    // ================= SETTERS (IMPORTANT FOR UPDATES) =================
    public void setStatus(String status) {
        this.status = status;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    // ================= TO STRING =================
    @Override
    public String toString() {
        return bookId + " | " + memberId + " | " + borrowDate + " | " + dueDate + " | " + status;
    }
}