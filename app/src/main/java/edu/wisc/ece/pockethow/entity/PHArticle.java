package edu.wisc.ece.pockethow.entity;

/**
 * Created by zosta on 10/23/2017.
 */

public class PHArticle {
    long _id;
    String _title;
    String _content;

    public PHArticle() {
    }

    public PHArticle(long id, String title, String content) {
        this._id = id;
        this._title = title;
        this._content = content;
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

    public void setContent(String content) {
        this._content = content;
    }


}
