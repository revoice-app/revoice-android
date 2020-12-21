package com.sar.user.smart_city;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class Splash extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        firebaseAuth = com.google.firebase.auth.FirebaseAuth.getInstance();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */

                if (firebaseAuth.getCurrentUser() != null){
                    Intent mainIntent = new Intent(Splash.this, MainScreen.class);
                    Splash.this.startActivity(mainIntent);
                    Splash.this.finish();
                }

            }
        }, 4000);
    }

    protected void onStart() {
        super.onStart();
        if (firebaseAuth.getCurrentUser() != null) {
        } else {
            finish();
            startActivity(new Intent(Splash.this, MainActivity.class));
        }
    }
}