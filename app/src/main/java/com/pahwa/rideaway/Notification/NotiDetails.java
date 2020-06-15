package com.pahwa.rideaway.Notification;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "notis")
public class NotiDetails {

    @PrimaryKey(autoGenerate = true)
    int uid=0;

    @ColumnInfo(name = "notiread")
    int read=0;

    @ColumnInfo(name = "notititle")
    String title;

    @ColumnInfo(name = "notibody")
    String body;

    String time;

    public NotiDetails( int read, String title, String body,String time) {
        this.read = read;
        this.title = title;
        this.body = body;
        this.time=time;
    }

    public NotiDetails() {
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getRead() {
        return read;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setRead(int read) {
        this.read = read;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
