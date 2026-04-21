# 📘 St Mary’s Digital Library System

## 📖 Overview
The St Mary’s Digital Library System is a Java-based application designed to modernise the management of library operations.  
It replaces manual processes with a structured system for managing books, members, and borrowing activities.

The system supports both:
- Console-based interaction
- Graphical User Interface (GUI) using Java Swing

## 🎯 Features

### 📚 Book Management
- Add new books
- View all books
- Update book details
- Delete books with confirmation
- Search books by title, author, or ID

### 👥 Member Management
- Register new members
- View member list
- Update member information
- Delete members
- Search members

### 🔄 Borrowing System
- Borrow books with due dates
- Return books
- Track borrowing records
- Automatically detect overdue books

### 🔐 User Authentication & Roles
- Secure login system
- Role-based access:
  - Admin – full access
  - Librarian – restricted access
- Admin-only user management

## 🖥️ User Interfaces

### Console Application
- Menu-driven system
- Clear prompts and validation
- Full CRUD operations supported

### Graphical User Interface (GUI)
- Dashboard layout
- Sidebar navigation
- Tables for data display
- Buttons for actions (Add, Update, Delete, Refresh)
- Styled UI with modern design

## 🗄️ Database

- SQLite database (library.db)
- JDBC used for connectivity
- Persistent data storage

### Tables:
- books
- members
- borrows
- users
- sessions

## 🏗️ System Architecture

UI Layer (Swing / Console)  
↓  
Service Layer (Business Logic)  
↓  
DAO Layer (Database Access)  
↓  
SQLite Database  

## ⚙️ Technologies Used

- Java (JDK 22+)
- Java Swing (GUI)
- SQLite
- JDBC (SQLite Driver)
- Java Collections Framework

## 🚀 How to Run

1. Clone or Download Project
2. Open in IDE (VS Code / NetBeans / IntelliJ)
3. Ensure SQLite JDBC driver is added
4. Run Main.java

## 🔑 Default Login

Username: admin  
Password: admin123  
Role: ADMIN  

## 📊 Advanced Features

- Role-based access control
- Overdue book detection
- Search & filtering functionality
- Styled GUI (modern UI design)

## 🧪 Validation & Error Handling

- Numeric validation for IDs
- Email format validation
- Exception handling for database operations
- User-friendly error messages

## 📈 Future Improvements

- Charts dashboard (data visualisation)
- Multi-threaded background loading
- Export to PDF/Excel
- Advanced filtering and analytics

## 📂 Project Structure

src/  
 ├── dao/  
 ├── database/  
 ├── models/  
 ├── services/  
 ├── ui/  

## 👨‍💻 Author

Jeffrey Oben  
St Mary’s University  
CPS4005 – Object-Oriented Programming  

## 📌 Conclusion

This system demonstrates:
- Object-Oriented Programming principles
- Database integration
- GUI development
- Clean architecture design

It provides a scalable and efficient solution for managing a digital library system.
