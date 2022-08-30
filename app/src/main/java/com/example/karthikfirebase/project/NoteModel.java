package com.example.karthikfirebase.project;

import com.google.firebase.Timestamp;

public class NoteModel {

    String title,desc,uid,timeStamp2;
    Timestamp timestamp;

    public NoteModel(String title, String desc, String uid) {
        this.title = title;
        this.desc = desc;
        this.uid = uid;
    }

    public NoteModel(String title, String desc, String uid, String timeStamp2) {
        this.title = title;
        this.desc = desc;
        this.uid = uid;
        this.timeStamp2 = timeStamp2;
    }

    public NoteModel(String title, String desc, String uid, Timestamp timestamp) {
        this.title = title;
        this.desc = desc;
        this.uid = uid;
        this.timestamp = timestamp;
    }

    public String getTimeStamp2() {
        return timeStamp2;
    }

    public void setTimeStamp2(String timeStamp2) {
        this.timeStamp2 = timeStamp2;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
