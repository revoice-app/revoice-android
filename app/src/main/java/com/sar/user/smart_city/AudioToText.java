package com.sar.user.smart_city;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.tensorflow.lite.examples.textclassification.client.Result;
import org.tensorflow.lite.examples.textclassification.client.TextClassificationClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class AudioToText extends AppCompatActivity {

    private EditText txvResult;
    private MaterialButton materialButton;
    DatabaseReference myRef;
    private TextClassificationClient client;

    private TextView resultTextView;
    private EditText inputEditText;
    private Handler handler;
    private ScrollView scrollView;
    private static String kaka="";
    private String text = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_to_text);
        txvResult =  findViewById(R.id.txvResult);
        materialButton=findViewById(R.id.buttonSave);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef= database.getReference("review");
        client = new TextClassificationClient(getApplicationContext());
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Submit",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        Toast.makeText(AudioToText.this,"Thank you",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AudioToText.this,MainScreen.class));
                        myRef.child(FirebaseAuth.getInstance().getUid().toString()).setValue(kaka).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d("popo","oo");
                            }
                        });
                    }
                });

        builder1.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });




                handler = new Handler();
        Button classifyButton = findViewById(R.id.button);
        materialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // builder1.setMessage(kaka);
               // AlertDialog alert11 = builder1.create();
               // alert11.show();
                classify(txvResult.getText().toString());

            }
        });



    }

    public void getSpeechInput(View view) {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 10);
        } else {
            Toast.makeText(this, "Your Device Don't Support Speech Input", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    txvResult.setText(result.get(0));
                    materialButton.setVisibility(View.VISIBLE);

                }
                break;
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        handler.post(
                () -> {
                    client.load();
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.post(
                () -> {
                    client.unload();
                });
    }private void classify(final String text) {
        handler.post(
                () -> {
                    // Run text classification with TF Lite.
                    List<Result> results = client.classify(text);

                    // Show classification result on screen
                    showResult(text, results);
                });
    }

    /**
     * Show classification result on the screen.
     */
    private void showResult(final String inputText, final List<Result> results) {
        // Run on UI thread as we'll updating our app UI
        runOnUiThread(
                () -> {
                    String textToShow = "Input: " + inputText + "\nOutput:\n";
                    Float a = 0f;
                    Float b = 0f;
                    for (int i = 0; i < results.size(); i++) {
                        Result result = results.get(i);
                        textToShow += String.format("    %s: %s\n", result.getTitle(), result.getConfidence());
                        if (i == 0)
                            a = result.getConfidence();
                        else {
                            b = result.getConfidence();
                            ;
                        }
                    }
                    textToShow += "\n";
                    text = textToShow;
                    if (text.length() > 0) {
                        Intent intent = new Intent(this, ImageSelect.class);
                        intent.putExtra("text", inputText);
                        if (a > b)
                            intent.putExtra("textSent", "Positive Review sentiment: We determined that you gave a \"Positive\" review. Thank you for your efforts.");
                        else {
                            intent.putExtra("textSent", "Negative Review sentiment: We determined that you gave a \"Negative\" review. We are sorry for the inconvenience.");
                        }
                        startActivity(intent);
                    }

                    // Append the result to the UI.
                    //resultTextView.setText(textToShow);

                    // Clear the input text.
                    // inputEditText.getText().clear();

                    // Scroll to the bottom to show latest entry's classification result.
                    //scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
                });
    }

}