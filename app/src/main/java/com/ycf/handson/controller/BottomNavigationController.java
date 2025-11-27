package com.ycf.handson.controller;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ycf.handson.ProfileActivity;
import com.ycf.handson.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 底部导航栏控制器（使用枚举重构）
 * 负责处理底部五个 Tab 的点击事件和样式切换。
 */
public class BottomNavigationController implements View.OnClickListener {

    // 1. 定义 Tab 的枚举类型
    public enum BottomTab {
        HOME,
        FRIENDS,
        ADD,
        MESSAGES,
        ME
    }

    private List<View> allTabs;
    private View selectedTab = null;
    private Map<Integer, BottomTab> tabIdToEnumMap;
    private static final String TAG = "BottomNavController";

    private final Activity activity;

    private FeedListController feedController;

    /**
     * 构造函数
     */
    public BottomNavigationController(Activity activity, FeedListController feedListController) {
        this.activity = activity;
        this.allTabs = new ArrayList<>();
        this.tabIdToEnumMap = new HashMap<>();
        this.feedController = feedListController;
    }

    public static BottomNavigationController attach(Activity activity, FeedListController feedListController) {

        BottomNavigationController controller = new BottomNavigationController(activity, feedListController);
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

        // 设置监听器，并确定初始选中 Tab
        for (View tab : allTabs) {
            tab.setOnClickListener(this);
            // 默认选中 "首页"
            if (tab.getId() == R.id.tab_home) {
                selectedTab = tab;
            }
        }

        // 更新所有 Tab 的初始样式
        updateTabStyles(selectedTab);
    }

    /**
     * 辅助方法：将 Tab 视图和枚举进行映射，并添加到列表中
     */
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

        if (clickedTabEnum == null) {
            Log.e(TAG, "Unknown Tab ID clicked: " + v.getId());
            return;
        }

        Log.d(TAG, "Clicked Tab: " + clickedTabEnum.name());

        handleTabSwitch(v);
        // 1. 使用 switch 匹配枚举，调用对应逻辑
        switch (clickedTabEnum) {
            case HOME:

                loadHomeFeed();
                break;
            case FRIENDS:

                loadFriendsData();
                break;
            case ADD:
                handleSpecialTabAction(); // "发布" 按钮，不参与普通切换
                break;
            case MESSAGES:

                loadMessages();
                break;
            case ME:

                loadProfile();
                break;
        }
    }

    /**
     * 辅助函数：处理普通的 Tab 切换（样式和选中状态）
     */
    private void handleTabSwitch(View clickedTab) {
        // 只有当点击的 Tab 与当前选中的 Tab 不同时，才进行切换
        if (clickedTab != selectedTab) {
            selectedTab = clickedTab;
            updateTabStyles(selectedTab);

        }
    }

    // --- Tab 对应的具体业务逻辑函数 ---

    private void loadHomeFeed() {
        feedController.triggerRefresh();
        Toast.makeText(selectedTab.getContext(), "加载首页内容", Toast.LENGTH_SHORT).show();
    }

    private void loadFriendsData() {
        Toast.makeText(selectedTab.getContext(), "加载朋友数据", Toast.LENGTH_SHORT).show();
    }

    private void handleSpecialTabAction() {
        // 例如：弹出底部 Sheet 或跳转到发布 Activity
        Toast.makeText(selectedTab.getContext(), "执行发布（ADD）操作", Toast.LENGTH_SHORT).show();
    }

    private void loadMessages() {
        Toast.makeText(selectedTab.getContext(), "加载消息列表", Toast.LENGTH_SHORT).show();
    }

    private void loadProfile() {

        Intent intent = new Intent(activity, ProfileActivity.class);
        activity.startActivity(intent);
        Toast.makeText(selectedTab.getContext(), "加载个人主页", Toast.LENGTH_SHORT).show();
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