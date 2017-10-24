package edu.wisc.ece.pockethow.dbHandler;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;

import java.sql.Date;

/**
 * Created by zosta on 10/23/2017.
 */

public class PHDBHandler extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "PocketHow.db";
    private static final int DATABASE_VERSION = 1;

    //ARTICLE TABLE
    public static final String TABLE_PHARTICLE = "articles";
    public static final String COLUMN_ID = "page_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_ARTICLE_LASTACCESS = "last_access";

    private static final String PHARTICLE_TABLE_CREATE =
            "CREATE VIRTUAL TABLE " + TABLE_PHARTICLE + " USING fts3(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY NOT NULL," +
                    COLUMN_TITLE + " TEXT NOT NULL," +
                    COLUMN_CONTENT + " TEXT NOT NULL)";

    private static final String PHARTICLE_TABLE_DELETE =
            "DROP TABLE IF EXISTS " + TABLE_PHARTICLE;


    private static final String PHARTICLE_TABLE_ADD_DOCUMENT_PREFIX =
            "INSERT INTO " + TABLE_PHARTICLE + "(" + COLUMN_ID  + ", " + COLUMN_TITLE + ", " + COLUMN_CONTENT;

    //CATEGORY TABLE
    public static final String TABLE_CATEGORY_TO_PAGE = "";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_PAGEID = "page_id";
    public static final String COLUMN_PAGEIDLIST = "page_id";
    public static final DateTime COLUMN_CATEGORY_LASTACCESS = new DateTime();

    public PHDBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(PHARTICLE_TABLE_CREATE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(PHARTICLE_TABLE_DELETE);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void addItem(String columnTitle, String columnContent)
    {
        
    }

}
