package edu.wisc.ece.pockethow.dbOperations;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.icu.text.SimpleDateFormat;
import android.util.Log;

import edu.wisc.ece.pockethow.dbHandler.PHDBHandler;
import edu.wisc.ece.pockethow.entity.PHArticle;

/**
 * Created by zosta on 10/23/2017.
 */


public class DbOperations {
    // set the format to sql date time
    public final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    public static final String TAG = "DB_OPERATIONS";

    SQLiteOpenHelper dbHandler;
    SQLiteDatabase database;

    public DbOperations(Context context) {
        dbHandler = new PHDBHandler(context);
    }

    private static final String[] allArticleColumns = {
            PHDBHandler.COLUMN_ID,
            PHDBHandler.COLUMN_TITLE,
            PHDBHandler.COLUMN_CONTENT,
            PHDBHandler.COLUMN_ARTICLE_LASTACCESS
    };

    public void open() {
        Log.i(TAG, "DB Opened");
        database = dbHandler.getWritableDatabase();
    }

    public void close() {
        Log.i(TAG, "Database Closed");
        dbHandler.close();
    }

    public PHArticle addArticle(PHArticle phArticle) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(PHDBHandler.COLUMN_ID, phArticle.getID());
        contentValues.put(PHDBHandler.COLUMN_TITLE, phArticle.getTitle());
        contentValues.put(PHDBHandler.COLUMN_CONTENT, phArticle.getContent());
        contentValues.put(PHDBHandler.COLUMN_ARTICLE_LASTACCESS,
                dateFormat.format(phArticle.getLastAccess()));
        database.insert(PHDBHandler.TABLE_PHARTICLE, null, contentValues);
        return phArticle;
    }

}
