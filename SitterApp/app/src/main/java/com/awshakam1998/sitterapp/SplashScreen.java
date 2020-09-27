package com.awshakam1998.sitterapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);



        Thread task=new Thread() {
            @Override
            public void run() {

                try {

                    sleep(3000);

                }
                catch (Exception e){
                    Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
                }
                finally {
                    Intent i = new Intent(SplashScreen.this, SigninScreen.class);
                    startActivity(i);
                }

            }
        };
        task.start();


    }

    @Override
    protected void onPause() {
        super.onPause();

        finish();
    }
}
