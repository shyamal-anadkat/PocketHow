package edu.wisc.ece.pockethow.entity;

import org.joda.time.DateTime;

import java.sql.Date;
import java.sql.Timestamp;

/*** PccketHow*/

public class PHCategory {

    String _category;
    private int _id;
    //comma delimited string of pageIDs as SQLITE types are atomic
    String _pageIdList;
    Timestamp _lastAccess;

    public PHCategory() {
    }

    public PHCategory(int id, String category, String pageIdList, Timestamp lastAccess) {
        this._id = id;
        this._category = category;
        this._pageIdList = pageIdList;
        this._lastAccess = lastAccess;
    }

    public long getId() {
        return this._id;
    }

    public void setId(int id) {
        this._id = id;
    }

    public void setLastAccess(Timestamp ts) {
        this._lastAccess = ts;
    }

    public Timestamp getLastAccess() {
        return this._lastAccess;
    }

    public String getCategory() {
        return this._category;
    }

    public void setCategory(String category) {
        this._category = category;
    }

    public void setPageIdList(String pageIdList) {
        this._pageIdList = pageIdList;
    }

    public String getPageIdList() {
        return this._pageIdList;
    }

}
