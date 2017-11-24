package edu.wisc.ece.pockethow.frontend;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;

import edu.wisc.ece.pockethow.frontend.ImageAdapter;
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
    private int globalCategoryId;
    //
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
                    Intent goToNextActivity = new Intent(getApplicationContext(), searchActivity.class);
                    goToNextActivity.putStringArrayListExtra(searchActivity.codeword, selectedCategories);
                    goToNextActivity.putExtra(searchActivity.categoryIntIdCodeword, listCategories.get(globalposition).Icon);
                    startActivity(goToNextActivity);
                }
            }
        };
        registerReceiver(downloadReceiver, filter);

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
                //Toast.makeText(CategorySelectionActivity.this, mAdapter.getItem(position).Label, Toast.LENGTH_SHORT).show();
                Context context = gridView.getContext();
                globalposition = position;
                Uri uri = listCategories.get(position).getUri();
                if(uri != null && downloadId == 0){
                    DownloadManager.Request request = new DownloadManager.Request(uri);
                    request.setTitle("Archive Download");
                    Toast toast = Toast.makeText(CategorySelectionActivity.this,
                            "Download Started", Toast.LENGTH_LONG);
                    toast.show();
                    downloadId = dlm.enqueue(request);
                }
                else //TODO: TEMP
                {
                    makeRequests();
                }
            }
        });

    }

    //temp for testing

    public void makeRequests()
    {
        ArrayList<String> selectedCategories = new ArrayList<>();

        selectedCategories.add(listLabel.get(globalposition));
        Intent goToNextActivity = new Intent(getApplicationContext(), searchActivity.class);
        goToNextActivity.putStringArrayListExtra(searchActivity.codeword, selectedCategories);
        goToNextActivity.putExtra(searchActivity.categoryIntIdCodeword, listCategories.get(globalposition).Icon);
        startActivity(goToNextActivity);
    }
    //
    public void prepareList()
    {
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


        listCategories.add(new CategoryIcon(R.drawable.arts_entertainment,"Arts","https://storage.googleapis.com/pockethow-database-archive/PocketHow.db"));
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
