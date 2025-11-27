package com.ycf.handson;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        View btnBack = this.findViewById(R.id.btn_back);

        btnBack.setOnClickListener(v -> {
            finish();
        });


    }
}