package com.ycf.handson;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.ycf.handson.controller.TabNavigationController;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TabNavigationController.attach(this);
    }


}