package edu.wisc.ece.pockethow.dbHandler;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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

    private static final String PHARTICLE_TABLE_CREATE =
            "CREATE TABLE " + TABLE_PHARTICLE + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY," +
                    COLUMN_TITLE + " TEXT," +
                    COLUMN_CONTENT + " TEXT)";

    private static final String PHARTICLE_TABLE_DELETE =
            "DROP TABLE IF EXISTS " + TABLE_PHARTICLE;


    //CATEGORY TABLE
    public static final String TABLE_CATEGORY_TO_PAGE = "";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_PAGEID = "page_id";

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
}
