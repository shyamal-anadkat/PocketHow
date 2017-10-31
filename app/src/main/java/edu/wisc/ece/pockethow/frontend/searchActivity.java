package edu.wisc.ece.pockethow.frontend;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;

import edu.wisc.ece.pockethow.R;

public class searchActivity extends AppCompatActivity {
    Button button;
    SearchView searchView;
    //private DbOperations dbOperations;
    TextView loadingTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        button = (Button) findViewById(R.id.main_search_btn);
        searchView = (SearchView) findViewById(R.id.main_search_bar);
        searchView.setSubmitButtonEnabled(true);

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
    protected void onResume()
    {
        super.onResume();
        //dbOperations.open();
        loadingTextView.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        //dbOperations.close();
    }

}
