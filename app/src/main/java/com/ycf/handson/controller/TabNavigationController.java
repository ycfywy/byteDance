package com.ycf.handson.controller;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.ycf.handson.R;

import java.util.ArrayList;
import java.util.List;

public class TabNavigationController implements View.OnClickListener {

    private List<TextView> allTabs;
    private TextView selectedTab = null;
    private static final String TAG = "TabController"; // 推荐使用常量标签


    /**
     * 构造函数：初始化控制器，并接收 Activity 作为上下文
     */
    public TabNavigationController() {
        this.allTabs = new ArrayList<>();
    }

    /**
     * 【公共静态入口】: 外部调用方法，负责创建实例并初始化
     *
     * @param activity 宿主 Activity
     */
    public static TabNavigationController attach(Activity activity) {
        TabNavigationController controller = new TabNavigationController();
        controller.setup(activity);
        return controller;
    }

    /**
     * 公共入口点：负责查找视图并设置监听器
     *
     * @param activity 宿主 Activity，用于 findViewById
     */
    public void setup(Activity activity) {
        // 1. 找到所有 Tab 视图并添加到列表中

        TextView tabTongCheng = activity.findViewById(R.id.tab_shangcheng);
        TextView tabBeijing = activity.findViewById(R.id.tab_beijing);
        TextView tabTuangou = activity.findViewById(R.id.tab_tuangou);
        TextView tabGuanZhu = activity.findViewById(R.id.tab_guanzhu);
        TextView tabSheQu = activity.findViewById(R.id.tab_shequ);
        TextView tabTuiJian = activity.findViewById(R.id.tab_tuijian);

        allTabs.add(tabTongCheng);
        allTabs.add(tabBeijing);
        allTabs.add(tabTuangou);
        allTabs.add(tabGuanZhu);
        allTabs.add(tabSheQu);
        allTabs.add(tabTuiJian);

        // 2. 为所有 Tab 设置点击监听器
        for (TextView tab : allTabs) {
            tab.setOnClickListener(this);
            // 3. 确定初始选中的 Tab
            if (tab.getId() == R.id.tab_shequ) {
                selectedTab = tab;
            }
        }

        // 4. 更新所有 Tab 的初始样式
        updateTabStyles(selectedTab);
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "click on tab");
        if (!(v instanceof TextView)) {
            return;
        }
        TextView clickedTab = (TextView) v;

        if (clickedTab != selectedTab) {
            selectedTab = clickedTab;
            updateTabStyles(selectedTab);
            loadFeedData(selectedTab.getText().toString());
        }
    }

    private void loadFeedData(String tabName) {
//        if(tabName.equals("社区"))

    }

    private void updateTabStyles(TextView newSelectedTab) {
        for (TextView tab : allTabs) {
            boolean isSelected = (tab == newSelectedTab);
            setTabStyle(tab, isSelected);
        }
    }

    private void setTabStyle(TextView textView, boolean isSelected) {
        if (isSelected) {
            textView.setTextAppearance(R.style.TabTextStyle_Selected);
        } else {
            textView.setTextAppearance(R.style.TabTextStyle_Unselected);
        }
    }
}