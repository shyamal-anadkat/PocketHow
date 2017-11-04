package edu.wisc.ece.pockethow.frontend;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import edu.wisc.ece.pockethow.R;

public class CategorySelectionActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    //private ArrayList<String> categoryArray = new ArrayList<>();
    public final String categoryTravel = "Travel";
    public final String categoryHealth = "Health";
    public final String categoryCars = "Cars and Other Vehicles";

    private String[] categoryArray = new String[]{ categoryCars, categoryHealth, categoryTravel};
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_selection);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new CardAdapter(categoryArray);
        mRecyclerView.setAdapter(mAdapter);
    }
}
