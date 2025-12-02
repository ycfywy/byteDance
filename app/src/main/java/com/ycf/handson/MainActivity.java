package com.ycf.handson;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.ycf.handson.controller.BottomNavigationController;
import com.ycf.handson.controller.FeedListController;
import com.ycf.handson.controller.TabNavigationController;

public class MainActivity extends AppCompatActivity {


    private FragmentManager fragmentManager;

    private TabNavigationController tabNavigationController;

    private FeedListController feedListController;

    private BottomNavigationController bottomNavigationController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();


        tabNavigationController = TabNavigationController.attach(this);
        feedListController = FeedListController.attach(this);
        bottomNavigationController = BottomNavigationController.attach(this, feedListController);


    }


}