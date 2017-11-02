package edu.wisc.ece.pockethow.frontend;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.List;

import edu.wisc.ece.pockethow.R;
import edu.wisc.ece.pockethow.dbOperations.DbOperations;
import edu.wisc.ece.pockethow.entity.PHCategory;
import edu.wisc.ece.pockethow.httpRequests.PHWikihowFetches;

public class searchActivity extends AppCompatActivity {
    Button button;
    SearchView searchView;
    //private DbOperations dbOperations;
    TextView loadingTextView;
    Button populateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        final DbOperations dbOperations;
        dbOperations = new DbOperations(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        button = (Button) findViewById(R.id.main_search_btn);
        searchView = (SearchView) findViewById(R.id.main_search_bar);
        searchView.setSubmitButtonEnabled(true);
        populateButton = (Button) findViewById(R.id.populateDBbutton);
        loadingTextView = (TextView) findViewById(R.id.textViewLoading);
        //dbOperations = new DbOperations(this); //this is a context

        //dbOperations.open();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingTextView.setVisibility(View.VISIBLE);
                Intent intent = new Intent(searchActivity.this, PageListActivity.class);
                Log.d("searchActivity", searchView.getQuery().toString());
                intent.putExtra("message", searchView.getQuery().toString());
                startActivity(intent);
                //startActivity(new Intent(searchActivity.this, PageDetailActivity.class));
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                loadingTextView.setVisibility(View.VISIBLE);
                Intent intent = new Intent(searchActivity.this, PageListActivity.class);
                Log.d("searchActivity", searchView.getQuery().toString());
                intent.putExtra("message", searchView.getQuery().toString());
                startActivity(intent);
                //startActivity(new Intent(searchActivity.this, PageDetailActivity.class));
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        populateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDatabase("PocketHow.db");
                new Thread(new Runnable() {
                    public void run() {
                        final PHWikihowFetches phWikihowFetches = new PHWikihowFetches();

                        dbOperations.open();

                        List<String> testIDs = phWikihowFetches.fetchPagesFromCategory("Travel", 100);
                        dbOperations.addCategoryToPageID(new PHCategory(2, "Travel"
                                , phWikihowFetches.categoryListToDelimString(testIDs),
                                null));

                        List<String> testIDs1 = phWikihowFetches.fetchPagesFromCategory("Physics", 100);
                        dbOperations.addCategoryToPageID(new PHCategory(3, "Physics"
                                , phWikihowFetches.categoryListToDelimString(testIDs1),
                                null));

                        Log.i("DetailActivity", dbOperations.getPageIds("Travel"));

                        dbOperations.parsePagesAndPopulateDB(phWikihowFetches.getJSONFromURL
                                (phWikihowFetches.getFetchURLFromPageIds
                                        (testIDs)));

                        dbOperations.parsePagesAndPopulateDB(phWikihowFetches.getJSONFromURL
                                (phWikihowFetches.getFetchURLFromPageIds
                                        (testIDs1)));
                        dbOperations.close();

                    }
                }).start();

            }
        });

        /*
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingTextView.setVisibility(View.VISIBLE);
                Intent intent = new Intent(searchActivity.this, PageListActivity.class);
                Log.d("searchActivity", searchView.getQuery().toString());
                intent.putExtra("message", searchView.getQuery().toString());
                startActivity(intent);
                //startActivity(new Intent(searchActivity.this, PageDetailActivity.class));
            }
        });
        */
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
