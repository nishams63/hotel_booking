package com.rest.models;

public abstract class User {
    private final int userId;
    private String name;
    private String phone;
    private String email;

    protected User(int userId, String name, String phone, String email) {
        this.userId = userId; this.name = name; this.phone = phone; this.email = email;
    }
    public int getUserId() { return userId; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public void setName(String name) { this.name = name; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setEmail(String email) { this.email = email; }
}
