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

    //*******
    //for cleaning pages
    public static ArrayList<PHArticle> washrack = new ArrayList<>();
    //******

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


        Log.i(TAG, "SEARCH WORD: " + searchWord);
        String selection = PHDBHandler.COLUMN_TITLE +
                " LIKE ? OR " + PHDBHandler.COLUMN_TITLE + " LIKE ? OR "
                + PHDBHandler.COLUMN_TITLE + " LIKE ? ";

        String[] selArgs = new String[]{"%" + searchWord + "%",
                searchWord + "%",
                "%" + searchWord};


        //Cursor cursor = database.query(true, PHDBHandler.TABLE_PHARTICLE, requestedColumns, selection,
        //        selArgs, null, null, null,
        //       null);
        Cursor cursor;
        String searchQuery = "'*" + searchWord + "*'";
        if (searchWord == "" || searchWord == null || searchWord.isEmpty()) {
            cursor = database.rawQuery("select * from "
                    + PHDBHandler.TABLE_PHARTICLE, null);
        } else {
            cursor = database.rawQuery("select * from "
                    + PHDBHandler.TABLE_PHARTICLE + " where title match " + searchQuery, null);
        }

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
                int pageId = 0;
                while (keys.hasNext()) {
                    String key = (String) keys.next();

                    if (pages.get(key) instanceof JSONObject) {
                        JSONObject page = pages.getJSONObject(key);
                        JSONArray revisions = page.getJSONArray("revisions");
                        String title = page.getString("title");
                        JSONObject firstRev = revisions.getJSONObject(0);
                        String content = firstRev.get("*").toString();

                        PHArticle phArticle = new PHArticle(pageId, title,
                                content,
                                new Timestamp(System.currentTimeMillis()));
                        //addArticle(phArticle);
                        //Note: attempts to prettify the pages while this method is running creates unusable pages,
                        //so add the pages to a global ArrayList, then parse the PHArticles' contents to clean them,
                        //then add them to database.
                        washrack.add(phArticle);
                        Log.i(TAG, title);
                        Log.i(TAG, content);
                        pageId++;
                    }
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public void pageCleaner()
    {
        //*************************
        //TODO:
        //Parse content to make it pretty and presentable

        for(PHArticle phArticle: washrack)
        {
            String content = phArticle.getContent();
            phArticle.setContent(stringCleaner(content));
            addArticle(phArticle);
        }
        washrack.clear();
        /*

        */
        //*****************
    }
    //Parse the content of the given PHArticle to prettify it up
    public String stringCleaner(String content)
    {
        for (int i = 0; i < content.length(); i++)
        {
            //get rid of stub date
            //ex: in article "Check in at the Royal National Hotel

            //get ride of [[Image: ...]]
            //for example: [[Image:Convince People You Are a local step1.jpg|center]]
            if(content.charAt(i) == '[' && i+1 < content.length() && content.charAt(i+1) == '[')
            {
                String s1 = content.substring(i+2, i+2+("Image".length()));
                if(s1.equals("Image"))
                {
                    int j = i+2+("Image".length());
                    //find the first ']'
                    while(j < content.length() && content.charAt(j) != ']')
                    {
                        j++;
                    }
                    j = j+2;
                    //Log.d("Editing", "string = " + content.substring(i,j));
                    String firstPart = content.substring(0,i);
                    String secondPart = content.substring(j);
                    content = firstPart.concat(secondPart);
                }
            }

            //get rid of more stuff
            //more parsing

        }
        return content;
    }
    //used only by dbTester
    public void addArticleToWashRack(PHArticle phArticle)
    {
        washrack.add(phArticle);
    }

}
