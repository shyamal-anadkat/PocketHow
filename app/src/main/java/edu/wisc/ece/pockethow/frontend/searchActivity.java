package edu.wisc.ece.pockethow.frontend;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.SearcherManager;

import java.util.List;

import edu.wisc.ece.pockethow.LuceneSearch;
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

    public static final SearcherManager getSearcherManager(){return searcherManager;}
    public static final IndexWriter getIndexWriter(){return indexWriter;}
    private static SearcherManager searcherManager=null;
    private static IndexWriter indexWriter=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //pick the properties from user preferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Analyzer analyzer= new SimpleAnalyzer(Version.LUCENE_41);

        IndexWriterConfig config=new IndexWriterConfig(Version.LUCENE_41,analyzer);
        //pick the buffer size from property
        String memorySize=preferences.getString("lucene_memory_size","5.0");
        config.setRAMBufferSizeMB(Double.valueOf(memorySize));
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        //create index on external directory under lucene folder
        File path= new File(getApplicationContext().getExternalFilesDir(null),"lucene");
        try {
            Directory directory= FSDirectory.open(path);
            indexWriter=new IndexWriter(directory,config);
            boolean applyAllDeletes = true;
            //no need to warm the search
            searcherManager = new SearcherManager(indexWriter, applyAllDeletes, null);
        } catch (IOException e) {
            Log.e(tag,"Error occurred while opening indexWriter/SearcherManager"+ e.getMessage(),e);
        }
    }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                loadingTextView.setVisibility(View.VISIBLE);
                Intent intent = new Intent(searchActivity.this, PageListActivity.class);
                Log.d("searchActivity", searchView.getQuery().toString());
                intent.putExtra("message", searchView.getQuery().toString());
                startActivity(intent);
                //startActivity(new Intent(searchActivity.this, PageDetailActivity.class));
                */
                LuceneSearch.runLucene();
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

                        List<String> testIDs = phWikihowFetches.fetchPagesFromCategory("Travel", 20);
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
