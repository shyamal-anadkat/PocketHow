package edu.wisc.ece.pockethow.frontend;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;


import edu.wisc.ece.pockethow.R;
import edu.wisc.ece.pockethow.dbOperations.DbOperations;
import edu.wisc.ece.pockethow.dummyContent.DummyContent;
import edu.wisc.ece.pockethow.entity.PHArticle;

import java.util.ArrayList;
import java.util.List;

/**
 * An activity representing a list of Pages. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link PageDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class PageListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    DbOperations dbOperations;
    String searchStr;
    ArrayList<PHArticle> searchResults;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        //toolbar.setTitle(getTitle());

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        searchStr = bundle.getString("message");
        if(searchStr == null)
        {
            searchStr = "";
        }
        Log.d("PageListActivity", "Search for: " + searchStr);
        //ImageButton imageButton = (ImageButton) findViewById(R.id.searchImageButton);
        //imageButton.setVisibility(View.VISIBLE);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        View recyclerView = findViewById(R.id.page_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.page_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
        dbOperations = new DbOperations(this);
        searchResults = new ArrayList<>(); //PHArticle arraylist to store the results of search query
        final SearchView searchView = (SearchView) findViewById(R.id.pageListSearchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //make DB query
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        searchStr = searchView.getQuery().toString();
                        searchResults.clear();
                        searchResults = dbOperations.getArticle(searchStr);
                        //solely for testing purposes
                        Log.d("PageListActivity", "Search for: " + searchStr);
                        if (searchResults.size() == 0)
                        {
                            Log.d("PageListActivity", "retrieved 0 articles");
                        }
                        else {
                            for (int i = 0; i < searchResults.size(); i++) {
                                Log.d("PageListActivity", "retrived article: " + searchResults.get(i).getTitle());
                            }
                        }
                    }
                }).start();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        searchView.setSubmitButtonEnabled(true);
        //dbOperations.open();
        //TODO: might not even be necessary

        Thread openDbOperationsThread = new Thread(new Runnable() {
            @Override
            public void run() {

                dbOperations.open();
                searchResults.clear();
                searchResults = dbOperations.getArticle(searchStr);
                //solely for testing purposes
                if (searchResults.size() == 0)
                {
                    Log.d("PageListActivity", "retrieved 0 articles");
                }
                for (int i = 0; i < searchResults.size(); i++)
                {
                    Log.d("PageListActivity", "retrived article: " + searchResults.get(i).getTitle());
                }
            }
        });
        openDbOperationsThread.start();

    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(DummyContent.ITEMS));
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<DummyContent.DummyItem> mValues;

        public SimpleItemRecyclerViewAdapter(List<DummyContent.DummyItem> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.page_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mIdView.setText(mValues.get(position).id);
            holder.mContentView.setText(mValues.get(position).content);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(PageDetailFragment.ARG_ITEM_ID, holder.mItem.id);
                        PageDetailFragment fragment = new PageDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.page_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, PageDetailActivity.class);
                        intent.putExtra(PageDetailFragment.ARG_ITEM_ID, holder.mItem.id);

                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIdView;
            public final TextView mContentView;
            public DummyContent.DummyItem mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }
}
