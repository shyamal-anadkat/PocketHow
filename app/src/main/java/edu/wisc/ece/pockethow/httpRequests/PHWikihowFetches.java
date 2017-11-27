package edu.wisc.ece.pockethow.httpRequests;

import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.json.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/*** PocketHow, (@C) 2017 ***/

/**
 * Handles outgoing get requests, specific to WIkiHow
 */
public class PHWikihowFetches {

    public PHWikihowFetches() {
    }
    static final String TAG = PHWikihowFetches.class.getSimpleName();

    @Deprecated
    public String fetchFromURLToStringResponse(String url_in) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL(url_in);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            //read the input stream
            InputStream stream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(stream));
            StringBuffer buffer = new StringBuffer();
            String line = "";
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
                Log.i(TAG, "Response:" + line);
            }
            return buffer.toString();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Construct get URL for Category, given numPages to retrieve
     *
     * @param category
     * @param numPages
     * @return
     */
    private String getCategoryResponseURL(String category, int numPages) {
        return "https://www.wikihow.com/api.php?" +
                "action=query&format=json" +
                "&list=categorymembers" +
                "&cmtitle=Category%3A" + category +
                "&cmlimit=" + numPages;
    }


    /**
     * Fetch the list of PageIds given the Category and numPages to limit fetch to
     *
     * @param category
     * @param numPages
     * @return List of PageIds
     */
    public List<String> fetchPagesFromCategory(String category, int numPages) {

        ArrayList<String> pageIds = new ArrayList<>(); //assuming no dups in api
        final String categoryFetchURL = getCategoryResponseURL(category, numPages);

        // edge cases, err handling
        if (category == null || category.isEmpty()) return null;

        //make service call to get response as string
        String jsonStr = new PHttpHandler().makeServiceCall(categoryFetchURL);
        if (jsonStr != null) {
            try {

                JSONObject jsonObject = new JSONObject(jsonStr);
                JSONObject query = jsonObject.getJSONObject("query");
                JSONArray categories = query.getJSONArray("categorymembers");

                for (int i = 0; i < categories.length(); i++) {
                    pageIds.add(categories.getJSONObject(i).get("pageid").toString());
                }

                Log.d(TAG, "pageIDs: " + pageIds.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return pageIds;
    }


    /**
     * Returns comma delim string
     *
     * @param in
     * @return
     */
    public String categoryListToDelimString(List<String> in) {
        String idList = in.toString();
        return idList.substring(1, idList.length() - 1).replace(", ", ",");
    }


    /**
     * Get fetch URL to retrieve pages for a list of PageIDs
     *
     * @param ids
     * @return
     */
    public String getFetchURLFromPageIds(List<String> ids) {
        String appendIDs = StringUtils.join(ids, "|");
        String retURL = "https://www.wikihow.com/api.php?action=query" +
                "&prop=revisions&rvprop=content&rvparse&format=json" +
                "&pageids=" + appendIDs;
        return retURL;
    }


    /**
     * Get JSONObj to work on from req. get url
     * Makes service call
     *
     * @param url_in
     * @return JSONObj
     */
    public JSONObject getJSONFromURL(String url_in) {
        PHttpHandler ph = new PHttpHandler();
        String jsonStr = ph.makeServiceCall(url_in);
        JSONObject jsonObject = null;
        Log.e(TAG, "Response from url: " + jsonStr);
        if (jsonStr != null) {
            try {
                jsonObject = new JSONObject(jsonStr);
            } catch (Exception je) {
                je.printStackTrace();
            }
            return jsonObject;
        }
        return jsonObject;
    }
}