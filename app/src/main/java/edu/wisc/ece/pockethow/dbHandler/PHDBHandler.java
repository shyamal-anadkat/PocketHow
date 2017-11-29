package edu.wisc.ece.pockethow.dbHandler;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/*** PocketHow */

public class PHDBHandler extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "PocketHow.db";
    private static final int DATABASE_VERSION = 2;

    //*************ARTICLE TABLE*************//
    public static final String TABLE_PHARTICLE = "articles";
    public static final String COLUMN_PHARTICLE_ID = "page_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_ARTICLE_LASTACCESS = "last_access";

    public static final String PHARTICLE_TABLE_CREATE =
            "CREATE VIRTUAL TABLE " + TABLE_PHARTICLE + " USING fts4(" +
                    COLUMN_PHARTICLE_ID + " INTEGER PRIMARY KEY autoincrement," +
                    COLUMN_TITLE + " TEXT NOT NULL," +
                    COLUMN_CONTENT + " BLOB NOT NULL, " +
                    COLUMN_ARTICLE_LASTACCESS + " TEXT NOT NULL, tokenize=porter);";

    public static final String PHARTICLE_TABLE_DELETE =
            "DROP TABLE IF EXISTS " + TABLE_PHARTICLE;

    //****************************************************//


    //*************CATEGORY TABLE*************************//
    public static final String TABLE_CATEGORY_TO_PAGEID = "category_to_pageid";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_CATEGORY_ID = "_id";
    public static final String COLUMN_CATEGORY_PAGEIDLIST = "page_id";
    public static final String COLUMN_CATEGORY_LASTACCESS = "last_access";

    public static final String TABLE_CATEGORY_TO_PAGEID_CREATE = "CREATE TABLE " + TABLE_CATEGORY_TO_PAGEID
            + "(" + COLUMN_CATEGORY_ID + " integer primary key autoincrement, "
            + COLUMN_CATEGORY + " TEXT NOT NULL," +
            COLUMN_CATEGORY_PAGEIDLIST + " TEXT," +
            COLUMN_CATEGORY_LASTACCESS + " TEXT NOT NULL);";
    private static final String CATEGORY_TO_PAGEID_TABLE_DELETE =
            "DROP TABLE IF EXISTS " + TABLE_CATEGORY_TO_PAGEID;
    //*******************************************************//
    /*****SEARCH WORDS TABLE***/
    public static final String searchWordColumn = "searchWord";
    public static final String searchWordTable = "searchwordTempTable";
    public static final String extraColumn = "extraColumn";

    public static final String TABLE_SEARCH_WORD_CREATE = "CREATE TABLE " + searchWordTable
            + "("
            + searchWordColumn + " VARCHAR PRIMARY KEY);";
    String TABLE_SEARCH_WORD_DELETE = "DROP TABLE IF EXISTS " + searchWordTable;
    //******//

    public PHDBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(PHARTICLE_TABLE_CREATE);
        db.execSQL(TABLE_CATEGORY_TO_PAGEID_CREATE);
        db.execSQL(TABLE_SEARCH_WORD_CREATE);
    }

    /**
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(PHARTICLE_TABLE_DELETE);
        db.execSQL(CATEGORY_TO_PAGEID_TABLE_DELETE);
        /*attempt*/
        db.execSQL(TABLE_SEARCH_WORD_DELETE);
        onCreate(db);
    }

    /**
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }


}
