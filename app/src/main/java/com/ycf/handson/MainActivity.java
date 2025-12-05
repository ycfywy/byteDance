package com.ycf.handson;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.ycf.handson.controller.BottomNavigationController;
import com.ycf.handson.controller.TopNavigationController;
import com.ycf.handson.fragment.FeedFragment;
import com.ycf.handson.fragment.ProfileFragment;
import com.ycf.handson.listener.OnTabSelectedListener;


public class MainActivity extends AppCompatActivity implements OnTabSelectedListener {

    private FragmentManager fragmentManager;
    private BottomNavigationController bottomNavigationController;
    private TopNavigationController topNavigationController;

    private FeedFragment feedFragment;
    private ProfileFragment profileFragment;
    private Fragment activeFragment; // 当前可见的 Fragment

    private static final String TAG_FEED = "TAG_FEED";
    private static final String TAG_ME = "TAG_ME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();


        bottomNavigationController = BottomNavigationController.attach(this, this);
        topNavigationController = TopNavigationController.attach(this);

        if (savedInstanceState == null) {
            initFragments();
        } else {
            restoreFragments();
        }


    }

    private void initFragments() {
        FragmentTransaction ft = fragmentManager.beginTransaction();

        feedFragment = new FeedFragment();
        profileFragment = new ProfileFragment();

        ft.add(R.id.fragment_container, profileFragment, TAG_ME).hide(profileFragment);
        ft.add(R.id.fragment_container, feedFragment, TAG_FEED);

        activeFragment = feedFragment;
        ft.commit();
    }

    /**
     * Activity 重建时：从 FragmentManager 恢复 Fragment 实例
     */
    private void restoreFragments() {

        feedFragment = (FeedFragment) fragmentManager.findFragmentByTag(TAG_FEED);
        profileFragment = (ProfileFragment) fragmentManager.findFragmentByTag(TAG_ME);

        if (feedFragment != null && !feedFragment.isHidden()) activeFragment = feedFragment;
        else if (profileFragment != null && !profileFragment.isHidden())
            activeFragment = profileFragment;
    }

    /**
     * 实现 OnTabSelectedListener 接口，根据点击的 Tab 枚举切换 Fragment
     */
    @Override
    public void onTabSelected(BottomNavigationController.BottomTab tab) {
        switch (tab) {
            case HOME:
                switchFragment(feedFragment);
                topNavigationController.setBarsVisibility(true);
                break;
            case ME:
                switchFragment(profileFragment);
                topNavigationController.setBarsVisibility(false);
                break;
            case ADD:
                Toast.makeText(this, "执行发布（ADD）操作", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void switchFragment(Fragment nextFragment) {
        if (activeFragment == nextFragment) {
            if (nextFragment instanceof FeedFragment) {
                ((FeedFragment) nextFragment).triggerRefresh();
            }
            return;
        }

        FragmentTransaction ft = fragmentManager.beginTransaction();

        if (activeFragment != null) {
            ft.hide(activeFragment);
        }
        if (nextFragment != null) {
            ft.show(nextFragment);
        }
        ft.commit();
        activeFragment = nextFragment;
    }
}