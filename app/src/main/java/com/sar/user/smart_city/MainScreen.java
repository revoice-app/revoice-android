package com.sar.user.smart_city;

import android.content.Intent;
import android.media.MediaRecorder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;

public class MainScreen extends AppCompatActivity {
    private FirebaseAuth.AuthStateListener authStateListener;
    private Button  recordButton;
    private MediaRecorder myAudioRecorder;
    private String outputFile;
    private RelativeLayout audioRL;
    private AppCompatImageView logout;
    private RelativeLayout textRL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        audioRL=findViewById(R.id.audioRL);
        textRL=findViewById(R.id.textRL);
        logout=findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
            }
        });
        audioRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainScreen.this,AudioToText.class));
            }
        });
        textRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainScreen.this,TextReview.class));
            }
        });
        //recordButton=findViewById(R.id.recordButton);
//        recordButton.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent(MainScreen.this, ram.class);
//                startActivity(i);
//            }
//        });
    }
}
