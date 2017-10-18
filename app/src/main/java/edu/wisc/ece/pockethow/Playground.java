package edu.wisc.ece.pockethow;

import android.util.Log;

import org.json.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/*** Created by zosta on 10/15/2017.*/

public class Playground {

    static final String TAG = Playground.class.getSimpleName();
    @Deprecated
    //use methods from PHttpHandler instead
    String fetchFromURLToStringResponse(String url_in) {
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
                //simply print out the response to output stream
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

    void parseJsonFromURL(String url_in) {
        PHttpHandler ph = new PHttpHandler();
        String jsonStr = ph.makeServiceCall(url_in);
        Log.e(TAG, "Response from url: " + jsonStr);
        if(jsonStr != null){
            try {
                //JSONObject jsonObject = new JSONObject(jsonStr);
                //JSONArray pages = jsonObject.getJSONArray("query");
                //System.out.print(pages.toString());
                //Log.e(TAG, jsonStr);


            } catch(Exception je) {
                je.printStackTrace();
            }
        }

    }


}
