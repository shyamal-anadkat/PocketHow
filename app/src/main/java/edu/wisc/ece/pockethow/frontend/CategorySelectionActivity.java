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

import edu.wisc.ece.pockethow.ImageAdapter;
import edu.wisc.ece.pockethow.R;
import edu.wisc.ece.pockethow.entity.CategoryIcon;

public class CategorySelectionActivity extends AppCompatActivity {

    private GridviewAdapter mAdapter;
    private ArrayList<CategoryIcon> listCategories;
    private GridView gridView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_selection);

        prepareList();

        // prepared arraylist and passed it to the Adapter class
        mAdapter = new GridviewAdapter(this,listCategories);

        // Set custom adapter to gridview
        gridView = (GridView) findViewById(R.id.gridView1);
        gridView.setAdapter(mAdapter);

        // Implement On Item click listener
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {
                Toast.makeText(CategorySelectionActivity.this, mAdapter.getItem(position).Label, Toast.LENGTH_SHORT).show();
                Context context = gridView.getContext();

                mAdapter.getItem(position).toggleChecked();

            }
        });

    }

    public void prepareList()
    {
        listCategories = new ArrayList<CategoryIcon>();

        listCategories.add(new CategoryIcon(R.drawable.arts_entertainment,"Arts"));
        listCategories.add(new CategoryIcon(R.drawable.automotive,"Auto"));
        listCategories.add(new CategoryIcon(R.drawable.education,"Education"));
        listCategories.add(new CategoryIcon(R.drawable.elec,"Electronics"));
        listCategories.add(new CategoryIcon(R.drawable.family,"Family"));
        listCategories.add(new CategoryIcon(R.drawable.finance,"Finance"));
        listCategories.add(new CategoryIcon(R.drawable.food,"Food"));
        listCategories.add(new CategoryIcon(R.drawable.garden,"Garden"));
        listCategories.add(new CategoryIcon(R.drawable.health,"Health"));
        listCategories.add(new CategoryIcon(R.drawable.hobbies,"Hobbies"));
        listCategories.add(new CategoryIcon(R.drawable.holidays,"Holidays"));
        listCategories.add(new CategoryIcon(R.drawable.personal_care,"Personal Care"));
        listCategories.add(new CategoryIcon(R.drawable.pets,"Pets"));
        listCategories.add(new CategoryIcon(R.drawable.relationships,"Relationships"));
        listCategories.add(new CategoryIcon(R.drawable.religion,"Religion"));
        listCategories.add(new CategoryIcon(R.drawable.sports,"Sports"));
        listCategories.add(new CategoryIcon(R.drawable.travel,"Travel"));
        listCategories.add(new CategoryIcon(R.drawable.wikihow,"Wikihow"));
        listCategories.add(new CategoryIcon(R.drawable.work,"Work"));
        listCategories.add(new CategoryIcon(R.drawable.youth,"Youth"));
    }

}
