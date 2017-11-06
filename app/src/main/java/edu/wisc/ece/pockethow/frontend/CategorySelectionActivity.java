package edu.wisc.ece.pockethow.frontend;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import edu.wisc.ece.pockethow.ImageAdapter;
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
        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new ImageAdapter(this));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(CategorySelectionActivity.this, "" + position,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

}
