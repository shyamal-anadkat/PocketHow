package edu.wisc.ece.pockethow.frontend;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import edu.wisc.ece.pockethow.R;


public class SplashScreen extends AppCompatActivity{

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        final Intent intent = new Intent(this, CategorySelectionActivity.class);;
        Thread timer= new Thread()
        {
            public void run(){
                try
                {
                    sleep(1000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                finally {
                    startActivity(intent);
                    finish();
                }
            }
        };
        timer.start();
    }

}
