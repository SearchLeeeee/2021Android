package com.example.webviewapp.data;

public class User{
    private String email;
    private int avatarId;

    public User() {
    }

    public User(String Email, int avatarId) {
        this.email = Email;
        this.avatarId = avatarId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAvatarId() {
        return avatarId;
    }

    public void setAvatarId(int avatarId) {
        this.avatarId = avatarId;
    }
}
