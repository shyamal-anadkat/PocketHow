package edu.wisc.ece.pockethow.frontend;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import edu.wisc.ece.pockethow.R;
import edu.wisc.ece.pockethow.dbHandler.PHDBHandler;
import edu.wisc.ece.pockethow.dbOperations.DbOperations;
import edu.wisc.ece.pockethow.dbOperations.dbTester;
import edu.wisc.ece.pockethow.entity.PHArticle;
import edu.wisc.ece.pockethow.entity.PHCategory;
import edu.wisc.ece.pockethow.httpRequests.PHWikihowFetches;

/**
 * An activity representing a single Page detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link PageListActivity}.
 */
public class PageDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own detail action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // Show the Up button in the action bar.
        /*ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }*/

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(PageDetailFragment.ARG_ITEM_ID,
                    getIntent().getStringExtra(PageDetailFragment.ARG_ITEM_ID));
            PageDetailFragment fragment = new PageDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.page_detail_container, fragment)
                    .commit();
        }


        //*****ONLY FOR TESTING PURPOSES*****//

        final DbOperations dbOperations;
        dbOperations = new DbOperations(this);
        this.deleteDatabase("PocketHow.db");

        //***testing***//
        new Thread(new Runnable() {
            public void run() {
                final PHWikihowFetches phWikihowFetches = new PHWikihowFetches();

                dbOperations.open();

                List<String> testIDs = phWikihowFetches.fetchPagesFromCategory("Travel", 5);
                dbOperations.addCategoryToPageID(new PHCategory(2, "Travel"
                        , phWikihowFetches.categoryListToDelimString(testIDs),
                        null));

                Log.i("DetailActivity", dbOperations.getPageIds("Travel"));

                dbOperations.parsePagesAndPopulateDB(phWikihowFetches.getJSONFromURL
                        (phWikihowFetches.getFetchURLFromPageIds
                                (testIDs)));
                dbOperations.close();

            }
        }).start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpTo(this, new Intent(this, PageListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
