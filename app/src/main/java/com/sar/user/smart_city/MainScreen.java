package com.sar.user.smart_city;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;

public class MainScreen extends AppCompatActivity {
    private FirebaseAuth.AuthStateListener authStateListener;
    private Button  recordButton;
    private MediaRecorder myAudioRecorder;
    private String outputFile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        recordButton=findViewById(R.id.recordButton);
        recordButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), RecordAudio.class);
                startActivity(i);
            }
        });
    }
}
