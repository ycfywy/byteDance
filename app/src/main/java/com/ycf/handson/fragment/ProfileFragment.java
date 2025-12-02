package com.ycf.handson.fragment; // 假设这是你的 Fragment 所在包

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.ycf.handson.R;

public class ProfileFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager; // 尽管我们不再使用，但保留以匹配布局
    private ImageView btnBack;

    // 标签页标题
    private final String[] tabTitles = new String[]{"作品", "喜欢", "私密"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 1. 加载布局文件
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 2. 初始化视图
        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager = view.findViewById(R.id.view_pager);
        btnBack = view.findViewById(R.id.btn_back);


        for (String title : tabTitles) {
            tabLayout.addTab(tabLayout.newTab().setText(title));
        }

        // 4. (可选) 如果您需要知道哪个 Tab 被选中，可以添加监听器
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // 当前选中 Tab 的位置：tab.getPosition()
                // 您可以在这里添加逻辑来替换 Fragment 或加载内容
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                //
            }
        });

        // 5. 设置返回按钮逻辑
        btnBack.setOnClickListener(v -> {
            // 在 Fragment 中实现“返回”操作
            if (getActivity() != null) {
                getParentFragmentManager().popBackStack();
            }
        });

    }
}