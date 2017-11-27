package edu.wisc.ece.pockethow.frontend;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import edu.wisc.ece.pockethow.R;

/**
 * A fragment representing a single Page detail screen.
 * This fragment is either contained in a {@link PageListActivity}
 * in two-pane mode (on tablets) or a {@link PageDetailActivity}
 * on handsets.
 */
public class PageDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    public static final String ARG_ITEM_TITLE = "item_title";

    private String content;
    private String title;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PageDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            //mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
            Bundle bundle = getArguments();
            content = bundle.getString(ARG_ITEM_ID);
            title = bundle.getString(ARG_ITEM_TITLE);
            Activity activity = this.getActivity();
            /*CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.content);
            }*/
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.page_detail, container, false);

        if (content != null) {
            //String html = "<html><body>Hello, World!</body></html>";
            String mime = "text/html";
            String encoding = "utf-8";

            WebView myWebView = ((WebView) rootView.findViewById(R.id.page_detail));
            myWebView.getSettings().setJavaScriptEnabled(true);
            myWebView.loadDataWithBaseURL(null, content, mime, encoding, null);
            //((TextView) rootView.findViewById(R.id.page_detail)).setText(content);
        }

        //String title = rootView.getResources().getString(R.string.title_page_detail);
        if (title != null && title.length() > 0) {
            this.getActivity().setTitle(title);
        }
        /*
        // Show the dummy content as text in a TextView.
        if (mItem != null) {
            //((TextView) rootView.findViewById(R.id.page_detail)).setText(mItem.details);
        }
*/
        return rootView;
    }
}
/*
<TextView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/page_detail"
    style="?android:attr/textAppearanceLarge"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:padding="16dp"
    android:textIsSelectable="true"
    tools:context="edu.wisc.ece.pockethow.frontend.PageDetailFragment" />

 */