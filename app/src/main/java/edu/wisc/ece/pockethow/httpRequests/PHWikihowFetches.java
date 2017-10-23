/* Author: Shyamal Anadkat */
package edu.wisc.ece.pockethow.httpRequests;

import android.util.Log;

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

/*** Created by zosta on 10/15/2017.*/

public class PHWikihowFetches {

    public PHWikihowFetches() {}
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
                Log.e(TAG, "Response:" + line);
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


    /* Fetch the list of PageIds given the Category and numPages to limit fetch to */
    public List<String> fetchPagesFromCategory(String category, int numPages) {
        ArrayList<String> pageIds = new ArrayList<>();
        final String categoryFetchURL = "https://www.wikihow.com/api.php?" +
                "action=query&format=json" +
                "&list=categorymembers" +
                "&cmtitle=Category%3A" + category +
                "&cmlimit=" + numPages;
        if (category == null || category.isEmpty()) return null;
        String jsonStr = new PHttpHandler().makeServiceCall(categoryFetchURL);
        if (jsonStr != null) {
            try {

                JSONObject jsonObject = new JSONObject(jsonStr);
                JSONObject query = jsonObject.getJSONObject("query");
                JSONArray categories = query.getJSONArray("categorymembers");

                for (int i = 0; i < categories.length(); i++) {
                    pageIds.add(categories.getJSONObject(i).get("pageid").toString());
                }
                Log.e(TAG, pageIds.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return pageIds;
    }

    /* SAMPLE - WIP */
    public JSONObject getJSONFromURL(String url_in) {
        PHttpHandler ph = new PHttpHandler();
        String jsonStr = ph.makeServiceCall(url_in);
        JSONObject jsonObject = null;
        Log.e(TAG, "Response from url: " + jsonStr);
        if (jsonStr != null) {
            try {
                jsonObject = new JSONObject(jsonStr);
                //JSONObject pages = jsonObject.getJSONObject("query").getJSONObject("pages");
                //JSONObject page = pages.getJSONObject("262356");
                //JSONArray revisions = page.getJSONArray("revisions");
                //JSONObject firstRev = revisions.getJSONObject(0);
                //String content = firstRev.get("*").toString();
            } catch (Exception je) {
                je.printStackTrace();
            }
            return jsonObject;
        }
        return jsonObject;
    }
}
