package edu.wisc.ece.pockethow.dbOperations;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.joda.time.DateTime;

import java.io.File;
import java.lang.reflect.Array;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import edu.wisc.ece.pockethow.R;
import edu.wisc.ece.pockethow.entity.PHArticle;

public class dbTester extends AppCompatActivity {
    private ArrayAdapter<String> mAdapter;
    private ArrayList<String> FOLKS = new ArrayList<String>();
    private ArrayList<PHArticle> officialArticleList = new ArrayList<>();
    private String searchString = "";
    private DbOperations dbOperations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db_tester);

        final EditText editText = (EditText) findViewById(R.id.plain_text_input);
        Button searchButton = (Button) findViewById(R.id.searchButton);
        ListView listView = (ListView) findViewById(R.id.listView);
        dbOperations = new DbOperations(this); //this is a context
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                searchString = editText.getText().toString();
                ArrayList<PHArticle> articleArrayList = dbOperations.getArticle(searchString);
                FOLKS.clear();
                for (int i = 0; i < articleArrayList.size(); i++) {
                    officialArticleList.add(articleArrayList.get(i));
                    FOLKS.add(articleArrayList.get(i).getTitle());
                }
                mAdapter.notifyDataSetChanged();
            }
        });
        //R.layout.list_item is solely for testing purposes
        mAdapter = new ArrayAdapter<String>(this,
                R.layout.list_item, FOLKS);
        AdapterView.OnItemClickListener mListener = new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

            }
        };
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(mListener);

        //PHArticle is fine

        //open is fine
        this.deleteDatabase("PocketHow.db");
        dbOperations.open();
        dbOperations.addArticle(new PHArticle(1, "chicken", "The chicken (Gallus gallus domesticus) is a type of domesticated fowl, a subspecies of the red junglefowl. It is one of the most common and widespread domestic animals, with a total population of more than 19 billion as of 2011.[1] Humans keep chickens primarily as a source of food, consuming both their meat and their eggs.\n" +
                "Genetic studies have pointed to multiple maternal origins in Southeast Asia, East Asia,[2] and South Asia, but with the clade found in the Americas, Europe, the Middle East and Africa originating in the Indian subcontinent. From India, the domesticated chicken was imported to Lydia in western Asia Minor, and to Greece by the fifth century BC.[3] Fowl had been known in Egypt since the mid-15th century BC, with the \"bird that gives birth every day\" having come to Egypt from the land between Syria and Shinar, Babylonia, according to the annals of Thutmose III.", new Timestamp(System.currentTimeMillis())));
        dbOperations.addArticle(new PHArticle(2, "pig", "A pig is any of the animals in the genus Sus, within the even-toed ungulate family Suidae. Pigs include the domestic pig and its ancestor, the common Eurasian wild boar (Sus scrofa), along with other species; related creatures outside the genus include the peccary, the babirusa, and the warthog. Pigs, like all suids, are native to the Eurasian and African continents. Juvenile pigs are known as piglets.[1] Pigs are highly social and intelligent animals.[2]\n" +
                "With around 1 billion individuals alive at any time, the domestic pig is among the most populous large mammals in the world.[3][4] Pigs are omnivores and can consume a wide range of food, similar to humans.[5] Pigs can harbour a range of parasites and diseases that can be transmitted to humans. Because of the similarities between pigs and humans, pigs are used for human medical research", new Timestamp(System.currentTimeMillis()))
        );
        dbOperations.addArticle(new PHArticle(3, "goat", "The domestic goat (Capra aegagrus hircus) is a subspecies of goat domesticated from the wild goat of southwest Asia and Eastern Europe.\n" +
                "The goat is a member of the family Bovidae and is closely related to the sheep as both are in the goat-antelope subfamily Caprinae. There are over 300 distinct breeds of goat.[1] Goats are one of the oldest domesticated species, and have been used for their milk, meat, hair, and skins over much of the world.[2] In 2011, there were more than 924 million live goats around the globe, according to the UN Food and Agriculture Organization.[3]\n" +
                "Female goats are referred to as \"does\" or \"nannies;\" intact males are called \"bucks\" or \"billies;\" and juveniles of both sexes are called \"kids\". Castrated males are called \"wethers\". Goat meat from younger animals is called \"kid\" or cabrito (Spanish), while meat from older animals is known simply as \"goat\" or sometimes called chevon, or in some areas \"mutton\" (which more often refers to adult sheep meat).", new Timestamp(System.currentTimeMillis()))
        );

        dbOperations.addArticle(new PHArticle(4, "cream cheese", "hi", new Timestamp(System.currentTimeMillis())));
        dbOperations.addArticle(new PHArticle(5, "title", "content", new Timestamp(System.currentTimeMillis())));
    }

  /*  @Override
    protected void onResume()
    {
        super.onResume();
        dbOperations.open();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        dbOperations.close();
    }
*/

}
