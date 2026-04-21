package models;

public class User {

    private int id;
    private String username;
    private String password;
    private String role;

    public User(int id, String username, String password, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // ================= GETTERS =================
    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    // ================= SETTER =================
    public void setRole(String role) {
        this.role = role;
    }

    // ================= TO STRING =================
    @Override
    public String toString() {
        return id + " | " + username + " | " + role;
    }
}