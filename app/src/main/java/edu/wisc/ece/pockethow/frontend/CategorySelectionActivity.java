package edu.wisc.ece.pockethow.frontend;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;

import edu.wisc.ece.pockethow.R;
import edu.wisc.ece.pockethow.entity.CategoryIcon;

public class CategorySelectionActivity extends AppCompatActivity {

    private GridviewAdapter mAdapter;
    private ArrayList<CategoryIcon> listCategories;
    private GridView gridView;
    private DownloadManager dlm;
    private BroadcastReceiver downloadReceiver;
    private long downloadId = 0;
    //temp for testing purposes
    private ArrayList<String> listLabel;
    private int globalposition = 0;
    private ArrayList<Integer> categoryIdList;
    private int globalCategoryId;
    private ArrayList<String> downloadedDatabaseNameList;

    //

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_download:
                Context context = gridView.getContext();
                for (CategoryIcon icon : listCategories )
                {
                    if(icon.isChecked()) {
                        Uri uri = icon.getUri();
                        if (uri != null) {
                            DownloadManager.Request request = new DownloadManager.Request(uri);
                            request.setTitle("Archive Download: " + icon.Label);
                            Toast toast = Toast.makeText(CategorySelectionActivity.this,
                                    "Download Started: " + icon.Label, Toast.LENGTH_LONG);
                            toast.show();
                            downloadId = dlm.enqueue(request);
                        }
                    }
                }
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_selection);

        prepareList();

        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);

        dlm = this.getSystemService(DownloadManager.class);

        downloadReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {

            //check if the broadcast message is for our enqueued download
            long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if(downloadId == referenceId){
                Toast toast = Toast.makeText(CategorySelectionActivity.this,
                        "Download Complete", Toast.LENGTH_LONG);
                toast.show();
                ArrayList<String> selectedCategories = new ArrayList<>();
                selectedCategories.add("Arts and Entertainment");
                categoryIdList.add(listCategories.get(globalposition).Icon);
                Intent goToNextActivity = new Intent(getApplicationContext(), searchActivity.class);
                goToNextActivity.putStringArrayListExtra(searchActivity.codeword, selectedCategories);
                //goToNextActivity.putExtra(searchActivity.categoryIntIdCodeword, listCategories.get(globalposition).Icon);
                goToNextActivity.putIntegerArrayListExtra(searchActivity.categoryIntIdCodeword, categoryIdList);
                goToNextActivity.putStringArrayListExtra(searchActivity.filenameCodeword, downloadedDatabaseNameList);
                startActivity(goToNextActivity);
                categoryIdList.clear();
            }
            }
        };
        registerReceiver(downloadReceiver, filter);

        // prepared arraylist and passed it to the Adapter class
        mAdapter = new GridviewAdapter(this, listCategories, getResources().getColor(R.color.colorPrimary));

        // Set custom adapter to gridview
        gridView = (GridView) findViewById(R.id.gridView1);
        gridView.setAdapter(mAdapter);

        // Implement On Item click listener
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {
                listCategories.get(position).toggleChecked();
                mAdapter.notifyDataSetChanged();
            }
        });

    }

    //temp for testing

    public void makeRequests() {
        ArrayList<String> selectedCategories = new ArrayList<>();

        selectedCategories.add(listLabel.get(globalposition));
        categoryIdList.add(listCategories.get(globalposition).Icon);
        Intent goToNextActivity = new Intent(getApplicationContext(), searchActivity.class);
        goToNextActivity.putStringArrayListExtra(searchActivity.codeword, selectedCategories);
        //goToNextActivity.putExtra(searchActivity.categoryIntIdCodeword, listCategories.get(globalposition).Icon);
        goToNextActivity.putIntegerArrayListExtra(searchActivity.categoryIntIdCodeword, categoryIdList);
        startActivity(goToNextActivity);
        selectedCategories.clear();
        categoryIdList.clear();
    }

    //
    public void prepareList() {
        listCategories = new ArrayList<CategoryIcon>();

        //
        listLabel = new ArrayList<>();
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
        //
        categoryIdList = new ArrayList<>();
        downloadedDatabaseNameList = new ArrayList<>();

        listCategories.add(new CategoryIcon(R.drawable.arts_entertainment, "Arts", "https://storage.googleapis.com/pockethow-database-archive/art.db"));
        listCategories.add(new CategoryIcon(R.drawable.automotive, "Auto"));
        listCategories.add(new CategoryIcon(R.drawable.education, "Education"));
        listCategories.add(new CategoryIcon(R.drawable.elec, "Electronics"));
        listCategories.add(new CategoryIcon(R.drawable.family, "Family"));
        listCategories.add(new CategoryIcon(R.drawable.finance, "Finance"));
        listCategories.add(new CategoryIcon(R.drawable.food, "Food"));
        listCategories.add(new CategoryIcon(R.drawable.garden, "Garden"));
        listCategories.add(new CategoryIcon(R.drawable.health, "Health"));
        listCategories.add(new CategoryIcon(R.drawable.hobbies, "Hobbies"));
        listCategories.add(new CategoryIcon(R.drawable.holidays, "Holidays"));
        listCategories.add(new CategoryIcon(R.drawable.personal_care, "Personal Care"));
        listCategories.add(new CategoryIcon(R.drawable.pets, "Pets"));
        listCategories.add(new CategoryIcon(R.drawable.relationships, "Relationships"));
        listCategories.add(new CategoryIcon(R.drawable.religion, "Religion"));
        listCategories.add(new CategoryIcon(R.drawable.sports, "Sports"));
        listCategories.add(new CategoryIcon(R.drawable.travel, "Travel"));
        listCategories.add(new CategoryIcon(R.drawable.wikihow, "Wikihow"));
        listCategories.add(new CategoryIcon(R.drawable.work, "Work"));
        listCategories.add(new CategoryIcon(R.drawable.youth, "Youth"));
    }

}
