package com.example.knzwapp;

public class HelperClass {
    String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    String password;

    public HelperClass(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public HelperClass() {
    }
}