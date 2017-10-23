package edu.wisc.ece.pockethow.entity;

import org.joda.time.DateTime;

import java.sql.Date;

/**
 * Created by zostale on 10/23/2017.
 */

public class PHCategory {

    String _category;

    //comma delimited string of pageIDs as SQLITE types are atomic
    String _pageIdList;
    DateTime _lastAccess;

    public PHCategory() {
    }

    public PHCategory(String category, String pageIdList, DateTime lastAccess) {
        this._category = category;
        this._pageIdList = pageIdList;
        this._lastAccess = lastAccess;
    }
}
