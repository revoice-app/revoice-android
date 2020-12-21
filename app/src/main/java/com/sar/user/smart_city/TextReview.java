package com.sar.user.smart_city;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import com.google.android.material.button.MaterialButton;

public class TextReview extends AppCompatActivity {
    private MaterialButton nextBtn;
    private AppCompatImageView backIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.text_review);
        nextBtn = findViewById(R.id.nextBtn);
        backIV = findViewById(R.id.backIV);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(TextReview.this, ImageSelect.class);
                startActivity(i);
            }
        });

        backIV.setOnClickListener(view -> {
            finish();
        });

    }
}
