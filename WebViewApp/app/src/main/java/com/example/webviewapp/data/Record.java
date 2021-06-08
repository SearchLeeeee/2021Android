package com.example.webviewapp.data;

import android.os.Parcel;
import android.os.Parcelable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Record extends RealmObject implements Parcelable {

    private long uid;
    @PrimaryKey
    private long time;
    private String url;
    private String title;
    private String details;
    private int isHistory;

    public Record() {
    }

    public Record(long uid) {
        this.uid = uid;
    }

    public Record(long uid, long time, String url, String title, String details, int isHistory) {
        this.uid = uid;
        this.time = time;
        this.url = url;
        this.title = title;
        this.details = details;
        this.isHistory = isHistory;
    }

    protected Record(Parcel in) {
        uid = in.readLong();
        time = in.readLong();
        url = in.readString();
        title = in.readString();
        details = in.readString();
        isHistory = in.readInt();
    }

    public static final Creator<Record> CREATOR = new Creator<Record>() {
        @Override
        public Record createFromParcel(Parcel source) {
            return new Record(source);
        }

        @Override
        public Record[] newArray(int size) {
            return new Record[size];
        }
    };

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public int getIsHistory() {
        return isHistory;
    }

    public void setIsHistory(int isHistory) {
        this.isHistory = isHistory;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(getUid());
        dest.writeLong(getTime());
        dest.writeString(getUrl());
        dest.writeString(getTitle());
        dest.writeString(getDetails());
        dest.writeInt(getIsHistory());
    }
}
