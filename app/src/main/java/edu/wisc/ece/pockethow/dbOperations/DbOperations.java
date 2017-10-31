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
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.DoubleBuffer;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import edu.wisc.ece.pockethow.dbHandler.PHDBHandler;
import edu.wisc.ece.pockethow.entity.PHArticle;
import edu.wisc.ece.pockethow.entity.PHCategory;
import edu.wisc.ece.pockethow.httpRequests.PHWikihowFetches;

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
            PHDBHandler.COLUMN_PHARTICLE_ID, //Do we want to add a Column_category filed to article?
            PHDBHandler.COLUMN_TITLE,
            PHDBHandler.COLUMN_CONTENT,
            PHDBHandler.COLUMN_ARTICLE_LASTACCESS
    };

    private static final String[] allCategoryToPageIDColumns = {
            PHDBHandler.COLUMN_CATEGORY,
            PHDBHandler.COLUMN_CATEGORY_PAGEIDLIST,
            PHDBHandler.COLUMN_CATEGORY_ID,
            PHDBHandler.COLUMN_CATEGORY_LASTACCESS
    };

    public void open() {
        Log.i(TAG, "DB Opened");
        database = dbHandler.getWritableDatabase();
        Log.d("DbOperations", database.getPath());
    }

    public void close() {
        Log.i(TAG, "Database Closed");
        dbHandler.close();
    }


    public PHCategory addCategoryToPageID(PHCategory category) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(PHDBHandler.COLUMN_CATEGORY, category.getCategory());
        contentValues.put(PHDBHandler.COLUMN_CATEGORY_ID, category.getId());
        contentValues.put(PHDBHandler.COLUMN_CATEGORY_PAGEIDLIST, category.getPageIdList());
        contentValues.put(PHDBHandler.COLUMN_CATEGORY_LASTACCESS, "");

        if (database.insert(PHDBHandler.TABLE_CATEGORY_TO_PAGEID, null, contentValues) == -1) {
            Log.e("DbOperations", "PHCategory: database insert failed");
        }

        return category;
    }

    public PHArticle addArticle(PHArticle phArticle) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(PHDBHandler.COLUMN_PHARTICLE_ID, phArticle.getID());
        contentValues.put(PHDBHandler.COLUMN_TITLE, phArticle.getTitle());
        contentValues.put(PHDBHandler.COLUMN_CONTENT, phArticle.getContent());
        Timestamp timestamp = phArticle.getLastAccess();
        contentValues.put(PHDBHandler.COLUMN_ARTICLE_LASTACCESS, timestamp.toString());
        if (database.insert(PHDBHandler.TABLE_PHARTICLE, null, contentValues) == -1) {
            Log.e("DbOperations", "PHArticle - database insert failed");
        }
        return phArticle;
    }

    public String getPageIds(String category) {
        Cursor cursor = database.query(true,
                PHDBHandler.TABLE_CATEGORY_TO_PAGEID, allCategoryToPageIDColumns,
                PHDBHandler.COLUMN_CATEGORY + "=?", new String[]{category},
                null,
                null,
                null,
                null);
        if (cursor != null)
            cursor.moveToFirst();
        return cursor.getString(cursor.getColumnIndex(PHDBHandler.COLUMN_CATEGORY_PAGEIDLIST));
    }

    public ArrayList<PHArticle> getArticle(String searchWord) {

        ArrayList<PHArticle> articleArrayList = new ArrayList<PHArticle>();
        String[] requestedColumns = new String[]{PHDBHandler.COLUMN_PHARTICLE_ID, PHDBHandler.COLUMN_TITLE, PHDBHandler.COLUMN_CONTENT, PHDBHandler.COLUMN_ARTICLE_LASTACCESS};

        Cursor cursor = database.query(true, PHDBHandler.TABLE_PHARTICLE, requestedColumns, PHDBHandler.COLUMN_TITLE + " LIKE ? OR " + PHDBHandler.COLUMN_CONTENT + " LIKE ?",
                new String[]{"%" + searchWord + "%"}, null, null, null,
                null);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            // do what you need with the cursor here


            Long columnID = cursor.getLong(cursor.getColumnIndex(PHDBHandler.COLUMN_PHARTICLE_ID));
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


    public void parsePagesAndPopulateDB(JSONObject jsonObject) {
        if (jsonObject != null) {

            try {
                JSONObject pages = jsonObject.getJSONObject("query").getJSONObject("pages");
                Iterator<?> keys = pages.keys();
                while (keys.hasNext()) {
                    String key = (String) keys.next();

                    if (pages.get(key) instanceof JSONObject) {
                        JSONObject page = pages.getJSONObject(key);
                        JSONArray revisions = page.getJSONArray("revisions");
                        String title = page.getString("title");
                        JSONObject firstRev = revisions.getJSONObject(0);
                        String content = firstRev.get("*").toString();

                        PHArticle phArticle = new PHArticle(0, title,
                                content,
                                new Timestamp(System.currentTimeMillis()));
                        addArticle(phArticle);
                        Log.i(TAG, title);
                        Log.i(TAG, content);
                    }
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


}
