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
import java.util.LinkedHashSet;
import java.util.Set;

import edu.wisc.ece.pockethow.dbHandler.PHDBHandler;
import edu.wisc.ece.pockethow.entity.PHArticle;
import edu.wisc.ece.pockethow.entity.PHCategory;
import edu.wisc.ece.pockethow.httpRequests.PHWikihowFetches;
import me.xdrop.fuzzywuzzy.FuzzySearch;

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
        ArrayList<Long> pageIdList = new ArrayList<>();
        ArrayList<PHArticle> articleArrayList = new ArrayList<PHArticle>();
        String[] requestedColumns = new String[]{PHDBHandler.COLUMN_PHARTICLE_ID, PHDBHandler.COLUMN_TITLE, PHDBHandler.COLUMN_CONTENT, PHDBHandler.COLUMN_ARTICLE_LASTACCESS};


        Log.i(TAG, Integer.toString(FuzzySearch.ratio("drift",
                "drifting")));
        Log.i(TAG, "SEARCH WORD: " + searchWord);
        String selection = PHDBHandler.COLUMN_TITLE +
                " LIKE ? OR " + PHDBHandler.COLUMN_TITLE + " LIKE ? OR "
                + PHDBHandler.COLUMN_TITLE + " LIKE ? ";

        String[] selArgs = new String[]{"%" + searchWord + "%",
                searchWord + "%",
                "%" + searchWord};


        Cursor cursor, cursorAll;


        //FUZZY SEARCH START
        if (searchWord != null && searchWord != "" && searchWord.length() > 0) {
            cursorAll = database.rawQuery("select * from " + PHDBHandler.TABLE_PHARTICLE, null);

            for (cursorAll.moveToFirst(); !cursorAll.isAfterLast(); cursorAll.moveToNext()) {
                // do what you need with the cursor here

                String columnTitle = cursorAll.getString(cursorAll.getColumnIndex(PHDBHandler.COLUMN_TITLE));
                Long columnID = cursorAll.getLong(cursorAll.getColumnIndex(PHDBHandler.COLUMN_PHARTICLE_ID));
                String columnContent = cursorAll.getString(cursorAll.getColumnIndex(PHDBHandler.COLUMN_CONTENT));
                String dateTimeString = cursorAll.getString(cursorAll.getColumnIndex(PHDBHandler.COLUMN_ARTICLE_LASTACCESS));
                Timestamp timestamp = Timestamp.valueOf(dateTimeString);


                try {
                    Date date = dateFormat.parse(dateTimeString);
                    if (FuzzySearch.ratio(columnTitle, searchWord) > 60
                            || FuzzySearch.tokenSetRatio(columnTitle, searchWord) > 85 ||
                            FuzzySearch.weightedRatio(columnTitle, searchWord) > 85
                            || FuzzySearch.partialRatio(columnTitle, searchWord) > 85
                            || FuzzySearch.tokenSortRatio(columnTitle, searchWord) > 85) {

                        if(!pageIdList.contains(columnID)) {
                            pageIdList.add(columnID);
                            articleArrayList.add(new PHArticle(columnID, columnTitle, columnContent, timestamp));
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            cursorAll.close();
        }
        //FUZZY SEARCH END

        String searchQuery = "'*" + searchWord + "*'";
        if (searchWord == "" || searchWord == null || searchWord.isEmpty() || searchWord.length() == 0) {
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
                if(!pageIdList.contains(columnID)) {
                    PHArticle ph = new PHArticle(columnID, columnTitle, columnContent, timestamp);
                    articleArrayList.add(ph);
                    pageIdList.add(columnID);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        cursor.close();
        //Set<PHArticle> removeDups = new LinkedHashSet<>(articleArrayList);
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
                        Log.i(TAG, "page = " + page.toString());
                        String title = page.getString("title");
                        Long pageId = page.getLong("pageid");
                        Log.d(TAG, "pageid = " + pageId);
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
                    }
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public void pageCleaner() {
        //*************************
        //TODO:
        //Parse content to make it pretty and presentable

        for (PHArticle phArticle : washrack) {
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
    public String stringCleaner(String content) {
        for (int i = 0; i < content.length(); i++) {
            //get rid of stub date
            //ex: in article "Check in at the Royal National Hotel

            //delete stub dates
            //TODO
            //ex: "buy a scale" {{stub|date=2016-08-18}}
            //{{Stub|date=2014-04-12}}

            if((i+1 < content.length()) && content.charAt(i) == '{' && content.charAt(i+1) == '{')
            {
                int numUnmatchedBrackets = 2;
                int j = i+1;
                while(numUnmatchedBrackets > 0 && j < content.length())
                {
                    if(content.charAt(j) == '}')
                    {
                        numUnmatchedBrackets--;
                    }
                    j++;
                }

                String string1 = content.substring(0, i);
                String string2 = content.substring(j);
                content = string1+string2;

            }

            //delete ref tags
            //TODO: NEEDS WORK, some tags are still being written
            //check the "being a drifter" page
            if ((i + "<ref>".length() < content.length()) && content.substring(i, i + "<ref>".length()).equals("<ref>")) {
                int j = i + "<ref>".length();
                /*
                while ((j+"</ref>".length() < content.length()) &&  !content.substring(j, j+"</ref>".length()).equals("</ref>") )
                {
                    j++;
                }
                */
                char char1 = content.charAt(j);
                while (j < content.length() && content.charAt(j) != '>') {
                    j++;
                    char1 = content.charAt(j);
                }
                /*
                if(content.substring(j, j+"</ref>".length()).equals("</ref>"))
                {
                    String string1 = content.substring(0, i);
                    String string2 = content.substring(j+"</ref>".length());
                    content = string1 + string2;
                }
                */
                if (content.charAt(j) == '>') {
                    j++;
                    String string1 = content.substring(0, i);
                    String string2 = content.substring(j);
                    content = string1 + string2;
                }
            }


            /*
            //delete everything that is in [[trash]]
            int leftBracketNum = 0;
            //int rightBracketNum = 0;
            if(content.charAt(i) == '[')
            {
                leftBracketNum++;
                int j = i+1;
                while(leftBracketNum > 0)
                {
                    if(content.charAt(j) == '[')
                    {
                        leftBracketNum++;
                    }
                    else if(content.charAt(j) == ']')
                    {
                        leftBracketNum--;
                    }
                    j++;
                }
                String string1 = content.substring(0, i);
                String string2 = content.substring(j);
                string1 += "\n";
                content = string1 + string2;
            }
            */
            //{{reflist}}

            //get ride of [[Image: ...]]
            //for example: [[Image:Convince People You Are a local step1.jpg|center]]
            if (i < content.length() && content.charAt(i) == '[' && i + 1 < content.length() && content.charAt(i + 1) == '[') {
                String s1 = content.substring(i + 2, i + 2 + ("Image".length()));
                if (s1.equals("Image")) {
                    int j = i + 2 + ("Image".length());
                    //find the first ']'
                    while (j < content.length() && content.charAt(j) != ']') {
                        j++;
                    }
                    j = j + 2;
                    //Log.d("Editing", "string = " + content.substring(i,j));
                    String firstPart = content.substring(0, i);
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
    public void addArticleToWashRack(PHArticle phArticle) {
        washrack.add(phArticle);
    }

}
