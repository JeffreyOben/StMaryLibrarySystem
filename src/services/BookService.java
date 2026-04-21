package services;

import dao.BookDAO;

public class BookService {

    private BookDAO dao = new BookDAO();

    // ADD BOOK
    public void addBook(int id, String title, String author, String category) {
        dao.addBook(id, title, author, category);
    }

    // VIEW BOOKS
    public void viewBooks() {
        dao.getAllBooks();
    }

    // DELETE BOOK
    public void deleteBook(int id) {
        dao.deleteBook(id);
    }

    // UPDATE BOOK
    public void updateBook(int id, String title, String category) {
        dao.updateBook(id, title, category);
    }

    // SEARCH BOOKS
    public void searchBooks(String keyword) {
        dao.searchBook(keyword); 
    }
}