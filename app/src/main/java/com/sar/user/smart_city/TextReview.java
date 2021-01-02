/*
 * Copyright 2019 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sar.user.smart_city;

import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import org.tensorflow.lite.examples.textclassification.client.Result;
import org.tensorflow.lite.examples.textclassification.client.TextClassificationClient;

import java.util.List;

public class TextReview extends AppCompatActivity {
    private static final String TAG = "TextClassificationDemo";

    private TextClassificationClient client;

    private AppCompatTextView resultTextView;
    private EditText inputEditText;
    private Handler handler;
    private ScrollView scrollView;
    private String text = "";
    LocationManager locationManager;
    private AppCompatImageView backIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tfe_tc_activity_main);
        Log.v(TAG, "onCreate");

        client = new TextClassificationClient(getApplicationContext());
        handler = new Handler();
        Button classifyButton = findViewById(R.id.button);
        backIV=findViewById(R.id.backIV);
        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        classifyButton.setOnClickListener(
                (View v) -> {
                    if (inputEditText.getText().length() > 0) {
                        classify(inputEditText.getText().toString());

                    } else {
                        Toast.makeText(TextReview.this, "Please give review", Toast.LENGTH_SHORT).show();

                    }

                });

        inputEditText = findViewById(R.id.input_text);
        resultTextView = findViewById(R.id.subjectTV);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG, "onStart");
        handler.post(
                () -> {
                    client.load();
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v(TAG, "onStop");
        handler.post(
                () -> {
                    client.unload();
                });
    }

    /**
     * Send input text to TextClassificationClient and get the classify messages.
     */
    private void classify(final String text) {
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
