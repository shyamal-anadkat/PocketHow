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
import org.jsoup.nodes.Document;

import java.nio.DoubleBuffer;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import edu.wisc.ece.pockethow.contentParser.markupParser;
import edu.wisc.ece.pockethow.dbHandler.PHDBHandler;
import edu.wisc.ece.pockethow.entity.PHArticle;
import edu.wisc.ece.pockethow.entity.PHCategory;
import edu.wisc.ece.pockethow.httpRequests.PHWikihowFetches;
import me.xdrop.fuzzywuzzy.FuzzySearch;

import static edu.wisc.ece.pockethow.dbHandler.PHDBHandler.extraColumn;
import static edu.wisc.ece.pockethow.dbHandler.PHDBHandler.searchWordColumn;
import static edu.wisc.ece.pockethow.dbHandler.PHDBHandler.searchWordTable;

/*** PocketHow, (@C) 2017 ***/
public class DbOperations {

    // set the format to sql date time
    public final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final String TAG = "DB_OPERATIONS";

    SQLiteOpenHelper dbHandler;
    SQLiteDatabase database;
    markupParser markupParser = new markupParser();

    //*******
    //for cleaning pages
    public static ArrayList<PHArticle> washrack = new ArrayList<>();
    //******
/*
    searchWordList for spelling correction
 */
    public ArrayList<String> searchWordList = new ArrayList<>();

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


    /**
     * Add Category to CategoryToPageID schema
     *
     * @param category
     * @return
     */
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

    /**
     * Query PageIds for a particular Category
     *
     * @param category
     * @return delim comma pageIds
     */
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

    /**
     * Query Article Results for Search Word
     * Using MATCH Sqlite(FTS4) functionality and
     * Fuzzy Search enhancements
     *
     * @param searchWord
     * @return
     */
    public ArrayList<PHArticle> getArticle(String searchWord) {
        Cursor cursor, cursorAll;
        ArrayList<Long> pageIdList = new ArrayList<>();
        ArrayList<PHArticle> articleArrayList = new ArrayList<PHArticle>();

        Log.i(TAG, "SEARCH WORD: " + searchWord);


        //*** @Deprecated Selection and selArgs below ***//
        String selection = PHDBHandler.COLUMN_TITLE +
                " LIKE ? OR " + PHDBHandler.COLUMN_TITLE + " LIKE ? OR "
                + PHDBHandler.COLUMN_TITLE + " LIKE ? ";
        String[] selArgs = new String[]{"%" + searchWord + "%",
                searchWord + "%",
                "%" + searchWord};


        //*****FUZZY SEARCH START*****//
        if (searchWord != null && searchWord != "" && searchWord.length() > 0) {
            cursorAll = database.rawQuery("select * from " + PHDBHandler.TABLE_PHARTICLE, null);

            for (cursorAll.moveToFirst(); !cursorAll.isAfterLast(); cursorAll.moveToNext()) {

                // do what you need with the cursor here
                try {
                    String columnTitle = cursorAll.getString(cursorAll.getColumnIndex(PHDBHandler.COLUMN_TITLE));
                    Long columnID = cursorAll.getLong(cursorAll.getColumnIndex(PHDBHandler.COLUMN_PHARTICLE_ID));
                    String columnContent = cursorAll.getString(cursorAll.getColumnIndex(PHDBHandler.COLUMN_CONTENT));
                    String dateTimeString = cursorAll.getString(cursorAll.getColumnIndex(PHDBHandler.COLUMN_ARTICLE_LASTACCESS));
                    Timestamp timestamp = Timestamp.valueOf(dateTimeString);

                    Date date = dateFormat.parse(dateTimeString);
                    if (FuzzySearch.ratio(columnTitle, searchWord) > 60
                            || FuzzySearch.tokenSetRatio(columnTitle, searchWord) > 85 ||
                            FuzzySearch.weightedRatio(columnTitle, searchWord) > 85
                            || FuzzySearch.partialRatio(columnTitle, searchWord) > 85
                            || FuzzySearch.tokenSortRatio(columnTitle, searchWord) > 85) {

                        if (!pageIdList.contains(columnID)) {
                            pageIdList.add(columnID);
                            articleArrayList.add(new PHArticle(columnID, columnTitle, columnContent, timestamp));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            cursorAll.close();
        }
        //*****FUZZY SEARCH END*****//

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
            try {
                Long columnID = cursor.getLong(cursor.getColumnIndex(PHDBHandler.COLUMN_PHARTICLE_ID));
                String columnTitle = cursor.getString(cursor.getColumnIndex(PHDBHandler.COLUMN_TITLE));
                String columnContent = cursor.getString(cursor.getColumnIndex(PHDBHandler.COLUMN_CONTENT));
                String dateTimeString = cursor.getString(cursor.getColumnIndex(PHDBHandler.COLUMN_ARTICLE_LASTACCESS));
                Timestamp timestamp = Timestamp.valueOf(dateTimeString);

                Date date = dateFormat.parse(dateTimeString);
                if (!pageIdList.contains(columnID)) {
                    PHArticle ph = new PHArticle(columnID, columnTitle, columnContent, timestamp);
                    articleArrayList.add(ph);
                    pageIdList.add(columnID);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        cursor.close();
        return articleArrayList;
    }


    /**
     * Populate database with articles
     *
     * @param jsonObject
     */
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


                        /// cleanup ///
                        Document doc = markupParser.getDocFromString(content);
                        // remove all links
                        doc.select("a").remove();
                        content = doc.toString();



                        if (!title.contains("Category:") && !title.contains("wikiHow:")) {
                            PHArticle phArticle = new PHArticle(pageId, title,
                                    content,
                                    new Timestamp(System.currentTimeMillis()));
                            //addArticle(phArticle);
                            //Note: attempts to prettify the pages while this method is running creates unusable pages,
                            //so add the pages to a global ArrayList, then parse the PHArticles' contents to clean them,
                            //then add them to database.
                            washrack.add(phArticle);
                        }
                        Log.i(TAG, title);
                        Log.i(TAG, content);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * @TODO
     */
    public void pageCleaner() {
        //*************************
        //Parse content to make it pretty and presentable

        long time = System.currentTimeMillis();

        //begin SQL transaction to make sequential SQL statements faster
        //Cuts the run time by half. from 1099 milliseconds to 513 milliseconds for Arts and Entertainment
        database.execSQL("BEGIN TRANSACTION");
        for (PHArticle phArticle : washrack) {
            String content = phArticle.getContent();
            phArticle.setContent(markupParser.stringCleaner(content));
            addArticle(phArticle);
        }
        //end SQL transaction
        database.execSQL("END TRANSACTION");
        washrack.clear();
        long time2 = System.currentTimeMillis();
        Log.d(TAG, "Time in milliseconds = " + Long.toString(time2 - time));
    }


    public boolean isOpen() {
        return database.isOpen();
    }

    /**
     * Given that the washrack array list is not empty after downloading documents,
     * parse every article title for unique words and add them to a SQL table full of
     * title words. Then, load the table into a global array list of title words
     *
     * @return
     */
    public void populateSearchWordTable() {
        database.execSQL("BEGIN TRANSACTION");
        for (PHArticle phArticle : washrack) {
            addArticleTitleToSearchWordTable(phArticle);
        }
        database.execSQL("END TRANSACTION");
        String sqlGetSearchWords = "SELECT * FROM " + searchWordList;
        String[] col = new String[1];
        col[0] = searchWordColumn;
        Cursor cursor = database.query(searchWordTable, col, null, null, null, null, null);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            searchWordList.add(cursor.getString(cursor.getColumnIndex(searchWordColumn)));
        }
    }

    /**
     * @param phArticle
     */
    public void addArticleTitleToSearchWordTable(PHArticle phArticle) {
        addSearchWord(phArticle.getTitle());
    }

    /**
     * @param word
     */
    public void addSearchWord(String word) {
        String[] arr = word.split(" ");

        for (String ss : arr) {
            ContentValues contentValues = new ContentValues();
            ss = ss.replace("'", "`");
            contentValues.put(searchWordColumn, ss);
            database.insertWithOnConflict(searchWordTable,
                    null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        }
    }


    /**
     * Load the search words from the database into a global array list
     *
     * @return
     */
    public ArrayList<String> getSearchWords() {
        Cursor cursor = database.rawQuery("select * from " + searchWordTable, null);
        ArrayList<String> returnValue = new ArrayList<>();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

            // do what you need with the cursor here
            try {
                String columnID = cursor.getString(cursor.getColumnIndex(searchWordColumn));
                returnValue.add(columnID);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        cursor.close();
        return returnValue;
    }

    /**
     * get closest established search word via Levenshtein Distance
     *
     * @param input
     * @return
     */
    public String getClosestSearchWord(String input) {
        if (searchWordList.size() == 0) {
            searchWordList = getSearchWords();
        }
        int ratio = 0;
        String output = input;
        int newRatio = 0;
        for (String searchWord : searchWordList) {
            if (searchWord.equals(input)) {
                return input;
            } else {
                newRatio = FuzzySearch.ratio(input, searchWord);
                if (ratio < newRatio) {
                    ratio = newRatio;
                    output = searchWord;
                }
            }
        }
        return output;
    }

    public SQLiteDatabase getDatabase() {
        return database;
    }

    /**
     * used for testing only
     *
     * @param phArticle
     */
    public void addArticleToWashRack(PHArticle phArticle) {
        washrack.add(phArticle);
    }

}
