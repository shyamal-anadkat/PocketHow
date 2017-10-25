package edu.wisc.ece.pockethow.frontend;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;

import edu.wisc.ece.pockethow.DatabaseTable;
import edu.wisc.ece.pockethow.R;

public class SearchActivity extends AppCompatActivity {
    private DatabaseTable db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        db = new DatabaseTable(getApplicationContext());

        Button addName = (Button) findViewById(R.id.searchButton);
        addName.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Cursor c = db.getWordMatches("vacuum",null);
            }
        });
    }

}
