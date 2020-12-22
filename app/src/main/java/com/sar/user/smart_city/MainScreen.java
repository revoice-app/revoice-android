package com.sar.user.smart_city;

import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import com.google.firebase.auth.FirebaseAuth;

public class MainScreen extends AppCompatActivity {
    private FirebaseAuth.AuthStateListener authStateListener;
    private Button recordButton;
    private MediaRecorder myAudioRecorder;
    private String outputFile;
    private RelativeLayout audioRL;
    private AppCompatImageView logout;
    private RelativeLayout textRL;
    private RelativeLayout aboutUs;
    private RelativeLayout profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);
        audioRL = findViewById(R.id.audioRL);
        textRL = findViewById(R.id.textRL);
        aboutUs = findViewById(R.id.aboutUs);
        profile = findViewById(R.id.profile);

        audioRL = findViewById(R.id.audioRL);
        textRL = findViewById(R.id.textRL);

        audioRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainScreen.this, AudioToText.class));
            }
        });
        textRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainScreen.this, TextReview.class));
            }
        });

        aboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainScreen.this, AboutUs.class));
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainScreen.this, Profile.class));
            }
        });

    }
}
