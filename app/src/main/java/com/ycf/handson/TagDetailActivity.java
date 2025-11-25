package com.ycf.handson;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class TagDetailActivity extends AppCompatActivity {
    public static final String EXTRA_HASHTAG_TEXT = "HASHTAG_TEXT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tag_detail);

        ImageView btnBack = findViewById(R.id.btn_back);

        btnBack.setOnClickListener(v -> {

            finish();
        });

    }
}