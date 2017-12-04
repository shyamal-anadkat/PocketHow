package edu.wisc.ece.pockethow.frontend;

import android.Manifest;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Array;
import java.net.URI;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import edu.wisc.ece.pockethow.R;
import edu.wisc.ece.pockethow.dbHandler.PHDBHandler;
import edu.wisc.ece.pockethow.dbOperations.DbOperations;
import edu.wisc.ece.pockethow.entity.CategoryIcon;
import edu.wisc.ece.pockethow.entity.PHArticle;
import edu.wisc.ece.pockethow.entity.PHCategory;

public class CategorySelectionActivity extends AppCompatActivity {

    private GridviewAdapter mAdapter;
    private ArrayList<CategoryIcon> listCategories;
    private GridView gridView;
    private DownloadManager dlm;
    private BroadcastReceiver downloadReceiver;
    private long downloadId = 0;
    //temp for testing purposes
    private ArrayList<String> listLabel;
    private int globalposition = 0;
    private ArrayList<Integer> categoryIdList;
    private int globalCategoryId;
    private ArrayList<String> downloadedDatabaseNameList;
    private ArrayList<String> pathList;
    private int numCategoriesSelected = 0;
    private int numCategoriesDownloaded = 0;
    private ArrayList<Long> downloadIdList = new ArrayList<>();
    public final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    //private ArrayList<URI> uriArrayList = new ArrayList<>();
    //

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
                for (CategoryIcon icon : listCategories )
                {
                    if(icon.isChecked()) {
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
                            downloadId = dlm.enqueue(request);
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
        setContentView(R.layout.activity_category_selection);
        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

        requestPermissions(permissions, 43); //write_request_code?
        //requestPermissions(permissions, WRITE_REQUEST_CODE);
        //requestPermissions();
        prepareList();
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);

        dlm = this.getSystemService(DownloadManager.class);

        downloadReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {

            //check if the broadcast message is for our enqueued download
            long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if(downloadId == referenceId){

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
                            uriString =  uriString.substring(7);
                        }

                        File file = new File(uriString);
                        if(file.exists())
                        {
                            Log.d("addCategory", "huzzah");
                        }
                        else
                        {
                            Log.d("addCategory", "fuck this shit");
                        }
                        try
                        {
                            DbOperations dbOperations = new DbOperations(CategorySelectionActivity.this);
                            SQLiteDatabase downloadedDB = SQLiteDatabase.openDatabase(file.getPath(), null, 0);
                            //I think we are prioritizing speed over memory space
                            downloadedDB.execSQL("BEGIN TRANSACTION");
                            Cursor cursorPharticle = downloadedDB.rawQuery("select * from " + PHDBHandler.TABLE_PHARTICLE, null);
                            Cursor cursorCategory = downloadedDB.rawQuery("select * from " + PHDBHandler.TABLE_CATEGORY_TO_PAGEID, null);
                            Cursor cursorSearchWord = downloadedDB.rawQuery("select * from " + PHDBHandler.searchWordTable, null);
                            downloadedDB.execSQL("END TRANSACTION");
                            //downloadedDB.close();
                            dbOperations.open();
                            if(dbOperations.getDatabase() == null)
                            {
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
                                    String pageIdList = cursorCategory.getString(cursorCategory.getColumnIndex(PHDBHandler.COLUMN_ARTICLE_LASTACCESS));
                                    String dateTimeString = cursorCategory.getString(cursorCategory.getColumnIndex(PHDBHandler.COLUMN_ARTICLE_LASTACCESS));
                                    Timestamp timestamp = Timestamp.valueOf(dateTimeString);
                                    //TODO: Make insert query
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
                        }
                        catch(Exception e)
                        {

                            e.printStackTrace();
                            Log.d("addCategory", "ERROR");
                        }

                    }
                }
                cursor.close();



                Toast toast = Toast.makeText(CategorySelectionActivity.this,
                        "Download Complete", Toast.LENGTH_LONG);
                toast.show();
                downloadIdList.add(downloadId);
                Uri uri = dlm.getUriForDownloadedFile(referenceId);
                File file = new File(uri.getPath());
                pathList.add(file.getAbsolutePath());



                //ArrayList<String> selectedCategories = new ArrayList<>();
                //selectedCategories.add("Arts and Entertainment");
                //categoryIdList.add(listCategories.get(globalposition).Icon);
                numCategoriesDownloaded++;
                if(numCategoriesDownloaded == numCategoriesSelected)
                {
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
        };
        registerReceiver(downloadReceiver, filter);

        // prepared arraylist and passed it to the Adapter class
        mAdapter = new GridviewAdapter(this, listCategories, getResources().getColor(R.color.colorPrimary));

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

    //temp for testing

    public void makeRequests() {
        ArrayList<String> selectedCategories = new ArrayList<>();

        selectedCategories.add(listLabel.get(globalposition));
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

        //
        listLabel = new ArrayList<>();
        listLabel.add("Arts and Entertainment");
        listLabel.add("Cars & Other Vehicles");
        listLabel.add("Education and Communications");
        listLabel.add("Computers and Electronics");
        listLabel.add("Family Life");
        listLabel.add("Finance and Business");
        listLabel.add("Food and Entertaining");
        listLabel.add("Home and Garden");
        listLabel.add("Health");
        listLabel.add("Hobbies and Crafts");
        listLabel.add("Holidays and Traditions");
        listLabel.add("Personal Care and Style");
        listLabel.add("Pets and Animals");
        listLabel.add("Relationships");
        listLabel.add("Philosophy and Religion");
        listLabel.add("Sports and Fitness");
        listLabel.add("Travel");
        listLabel.add("Wikihow");
        listLabel.add("Work World");
        listLabel.add("Youth");
        //
        categoryIdList = new ArrayList<>();
        downloadedDatabaseNameList = new ArrayList<>();
        pathList = new ArrayList<>();
        listCategories.add(new CategoryIcon(R.drawable.arts_entertainment, "Arts", "https://storage.googleapis.com/pockethow-database-archive/art.db", "art.db"));
        listCategories.add(new CategoryIcon(R.drawable.automotive, "Auto"));
        listCategories.add(new CategoryIcon(R.drawable.education, "Education"));
        listCategories.add(new CategoryIcon(R.drawable.elec, "Electronics"));
        listCategories.add(new CategoryIcon(R.drawable.family, "Family"));
        listCategories.add(new CategoryIcon(R.drawable.finance, "Finance"));
        listCategories.add(new CategoryIcon(R.drawable.food, "Food"));
        listCategories.add(new CategoryIcon(R.drawable.garden, "Garden"));
        listCategories.add(new CategoryIcon(R.drawable.health, "Health"));
        listCategories.add(new CategoryIcon(R.drawable.hobbies, "Hobbies"));
        listCategories.add(new CategoryIcon(R.drawable.holidays, "Holidays"));
        listCategories.add(new CategoryIcon(R.drawable.personal_care, "Personal Care"));
        listCategories.add(new CategoryIcon(R.drawable.pets, "Pets"));
        listCategories.add(new CategoryIcon(R.drawable.relationships, "Relationships"));
        listCategories.add(new CategoryIcon(R.drawable.religion, "Religion"));
        listCategories.add(new CategoryIcon(R.drawable.sports, "Sports"));
        listCategories.add(new CategoryIcon(R.drawable.travel, "Travel"));
        listCategories.add(new CategoryIcon(R.drawable.wikihow, "Wikihow"));
        listCategories.add(new CategoryIcon(R.drawable.work, "Work"));
        listCategories.add(new CategoryIcon(R.drawable.youth, "Youth"));
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
}
