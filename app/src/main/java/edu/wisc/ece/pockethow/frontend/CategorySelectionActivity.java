package edu.wisc.ece.pockethow.frontend;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;

import edu.wisc.ece.pockethow.frontend.ImageAdapter;
import edu.wisc.ece.pockethow.R;

public class CategorySelectionActivity extends AppCompatActivity {

    private GridviewAdapter mAdapter;
    private ArrayList<String> listLabel;
    private ArrayList<Integer> listIcon;
    private ArrayList<String> selectedCategories = new ArrayList<>();
    private GridView gridView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_selection);

        prepareList();

        // prepared arraylist and passed it to the Adapter class
        mAdapter = new GridviewAdapter(this,listLabel, listIcon);

        // Set custom adapter to gridview
        gridView = (GridView) findViewById(R.id.gridView1);
        gridView.setAdapter(mAdapter);

        // Implement On Item click listener
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {
                Toast.makeText(CategorySelectionActivity.this, mAdapter.getItem(position), Toast.LENGTH_SHORT).show();
                Context context = gridView.getContext();
                Intent intent = new Intent(context, searchActivity.class);
                //intent.putExtra(PageDetailFragment.ARG_ITEM_ID, Long.toString(holder.mItem.getID()));
                //send the content of the selected article
                String string = mAdapter.getItem(position);
                //intent.putExtra(searchActivity.codeword, mAdapter.getItem(position));

                /*********temporary*/
                selectedCategories.add(string);
                /**********temporary*/
                intent.putStringArrayListExtra(searchActivity.codeword, selectedCategories);
                //intent.putExtra(PHDBHandler.COLUMN_CONTENT, holder.mView.mContextView.toString());
                context.startActivity(intent);
                /**********temporary*/
                selectedCategories.clear();
                /*******temporary*/
            }
        });

    }

    public void prepareList()
    {
        listLabel = new ArrayList<String>();
        /*
        listLabel.add("Arts");
        listLabel.add("Auto");
        listLabel.add("Education");
        listLabel.add("Electronics");
        listLabel.add("Family");
        listLabel.add("Finance");
        listLabel.add("Food");
        listLabel.add("Garden");
        listLabel.add("Health");
        listLabel.add("Hobbies");
        listLabel.add("Holidays");
        listLabel.add("Personal Care");
        listLabel.add("Pets");
        listLabel.add("Relationships");
        listLabel.add("Religion");
        listLabel.add("Sports");
        listLabel.add("Travel");
        listLabel.add("Wikihow");
        listLabel.add("Work");
        listLabel.add("Youth");
        */

        //This list corresponds to the Categories exactly, but it looks ugly
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


        listIcon = new ArrayList<Integer>();
        listIcon.add(R.drawable.arts_entertainment);
        listIcon.add(R.drawable.automotive);
        listIcon.add(R.drawable.education);
        listIcon.add(R.drawable.elec);
        listIcon.add(R.drawable.family);
        listIcon.add(R.drawable.finance);
        listIcon.add(R.drawable.food);
        listIcon.add(R.drawable.garden);
        listIcon.add(R.drawable.health);
        listIcon.add(R.drawable.hobbies);
        listIcon.add(R.drawable.holidays);
        listIcon.add(R.drawable.personal_care);
        listIcon.add(R.drawable.pets);
        listIcon.add(R.drawable.relationships);
        listIcon.add(R.drawable.religion);
        listIcon.add(R.drawable.sports);
        listIcon.add(R.drawable.travel);
        listIcon.add(R.drawable.wikihow);
        listIcon.add(R.drawable.work);
        listIcon.add(R.drawable.youth);
    }

}
