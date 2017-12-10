package edu.wisc.ece.pockethow.frontend;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;

import edu.wisc.ece.pockethow.R;
import edu.wisc.ece.pockethow.dbHandler.PHDBHandler;
import edu.wisc.ece.pockethow.dbOperations.DbOperations;
import edu.wisc.ece.pockethow.entity.CategoryIcon;
import edu.wisc.ece.pockethow.entity.PHArticle;
import edu.wisc.ece.pockethow.entity.PHCategory;

public class CategorySelectionActivity extends AppCompatActivity {

    private GridviewAdapter mAdapter;
    private ArrayList<CategoryIcon> listCategories;
    private ArrayList<CategoryIcon> categoryListPermanent = new ArrayList<>();
    private GridView gridView;
    private DownloadManager dlm;
    private BroadcastReceiver downloadReceiver;
    private int globalposition = 0;
    private ArrayList<Integer> categoryIdList;
    private ArrayList<String> downloadedDatabaseNameList;
    //private ArrayList<String> pathList;
    private int numCategoriesSelected = 0;
    private int numCategoriesDownloaded = 0;
    private ArrayList<Long> downloadIdList = new ArrayList<>();
    ArrayList<String> list = new ArrayList<>();
    public static Boolean deleteButtonPressed = false;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_download:
                Context context = gridView.getContext();
                list = fetchCurrentCategories();
                int numSelected = 0;
                for (CategoryIcon icon : listCategories) {
                    if (icon.isChecked()) {
                        numSelected++;
                    }
                }
                if (numSelected == 0) {
                    Intent goToNextActivity = new Intent(getApplicationContext(), searchActivity.class);
                    startActivity(goToNextActivity);
                } else {
                    for (CategoryIcon icon : listCategories) {
                        if (icon.isChecked() && !isInDatabase(icon.Icon)) {
                            Uri uri = icon.getUri();
                            if (uri != null) {
                                DownloadManager.Request request = new DownloadManager.Request(uri);
                                request.setTitle("Archive Download: " + icon.Label);
                                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, icon.getDatabaseName());
                                Toast toast = Toast.makeText(CategorySelectionActivity.this,
                                        "Download Started: " + icon.Label, Toast.LENGTH_LONG);
                                toast.show();
                                //TODO
                                //request.setDestinationInExternalFilesDir(CategorySelectionActivity.this, Environment.getExternalStorageDirectory().getAbsolutePath(), icon.getDatabaseName());
                                //pathList.add(Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator + icon.getDatabaseName());
                                numCategoriesSelected++;
                                //downloadId = dlm.enqueue(request);
                                //requestedIdList.add(dlm.enqueue(request));
                                icon.addDownloadId(dlm.enqueue(request));
                            }
                        }
                    }
                }
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  Declare a new thread to do a preference check
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //  Initialize SharedPreferences
                SharedPreferences getPrefs = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());

                //  Create a new boolean and preference and set it to true
                boolean isFirstStart = getPrefs.getBoolean("firstStart", true);

                //  If the activity has never started before...
                if (isFirstStart) {

                    //  Launch app intro
                    final Intent i = new Intent(CategorySelectionActivity.this, IntroActivity.class);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(i);
                        }
                    });

                    //  Make a new preferences editor
                    SharedPreferences.Editor e = getPrefs.edit();

                    //  Edit preference to make it false because we don't want this to run again
                    e.putBoolean("firstStart", false);

                    //  Apply changes
                    e.apply();
                }
            }
        });

        // Start the thread
        t.start();
        // unregisterReceiver(); ???
        setContentView(R.layout.activity_category_selection);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.action_bar)));
        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

        requestPermissions(permissions, 43); //write_request_code?
        //requestPermissions(permissions, WRITE_REQUEST_CODE);
        //requestPermissions();
        prepareList();

        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        list = fetchCurrentCategories();
        dlm = this.getSystemService(DownloadManager.class);

        downloadReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {

                //check if the broadcast message is for our enqueued download
                long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                //for (CategoryIcon categoryIcon : listCategories) {
                CategoryIcon tempIcon = null;
                int tempIndex = 0;
                for (int i = 0; i < listCategories.size(); i++) {
                    CategoryIcon categoryIcon = listCategories.get(i);
                    Long downloadId = categoryIcon.getDownloadId();


                    if (downloadId != null && downloadId == referenceId) {
                        tempIcon = categoryIcon;
                        listCategories.remove(i);
                        mAdapter.notifyDataSetChanged();
                        gridView.invalidateViews();
                        DownloadManager.Query query = new DownloadManager.Query();
                        query.setFilterById(downloadId);
                        Cursor cursor = dlm.query(query);
                        if (cursor.moveToFirst()) {
                            int downloadStatus = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                            String downloadLocalUri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                            String downloadMimeType = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_MEDIA_TYPE));
                            if ((downloadStatus == DownloadManager.STATUS_SUCCESSFUL) && downloadLocalUri != null) {
                                String uriString = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));

                                if (uriString.substring(0, 7).matches("file://")) {
                                    uriString = uriString.substring(7);
                                }

                                File file = new File(uriString);
                                if (file.exists()) {
                                    Log.d("addCategory", "huzzah");
                                } else {
                                    Log.d("addCategory", "fuck this shit");
                                }
                                try {
                                    DbOperations dbOperations = new DbOperations(CategorySelectionActivity.this);
                                    SQLiteDatabase downloadedDB = SQLiteDatabase.openDatabase(file.getPath(), null, 0);
                                    //I think we are prioritizing speed over memory space
                                    downloadedDB.execSQL("BEGIN TRANSACTION");
                                    Cursor cursorPharticle = downloadedDB.rawQuery("select * from " + PHDBHandler.TABLE_PHARTICLE, null);
                                    Cursor cursorCategory = downloadedDB.rawQuery("select * from " + PHDBHandler.TABLE_CATEGORY_TO_PAGEID, null);
                                    Cursor cursorSearchWord = downloadedDB.rawQuery("select * from " + PHDBHandler.searchWordTable, null);

                                    Log.d("CategorySelectionActivity", "# or rows in cursorPharticle = " + cursorPharticle.getCount());
                                    Log.d("CategorySelectionActivity", "# of rows in category = " + cursorCategory.getCount());
                                    Log.d("CategorySelectionActivity", "# of rows in searchWord = " + cursorSearchWord.getCount());

                                    downloadedDB.execSQL("END TRANSACTION");
                                    //downloadedDB.close();
                                    dbOperations.open();
                                    if (dbOperations.getDatabase() == null) {
                                        Log.d("addCategory", "dbOperations doesn't exist");
                                    }
                                    dbOperations.getDatabase().execSQL("BEGIN TRANSACTION");
                                    for (cursorPharticle.moveToFirst(); !cursorPharticle.isAfterLast(); cursorPharticle.moveToNext()) {
                                        try {
                                            Long columnID = cursorPharticle.getLong(cursorPharticle.getColumnIndex(PHDBHandler.COLUMN_PHARTICLE_ID));
                                            String columnTitle = cursorPharticle.getString(cursorPharticle.getColumnIndex(PHDBHandler.COLUMN_TITLE));
                                            byte[] columnContent = cursorPharticle.getBlob(cursorPharticle.getColumnIndex(PHDBHandler.COLUMN_CONTENT));
                                            String dateTimeString = cursorPharticle.getString(cursorPharticle.getColumnIndex(PHDBHandler.COLUMN_ARTICLE_LASTACCESS));
                                            Timestamp timestamp = Timestamp.valueOf(dateTimeString);
                                            //TODO: Make insert query
                                            dbOperations.addArticle(new PHArticle(columnID, columnTitle, columnContent, timestamp));
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    dbOperations.getDatabase().execSQL("END TRANSACTION");
                                    dbOperations.getDatabase().execSQL("BEGIN TRANSACTION");
                                    for (cursorCategory.moveToFirst(); !cursorCategory.isAfterLast(); cursorCategory.moveToNext()) {
                                        try {
                                            String category = cursorCategory.getString(cursorCategory.getColumnIndex(PHDBHandler.COLUMN_CATEGORY));
                                            int id = cursorCategory.getInt(cursorCategory.getColumnIndex(PHDBHandler.COLUMN_CATEGORY_ID));
                                            //TODO: issue: pageIdList was suppposed to be a string with commas in between numbers, but all punctuation disappeared
                                            //
                                            String pageIdList = cursorCategory.getString(cursorCategory.getColumnIndex(PHDBHandler.COLUMN_ARTICLE_LASTACCESS));
                                            String dateTimeString = cursorCategory.getString(cursorCategory.getColumnIndex(PHDBHandler.COLUMN_ARTICLE_LASTACCESS));

                                            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                                            //Timestamp timestamp = Timestamp.valueOf(dateTimeString);
                                            dbOperations.addCategoryToPageID(new PHCategory(id, category, pageIdList, timestamp));
                                            //dbOperations.addArticle(new PHArticle(columnID, columnTitle, columnContent, timestamp));
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    dbOperations.getDatabase().execSQL("END TRANSACTION");
                                    dbOperations.getDatabase().execSQL("BEGIN TRANSACTION");
                                    for (cursorSearchWord.moveToFirst(); !cursorSearchWord.isAfterLast(); cursorSearchWord.moveToNext()) {
                                        try {
                                            String word = cursorSearchWord.getString(cursorSearchWord.getColumnIndex(PHDBHandler.searchWordColumn));
                                            //TODO: Make insert query
                                            dbOperations.addSearchWord(word);
                                            //dbOperations.addArticle(new PHArticle(columnID, columnTitle, columnContent, timestamp));
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    dbOperations.getDatabase().execSQL("END TRANSACTION");
                                    dbOperations.close();
                                    i--;

                                } catch (Exception e) {
                                    listCategories.add(i, tempIcon);
                                    mAdapter.notifyDataSetChanged();
                                    gridView.invalidateViews();
                                    e.printStackTrace();
                                    Log.d("addCategory", "ERROR");
                                }

                            }
                        }
                        cursor.close();

/*
                Toast toast = Toast.makeText(CategorySelectionActivity.this,
                        "Download Complete", Toast.LENGTH_LONG);
                toast.show();
                downloadIdList.add(downloadId);
                Uri uri = dlm.getUriForDownloadedFile(referenceId);
                File file = new File(uri.getPath());
                pathList.add(file.getAbsolutePath());
*/

                        //ArrayList<String> selectedCategories = new ArrayList<>();
                        //selectedCategories.add("Arts and Entertainment");
                        //categoryIdList.add(listCategories.get(globalposition).Icon);
                        numCategoriesDownloaded++;
                        if (numCategoriesDownloaded == numCategoriesSelected) {
                            downloadIdList.clear();
                            Toast.makeText(CategorySelectionActivity.this, "Done downloading databases", Toast.LENGTH_SHORT).show();

                            Intent goToNextActivity = new Intent(getApplicationContext(), searchActivity.class);
                            startActivity(goToNextActivity);
                    /*
                    Intent goToNextActivity = new Intent(getApplicationContext(), searchActivity.class);
                    goToNextActivity.putExtra(searchActivity.databaseFromServer, true);
                    //goToNextActivity.putStringArrayListExtra(searchActivity.codeword, selectedCategories);
                    //goToNextActivity.putExtra(searchActivity.categoryIntIdCodeword, listCategories.get(globalposition).Icon);
                    //goToNextActivity.putIntegerArrayListExtra(searchActivity.categoryIntIdCodeword, categoryIdList);
                    //goToNextActivity.putStringArrayListExtra(searchActivity.filenameCodeword, downloadedDatabaseNameList);

                    goToNextActivity.putStringArrayListExtra(searchActivity.downloadDatabase, pathList);
                    startActivity(goToNextActivity);
                    categoryIdList.clear();
                    pathList.clear();
                    numCategoriesDownloaded=0;
                    numCategoriesSelected=0;
                    */
                        }

                    }
                }
            }
        };
        registerReceiver(downloadReceiver, filter);

        // prepared arraylist and passed it to the Adapter class
        mAdapter = new GridviewAdapter(this, listCategories, getResources().getColor(R.color.category_select));

        // Set custom adapter to gridview
        gridView = (GridView) findViewById(R.id.gridView1);
        gridView.setAdapter(mAdapter);

        // Implement On Item click listener
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {
                listCategories.get(position).toggleChecked();
                mAdapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public void onBackPressed() {
        return;
    }

    //temp for testing

    public void makeRequests() {
        ArrayList<String> selectedCategories = new ArrayList<>();

        selectedCategories.add(listCategories.get(globalposition).getDatabaseName());
        categoryIdList.add(listCategories.get(globalposition).Icon);
        Intent goToNextActivity = new Intent(getApplicationContext(), searchActivity.class);
        goToNextActivity.putStringArrayListExtra(searchActivity.codeword, selectedCategories);
        //goToNextActivity.putExtra(searchActivity.categoryIntIdCodeword, listCategories.get(globalposition).Icon);
        goToNextActivity.putExtra(searchActivity.databaseFromServer, false);
        goToNextActivity.putIntegerArrayListExtra(searchActivity.categoryIntIdCodeword, categoryIdList);
        startActivity(goToNextActivity);
        selectedCategories.clear();
        categoryIdList.clear();
    }

    //
    public void prepareList() {
        listCategories = new ArrayList<CategoryIcon>();
        categoryIdList = new ArrayList<>();
        downloadedDatabaseNameList = new ArrayList<>();
        //pathList = new ArrayList<>();
        categoryListPermanent.add(new CategoryIcon(R.drawable.arts_entertainment, "Arts", "https://storage.googleapis.com/pockethow-database-archive/art.db", "Arts and Entertainment"));
        categoryListPermanent.add(new CategoryIcon(R.drawable.automotive, "Auto", "https://storage.googleapis.com/pockethow-database-archive/auto.db", "Cars & Other Vehicles"));
        categoryListPermanent.add(new CategoryIcon(R.drawable.education, "Education", "https://storage.googleapis.com/pockethow-database-archive/education.db", "Education and Communications"));
        categoryListPermanent.add(new CategoryIcon(R.drawable.elec, "Electronics", "https://storage.googleapis.com/pockethow-database-archive/electronics.db", "Computers and Electronics"));
        categoryListPermanent.add(new CategoryIcon(R.drawable.family, "Family", "https://storage.googleapis.com/pockethow-database-archive/family.db", "Family Life"));
        categoryListPermanent.add(new CategoryIcon(R.drawable.finance, "Finance", "https://storage.googleapis.com/pockethow-database-archive/finance.db", "Finance and Business"));
        categoryListPermanent.add(new CategoryIcon(R.drawable.food, "Food", "https://storage.googleapis.com/pockethow-database-archive/food.db", "Food and Entertaining"));
        categoryListPermanent.add(new CategoryIcon(R.drawable.garden, "Garden", "https://storage.googleapis.com/pockethow-database-archive/garden.db", "Home and Garden"));
        categoryListPermanent.add(new CategoryIcon(R.drawable.health, "Health", "https://storage.googleapis.com/pockethow-database-archive/health.db", "Health"));
        categoryListPermanent.add(new CategoryIcon(R.drawable.hobbies, "Hobbies", "https://storage.googleapis.com/pockethow-database-archive/hobby.db", "Hobbies and Crafts"));
        categoryListPermanent.add(new CategoryIcon(R.drawable.holidays, "Holidays", "https://storage.googleapis.com/pockethow-database-archive/holiday.db", "Holidays and Traditions"));
        categoryListPermanent.add(new CategoryIcon(R.drawable.personal_care, "Personal Care", "https://storage.googleapis.com/pockethow-database-archive/personal_care.db", "Personal Care and Style"));
        categoryListPermanent.add(new CategoryIcon(R.drawable.pets, "Pets", "https://storage.googleapis.com/pockethow-database-archive/pets.db", "Pets and Animals"));
        categoryListPermanent.add(new CategoryIcon(R.drawable.relationships, "Relationships", "https://storage.googleapis.com/pockethow-database-archive/relationships.db", "Relationships"));
        categoryListPermanent.add(new CategoryIcon(R.drawable.religion, "Religion", "https://storage.googleapis.com/pockethow-database-archive/religion.db", "Philosophy and Religion"));
        categoryListPermanent.add(new CategoryIcon(R.drawable.sports, "Sports", "https://storage.googleapis.com/pockethow-database-archive/sports.db", "Sports and Fitness"));
        categoryListPermanent.add(new CategoryIcon(R.drawable.travel, "Travel", "https://storage.googleapis.com/pockethow-database-archive/travel.db", "Travel"));
        categoryListPermanent.add(new CategoryIcon(R.drawable.wikihow, "Wikihow", "https://storage.googleapis.com/pockethow-database-archive/wikihow.db", "Wikihow"));
        categoryListPermanent.add(new CategoryIcon(R.drawable.work, "Work", "https://storage.googleapis.com/pockethow-database-archive/work.db", "Work World"));
        categoryListPermanent.add(new CategoryIcon(R.drawable.youth, "Youth", "https://storage.googleapis.com/pockethow-database-archive/youth.db", "Youth"));
        list = fetchCurrentCategories();
        for (CategoryIcon c : categoryListPermanent) {
            if (!isInDatabase(c.Icon)) {
                listCategories.add(c);
            }
        }
    }


    @Override // android recommended class to handle permissions
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.d("permission", "granted");
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.uujm
                    Toast.makeText(CategorySelectionActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();

                    //app cannot function without this permission for now so close it...
                    onDestroy();
                }
                return;
            }

            // other 'case' line to check fosr other
            // permissions this app might request
        }
    }

    public Boolean isInDatabase(Integer id) {
        for (String item : list) {
            if (id.toString().equals(item)) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<String> fetchCurrentCategories() {
        ArrayList<String> databaseCategoryList = new ArrayList<>();
        DbOperations dbOperations = new DbOperations(CategorySelectionActivity.this);
        dbOperations.open();
        SQLiteDatabase database = dbOperations.getDatabase();
        Cursor cursorAll = database.rawQuery("select * from " + PHDBHandler.TABLE_CATEGORY_TO_PAGEID, null);

        Log.d("CategorySelectionActivity", "# of rows retrieved = " + cursorAll.getCount());
        for (cursorAll.moveToFirst(); !cursorAll.isAfterLast(); cursorAll.moveToNext()) {

            // do what you need with the cursor here
            try {
                String columnTitle = cursorAll.getString(cursorAll.getColumnIndex(PHDBHandler.COLUMN_CATEGORY_ID));
                //int categoryLong = Integer.getInteger(columnTitle);
                //databaseCategoryList.add(categoryLong);
                databaseCategoryList.add(columnTitle);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        cursorAll.close();
        return databaseCategoryList;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (deleteButtonPressed) {
            deleteButtonPressed = false;
            list.clear();
            listCategories.clear();
            for (CategoryIcon categoryIcon : categoryListPermanent) {
                categoryIcon.toggleReset();
                listCategories.add(categoryIcon);
            }
            mAdapter.notifyDataSetChanged();
            gridView.invalidateViews();
            gridView.setAdapter(mAdapter);
        }
        //list = fetchCurrentCategories();
    }
}