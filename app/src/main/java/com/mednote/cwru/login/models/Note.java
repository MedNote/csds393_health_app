package com.mednote.cwru.login.models;

import java.io.Serializable;

public class Note implements Serializable {
    private String note;
    private String date;

    public Note(String note, String date) {
        this.note = note;
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public String getNote() {
        return note;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setNote(String note) {
        this.note = note;
    }
}