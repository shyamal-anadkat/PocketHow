package edu.wisc.ece.pockethow.dbOperations;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.icu.text.SimpleDateFormat;
import android.util.Log;

import org.joda.time.DateTime;

import java.nio.DoubleBuffer;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

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

    //TODO: addArticle crashes the app
    public PHArticle addArticle(PHArticle phArticle) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(PHDBHandler.COLUMN_ID, phArticle.getID());
        contentValues.put(PHDBHandler.COLUMN_TITLE, phArticle.getTitle());
        contentValues.put(PHDBHandler.COLUMN_CONTENT, phArticle.getContent());
        //StringBuffer buffer = new StringBuffer("howdy");
        Timestamp timestamp = phArticle.getLastAccess();
        String timestampString = timestamp.toString();

        //dateFormat.format(dateTime); //TODO: bug source
        //contentValues.put(PHDBHandler.COLUMN_ARTICLE_LASTACCESS, buffer);
        /*
        contentValues.put(PHDBHandler.COLUMN_ARTICLE_LASTACCESS,
                dateFormat.format(phArticle.getLastAccess()));
                */
        contentValues.put(PHDBHandler.COLUMN_ARTICLE_LASTACCESS, timestampString);
        if (database.insert(PHDBHandler.TABLE_PHARTICLE, null, contentValues) == -1)
        {
            Log.d("DbOperations", "database insert failed");
        }
        return phArticle;
    }
    public ArrayList<PHArticle> getArticle(String searchWord)
    {
        /*Alternative method: issue, may not use the "MATCH" which is unique to the FTS3 and FTS4
        query (boolean distinct,
                String table,
                String[] columns,
                String selection,
                String[] selectionArgs,
                String groupBy,
                String having,
                String orderBy,
                String limit)
         */
        /*String[] columns = new String[2];
        columns[0] = PHDBHandler.COLUMN_TITLE;
        columns[1] = PHDBHandler.COLUMN_CONTENT;
        String[] selectionArgs = new String[1];
        selectionArgs[0] = searchWord;
        Cursor cursor = query(true, PHDBHandler.TABLE_PHARTICLE, columns, PHDBHandler.COLUMN_TITLE, selectionArgs);
        */
        /*
        execSQL() cannot be used for SELECT operations
        */


        ArrayList<PHArticle> articleArrayList = new ArrayList<PHArticle>();
        //String searchCmd = "SELECT * FROM " + PHDBHandler.TABLE_PHARTICLE + " WHERE " + PHDBHandler.COLUMN_TITLE + " MATCH ? OR " + PHDBHandler.COLUMN_CONTENT + " MATCH ?";
        //String searchCmd = "SELECT * FROM " +  PHDBHandler.TABLE_PHARTICLE + " WHERE " + PHDBHandler.COLUMN_CONTENT + " MATCH ?";
        //String searchCmd = "SELECT * FROM " +  PHDBHandler.TABLE_PHARTICLE + " WHERE " + PHDBHandler.TABLE_PHARTICLE + " LIKE ?";

        String[] requestedColumns = new String[] { PHDBHandler.COLUMN_ID,PHDBHandler.COLUMN_TITLE,PHDBHandler.COLUMN_CONTENT,PHDBHandler.COLUMN_ARTICLE_LASTACCESS };

        Cursor cursor = database.query(true, PHDBHandler.TABLE_PHARTICLE, requestedColumns , PHDBHandler.COLUMN_TITLE + " OR " + PHDBHandler.COLUMN_CONTENT + " LIKE ?",
            new String[] { "%"+searchWord+"%" }, null, null, null,
            null);

/*
        ArrayList<PHArticle> articleArrayList = new ArrayList<PHArticle>(); //ArrayList that will be returned as output
        String[] columnsToOutput = new String[] {PHDBHandler.COLUMN_ID, PHDBHandler.COLUMN_TITLE, PHDBHandler.COLUMN_CONTENT, PHDBHandler.COLUMN_ARTICLE_LASTACCESS};
        String selection = PHDBHandler.COLUMN_TITLE + "MATCH ?";
        String[] selectionArgs = new String[] {searchWord + "*"};
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(PHDBHandler.TABLE_PHARTICLE);
        Cursor cursor = builder.query(dbHandler.getReadableDatabase(),
                columnsToOutput, selection, selectionArgs, null, null, null);

*/
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            // do what you need with the cursor here


            Long columnID = cursor.getLong(cursor.getColumnIndex(PHDBHandler.COLUMN_ID));
            String columnTitle = cursor.getString(cursor.getColumnIndex(PHDBHandler.COLUMN_TITLE));
            String columnContent = cursor.getString(cursor.getColumnIndex(PHDBHandler.COLUMN_CONTENT));
            String dateTimeString = cursor.getString(cursor.getColumnIndex(PHDBHandler.COLUMN_ARTICLE_LASTACCESS));
            Timestamp timestamp = Timestamp.valueOf(dateTimeString);

            try {
                Date date = dateFormat.parse(dateTimeString);
                articleArrayList.add(new PHArticle(columnID, columnTitle, columnContent, timestamp));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        cursor.close();
        return articleArrayList;
    }
}
