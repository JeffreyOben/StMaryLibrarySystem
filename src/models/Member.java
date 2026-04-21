package models;

public class Member {

    private int id;
    private String name;
    private String email;
    private String type;

    public Member(int id, String name, String email, String type) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.type = type;
    }

    // ================= GETTERS =================
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getType() {
        return type;
    }

    // ================= SETTERS =================
    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setType(String type) {
        this.type = type;
    }

    // ================= TO STRING =================
    @Override
    public String toString() {
        return id + " | " + name + " | " + email + " | " + type;
    }
}