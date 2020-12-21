package com.sar.user.smart_city;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.button.MaterialButton;
import org.tensorflow.lite.examples.textclassification.client.Result;
import org.tensorflow.lite.examples.textclassification.client.TextClassificationClient;
import java.util.List;
import androidx.appcompat.app.AppCompatActivity;
public class FinalAct extends AppCompatActivity {
    TextView textView;
    private TextClassificationClient client;
    private MaterialButton materialButton;
    private Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final);
        textView=findViewById(R.id.txvResult);
        client = new TextClassificationClient(getApplicationContext());
        handler = new Handler();
        Intent intent=getIntent();
        String text=intent.getStringExtra("text");
        textView.setText(text);
        //classify("nice");
        materialButton=findViewById(R.id.button);
        materialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(FinalAct.this,"Thank you",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(FinalAct.this,MainScreen.class));
            }
        });
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
    }
    /** Send input text to TextClassificationClient and get the classify messages. */
    private void classify(final String text) {
        handler.post(
                () -> {
                    // Run text classification with TF Lite.
                    List<Result> results = client.classify(text.toString());
                    // Show classification result on screen
                    showResult(text, results);
                });
    }
    /** Show classification result on the screen. */
    private void showResult(final String inputText, final List<Result> results) {
        // Run on UI thread as we'll updating our app UI
        runOnUiThread(
                () -> {
                    String textToShow = "Input: " + inputText + "\nOutput:\n";
                    for (int i = 0; i < results.size(); i++) {
                        Result result = results.get(i);
                        textToShow += String.format("    %s: %s\n", result.getTitle(), result.getConfidence());
                    }
                    textToShow += "---------\n";
                    // Append the result to the UI.
                    textView.append(textToShow);
                    // Clear the input text.
                    // Scroll to the bottom to show latest entry's classification result.
                });
    }
}