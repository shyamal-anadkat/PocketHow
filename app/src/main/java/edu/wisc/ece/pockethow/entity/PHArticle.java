package edu.wisc.ece.pockethow.entity;

import android.text.method.DateTimeKeyListener;

import org.joda.time.DateTime;

import java.sql.Date;
import java.sql.Timestamp;


public class PHArticle {
    long _id;
    String _title;
    String _content;
    Timestamp _timestamp;

    public PHArticle() {
    }

    //public PHArticle(long id, String title, String content, DateTime lastAccess) {
    public PHArticle(long id, String title, String content, Timestamp timestamp) {
        this._id = id;
        this._title = title;
        this._content = content;
        this._timestamp = timestamp;
    }

    public long getID() {
        return this._id;
    }

    public void setID(int id) {
        this._id = id;
    }

    public String getTitle() {
        return this._title;
    }

    public void setTitle(String title) {
        this._title = title;
    }

    public String getContent() {
        return this._content;
    }

    public Timestamp getLastAccess() {
        return this._timestamp;
    }

    public void setLastAccess(Timestamp timestamp) {
        this._timestamp = timestamp;
    }

    public void setContent(String content) {
        this._content = content;
    }

}
