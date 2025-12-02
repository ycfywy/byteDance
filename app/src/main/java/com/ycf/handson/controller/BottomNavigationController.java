package com.ycf.handson.controller;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ycf.handson.R;
import com.ycf.handson.listener.OnTabSelectedListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 重构后的底部导航栏控制器
 * 负责处理视图查找、样式切换，并通过接口回调通知 Fragment 切换。
 */
public class BottomNavigationController implements View.OnClickListener {

    public enum BottomTab {
        HOME,
        FRIENDS,
        ADD,
        MESSAGES,
        ME
    }

    private List<View> allTabs;
    private View selectedTab = null;
    private final Map<Integer, BottomTab> tabIdToEnumMap;
    private static final String TAG = "BottomNavController";

    private final OnTabSelectedListener listener; // 新增回调接口

    /**
     * 构造函数：现在接受一个回调监听器
     */
    public BottomNavigationController(OnTabSelectedListener listener) {
        this.listener = listener;
        this.allTabs = new ArrayList<>();
        this.tabIdToEnumMap = new HashMap<>();
    }

    public static BottomNavigationController attach(Activity activity, OnTabSelectedListener listener) {
        BottomNavigationController controller = new BottomNavigationController(listener);
        controller.setup(activity);
        return controller;
    }

    /**
     * 公共入口点：负责查找视图、映射 ID 和设置监听器
     */
    public void setup(Activity activity) {
        // 查找所有 Tab 视图
        TextView tabHome = activity.findViewById(R.id.tab_home);
        TextView tabFriends = activity.findViewById(R.id.tab_friends);
        ImageView tabAdd = activity.findViewById(R.id.tab_add);
        TextView tabMessages = activity.findViewById(R.id.tab_messages);
        TextView tabMe = activity.findViewById(R.id.tab_me);

        // 映射 View ID 到枚举，并添加到列表
        mapTab(tabHome, BottomTab.HOME);
        mapTab(tabFriends, BottomTab.FRIENDS);
        mapTab(tabAdd, BottomTab.ADD);
        mapTab(tabMessages, BottomTab.MESSAGES);
        mapTab(tabMe, BottomTab.ME);

        for (View tab : allTabs) {
            tab.setOnClickListener(this);
            if (tab.getId() == R.id.tab_home) {
                selectedTab = tab;
            }
        }

        updateTabStyles(selectedTab);
    }

    private void mapTab(View tab, BottomTab tabEnum) {
        allTabs.add(tab);
        tabIdToEnumMap.put(tab.getId(), tabEnum);
    }

    /**
     * 处理 Tab 点击事件
     */
    @Override
    public void onClick(View v) {
        BottomTab clickedTabEnum = tabIdToEnumMap.get(v.getId());

        if (clickedTabEnum == null) return;

        // 1. 处理样式切换
        if (clickedTabEnum != BottomTab.ADD) {
            handleTabStyleSwitch(v);
        }

        // 2. 通过接口回调通知 MainActivity 切换 Fragment
        if (listener != null) {
            listener.onTabSelected(clickedTabEnum);
        }
    }


    private void handleTabStyleSwitch(View clickedTab) {
        // 只有当点击的 Tab 与当前选中的 Tab 不同时，才进行切换
        if (clickedTab != selectedTab) {
            selectedTab = clickedTab;
            updateTabStyles(selectedTab);
        }
    }


    private void updateTabStyles(View newSelectedTab) {
        for (View tab : allTabs) {
            boolean isSelected = (tab == newSelectedTab);
            setTabStyle(tab, isSelected);
        }
    }

    private void setTabStyle(View tab, boolean isSelected) {
        if (tab instanceof TextView) {
            TextView textView = (TextView) tab;
            textView.setTextAppearance(isSelected ? R.style.BottomBarLabel_Selected : R.style.BottomBarLabel_Unselected);
        }
    }
}