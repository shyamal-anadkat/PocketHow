package edu.wisc.ece.pockethow.frontend;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import edu.wisc.ece.pockethow.R;


public class SplashScreen extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        Intent intent = null;

        try {
            if(isInternetAvailable())
                intent = new Intent(this, CategorySelectionActivity.class);
            else
                intent = new Intent(this, searchActivity.class);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        final Intent finalIntent = intent;

        Thread timer = new Thread() {
            public void run() {
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    startActivity(finalIntent);
                    finish();
                }
            }
        };
        timer.start();
    }


    public boolean isInternetAvailable() throws InterruptedException, IOException
    {
        String command = "ping -c 1 google.com";
        return (Runtime.getRuntime().exec (command).waitFor() == 0);
    }
//
//    public boolean isInternetAvailable2() {
//        try {
//            final InetAddress address = InetAddress.getByName("www.google.com");
//            return !address.equals("");
//        } catch (UnknownHostException e) {
//            // Log error
//        }
//        return false;
//    }

}

