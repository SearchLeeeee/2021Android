package com.example.webviewapp.data;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Permissions extends RealmObject {
    @PrimaryKey
    String permission;
    long time;

    public Permissions() {
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Permissions(String permission, long time) {
        this.permission = permission;
        this.time = time;
    }
}
