package com.example.webviewapp.data;

import android.os.Parcel;
import android.os.Parcelable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class User extends RealmObject implements Parcelable {

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
    /**
     * 表示该字段是主键
     * <p>
     * 字段类型必须是字符串（String）或整数（byte，short，int或long）
     * 以及它们的包装类型（Byte,Short, Integer, 或 Long）。不可以存在多个主键，
     * 使用字符串字段作为主键意味着字段被索引（注释@PrimaryKey隐式地设置注释@Index）。
     */
    @PrimaryKey
    private long primaryKey;
    private long uid;
    private long password;

    public User() {
    }

    public User(long uid) {
        this.uid = uid;
    }


    public User(long uid, long password) {
        this.uid = uid;
        this.password = password;
    }

    protected User(Parcel in) {
        primaryKey = in.readLong();
        uid = in.readLong();
        password = in.readLong();

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(getPrimaryKey());
        dest.writeLong(getUid());
        dest.writeLong(getPassword());
    }

    public long getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(long primaryKey) {
        this.primaryKey = primaryKey;
    }

    public long getPassword() {
        return password;
    }

    public void setPassword(long password) {
        this.password = password;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }


    @Override
    public int describeContents() {
        return 0;
    }
}
