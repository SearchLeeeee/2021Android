package com.example.webviewapp.data;

import android.os.Parcel;
import android.os.Parcelable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class LoginState extends RealmObject implements Parcelable {
    private String LoginId;
    private boolean isLogin;
    @PrimaryKey
    long PrimaryKey;

    public LoginState(){}

    public LoginState(String LoginId,boolean isLogin){
        this.isLogin = isLogin;
        this.LoginId = LoginId;
    }

    protected LoginState(Parcel in) {
        LoginId = in.readString();
        isLogin = in.readByte() != 0;
        PrimaryKey = in.readLong();
    }

    public static final Creator<LoginState> CREATOR = new Creator<LoginState>() {
        @Override
        public LoginState createFromParcel(Parcel in) {
            return new LoginState(in);
        }

        @Override
        public LoginState[] newArray(int size) {
            return new LoginState[size];
        }
    };

    public long getPrimaryKey() {
        return PrimaryKey;
    }

    public String getLoginId() {
        return LoginId;
    }

    public void setPrimaryKey(long primaryKey) {
        PrimaryKey = primaryKey;
    }

    public void setLogin(boolean login) {
        isLogin = login;
    }

    public boolean getLogin() {
        return this.isLogin;
    }

    public void setLoginId(String loginId) {
        LoginId = loginId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(LoginId);
        dest.writeByte((byte) (isLogin ? 1 : 0));
        dest.writeLong(PrimaryKey);
    }
}
