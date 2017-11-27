package edu.wisc.ece.pockethow.frontend;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import edu.wisc.ece.pockethow.R;
import edu.wisc.ece.pockethow.dbHandler.PHDBHandler;
import edu.wisc.ece.pockethow.dbOperations.DbOperations;
import edu.wisc.ece.pockethow.entity.PHCategory;
import edu.wisc.ece.pockethow.httpRequests.PHWikihowFetches;

public class searchActivity extends AppCompatActivity {
    Button button;
    EditText searchEditText;
    TextView loadingTextView;
    ImageButton imageButton;
    Button deleteButton;
    static final String codeword = "catagory";
    static final String categoryIntIdCodeword = "categoryIntId";
    static final String filenameCodeword = "filename";
    static final String downloadedParentPath = "/data/user/0/com.android.providers.downloads/cache/";
    //int categoryIdGlobal;
    ArrayList<Integer> categoryIdList = new ArrayList<>();
    ArrayList<String> categoryArrayList = new ArrayList<>();
    ArrayList<String> downloadedFilePathList = new ArrayList<>();
    final DbOperations dbOperations = new DbOperations(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        button = (Button) findViewById(R.id.main_search_btn);
        searchEditText = (EditText) findViewById(R.id.main_search_bar);
        loadingTextView = (TextView) findViewById(R.id.textViewLoading);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingTextView.setVisibility(View.VISIBLE);
                Context context = searchActivity.this;
                File dbFile = context.getDatabasePath("PocketHow.db");
                if(dbFile.exists())
                {
                    Intent intent = new Intent(searchActivity.this, PageListActivity.class);
                    Log.d("searchActivity", searchEditText.getText().toString());
                /*
                TODO: searching door should allow for "Indoor" and "Door", but it only returns "Indoor"
                high priority
                 */
                /*
                TODO: "heal" turns into "deal" instead of "health"
                high priority
                 */
                /*
                TODO: prioritize articles that have more matched items
                low priority
                For example, "nut health" should have the "nut health" article on top instead of "health..." and then "nut health"
                 */
                    String inputString = "";
                    //intent.putExtra("message", dbOperations.getClosestSearchWord(searchEditText.getText().toString()));
                    String originalString = searchEditText.getText().toString();
                    String[] tokenArray = originalString.split(" ");
                    for (int i = 0; i < tokenArray.length; i++) {
                    /*
                    check for 's and delete them
                    for example: nut's becomes nut
                     */
                        String tempInput = tokenArray[i];
                        for (int j = 0; j < tempInput.length(); j++) {
                            if (tempInput.charAt(j) == '\'' && (j + 1) < tempInput.length() && tempInput.charAt(j + 1) == 's') {
                                tempInput = tempInput.substring(0, j);
                                j = tempInput.length();
                            }
                        }
                        inputString += dbOperations.getClosestSearchWord(tempInput) + " ";
                    }
                    Log.d("searchActivity", "input string = " + inputString);
                    intent.putExtra("message", dbOperations.getClosestSearchWord(inputString));
                    if (dbOperations.isOpen()) {
                        Toast.makeText(searchActivity.this, "Please wait, the database is loading",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Log.d("searchActivity", "Done loading DB");
                        startActivity(intent);
                    }
                    //startActivity(new Intent(searchActivity.this, PageDetailActivity.class));
                }
                else
                {
                    Toast.makeText(searchActivity.this, "Database does not exist. Please download a category",
                            Toast.LENGTH_LONG).show();
                }

            }
        });

        imageButton = (ImageButton) findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View v) {
                                               button.performClick();
                                           }
                                       }
        );

        deleteButton = (Button) findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteDatabase("PocketHow.db");
            }
        });
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        categoryArrayList = bundle.getStringArrayList(codeword);
        //categoryIdGlobal = bundle.getInt(categoryIntIdCodeword);
        categoryIdList = bundle.getIntegerArrayList(categoryIntIdCodeword);
        downloadedFilePathList = bundle.getStringArrayList(filenameCodeword);
        if(downloadedFilePathList != null && downloadedFilePathList.size() != 0)
        {
            /*
            for(String downloadedFilePath: downloadedFilePathList)
            {
                downloadedFilePath = downloadedParentPath + downloadedFilePath;
            }
            */
            //take the name of the database and add the filepath to it
            for(int i = 0; i < downloadedFilePathList.size(); i++)
            {
                downloadedFilePathList.set(i, downloadedParentPath + downloadedFilePathList.get(i));
            }
        }
        //deleteDatabase("PocketHow.db");
        populateDB();

    }

    public void populateDB() {
        //deleteDatabase("PocketHow.db");
        dbOperations.searchWordList.clear();
        new Thread(new Runnable() {
            public void run() {
                if(downloadedFilePathList == null || downloadedFilePathList.size() == 0) {
                    //fetch and parse WikiHow pages
                    final PHWikihowFetches phWikihowFetches = new PHWikihowFetches();

                    dbOperations.open();
                        /*
                        List<String> testIDs = phWikihowFetches.fetchPagesFromCategory(categoryStr, 100);
                        dbOperations.addCategoryToPageID(new PHCategory(2, categoryStr
                                , phWikihowFetches.categoryListToDelimString(testIDs),
                                null));
                        dbOperations.parsePagesAndPopulateDB(phWikihowFetches.getJSONFromURL
                                (phWikihowFetches.getFetchURLFromPageIds
                                        (testIDs)));
                                        */
                    //for (String categoryStr : categoryArrayList) {
                    for (int i = 0; i < categoryArrayList.size(); i++) {
                        String categoryStr = categoryArrayList.get(i);
                        int categoryIdGlobal = categoryIdList.get(i);
                        List<String> testIDs = phWikihowFetches.fetchPagesFromCategory(categoryStr, 100);
                        dbOperations.addCategoryToPageID(new PHCategory(categoryIdGlobal, categoryStr
                                , phWikihowFetches.categoryListToDelimString(testIDs),
                                null));

                        //// supports upto 100 article requests ////
                        if (testIDs.size() > 50) {
                            List<String> temp = new ArrayList<>(testIDs.subList(0, 50));
                            dbOperations.parsePagesAndPopulateDB(phWikihowFetches.getJSONFromURL
                                    (phWikihowFetches.getFetchURLFromPageIds
                                            (temp)));
                            List<String> temp1 = new ArrayList<>(testIDs.subList(50, testIDs.size()));
                            dbOperations.parsePagesAndPopulateDB(phWikihowFetches.getJSONFromURL
                                    (phWikihowFetches.getFetchURLFromPageIds
                                            (temp1)));

                        } else {
                            dbOperations.parsePagesAndPopulateDB(phWikihowFetches.getJSONFromURL
                                    (phWikihowFetches.getFetchURLFromPageIds
                                            (testIDs)));
                        }
                    }
                    dbOperations.populateSearchWordTable();
                    dbOperations.pageCleaner();
                    dbOperations.close();
                    categoryArrayList.clear();
                }
                else //Add downloaded db file into PocketHow.db
                {
                    if(downloadedFilePathList != null) {
                        for (String downloadedFilePath : downloadedFilePathList) {
                            //SQLiteDatabase db = SQLiteDatabase.openDatabase(downloadedFilePath, null, 0);
                            String sql = "ATTACH DATABASE '" + downloadedFilePath + "' as 'DownloadedAlias'";
                            dbOperations.getDatabase().execSQL(sql);

                            //sql = "INSERT INTO X.TABLE SELECT * FROM Y.TABLE";
                            sql = "INSERT INTO PocketHow." + PHDBHandler.TABLE_PHARTICLE + " SELECT * FROM DownloadedAlias." + PHDBHandler.TABLE_PHARTICLE;
                            dbOperations.getDatabase().execSQL(sql);
                            sql = "INSERT INTO PocketHow." + PHDBHandler.TABLE_CATEGORY_TO_PAGEID + " SELECT * FROM DownloadedAlias." + PHDBHandler.TABLE_CATEGORY_TO_PAGEID;
                            dbOperations.getDatabase().execSQL(sql);
                            sql = "INSERT INTO PocketHow." + PHDBHandler.searchWordTable + " SELECT * FROM DownloadedAlias." + PHDBHandler.searchWordTable;
                            dbOperations.getDatabase().execSQL(sql);
                            sql = "DETACH DATABASE 'DownloadedAlias'";
                            dbOperations.getDatabase().execSQL(sql);
                        }
                        downloadedFilePathList.clear();
                    }
                }
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //dbOperations.open();
        loadingTextView.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //dbOperations.close();
    }
}
