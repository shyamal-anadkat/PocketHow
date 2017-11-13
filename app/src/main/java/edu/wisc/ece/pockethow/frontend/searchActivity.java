package edu.wisc.ece.pockethow.frontend;

import android.content.Intent;
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

import java.util.ArrayList;
import java.util.List;

import edu.wisc.ece.pockethow.R;
import edu.wisc.ece.pockethow.dbOperations.DbOperations;
import edu.wisc.ece.pockethow.entity.PHCategory;
import edu.wisc.ece.pockethow.httpRequests.PHWikihowFetches;

public class searchActivity extends AppCompatActivity {
    Button button;
    EditText searchEditText;
    //private DbOperations dbOperations;
    TextView loadingTextView;
    ImageButton imageButton;
    //Button populateButton;
    //String categoryStr;

    static final String codeword = "catagory";
    ArrayList<String> categoryArrayList = new ArrayList<>();

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
                Intent intent = new Intent(searchActivity.this, PageListActivity.class);
                Log.d("searchActivity", searchEditText.getText().toString());
                intent.putExtra("message", searchEditText.getText().toString());
                if(dbOperations.isOpen())
                {
                    Toast.makeText(searchActivity.this, "Please wait, the database is loading",
                            Toast.LENGTH_LONG).show();
                }
                else {
                    Log.d("searchActivity", "Done loading DB");
                    startActivity(intent);
                }
                //startActivity(new Intent(searchActivity.this, PageDetailActivity.class));
            }
        });

        imageButton = (ImageButton) findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View v) {
                                               loadingTextView.setVisibility(View.VISIBLE);
                                               Intent intent = new Intent(searchActivity.this, PageListActivity.class);
                                               Log.d("searchActivity", searchEditText.getText().toString());
                                               intent.putExtra("message", searchEditText.getText().toString());
                                               if(dbOperations.isOpen())
                                               {
                                                   Toast.makeText(searchActivity.this, "Please wait, the database is loading",
                                                           Toast.LENGTH_LONG).show();
                                               }
                                               else {
                                                   Log.d("searchActivity", "Done loading DB");
                                                   startActivity(intent);
                                               }
                                           }
                                       }
        );

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        categoryArrayList = bundle.getStringArrayList(codeword);
        populateDB();



    }
    public void populateDB()
    {
        deleteDatabase("PocketHow.db");
        new Thread(new Runnable() {
            public void run() {
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
                for(String categoryStr: categoryArrayList)
                {
                    List<String> testIDs = phWikihowFetches.fetchPagesFromCategory(categoryStr, 100);
                    dbOperations.addCategoryToPageID(new PHCategory(2, categoryStr
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
                dbOperations.pageCleaner();
                dbOperations.close();
                categoryArrayList.clear();

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
