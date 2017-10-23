package edu.wisc.ece.pockethow.entity;

import android.text.method.DateTimeKeyListener;

import org.joda.time.DateTime;

import java.sql.Date;

/**
 * Created by zosta on 10/23/2017.
 */

public class PHArticle {
    long _id;
    String _title;
    String _content;
    DateTime _lastAccess;

    public PHArticle() {
    }

    public PHArticle(long id, String title, String content, DateTime lastAccess) {
        this._id = id;
        this._title = title;
        this._content = content;
        this._lastAccess = lastAccess;
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

    public DateTime getLastAccess() {
        return this._lastAccess;
    }

    public void setLastAccess(DateTime lastAccess) {
        this._lastAccess = lastAccess;
    }

    public void setContent(String content) {
        this._content = content;
    }

}
