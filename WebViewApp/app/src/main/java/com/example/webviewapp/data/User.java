package com.example.webviewapp.data;

import android.os.Parcel;
import android.os.Parcelable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class User{
    private String Email;
    private int avatarId;

    public User() {
    }

    //fb
    public User(String Email, int avatarId) {
        this.Email = Email;
        this.avatarId = avatarId;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public int getAvatarId() {
        return avatarId;
    }

    public void setAvatarId(int avatarId) {
        this.avatarId = avatarId;
    }
}
