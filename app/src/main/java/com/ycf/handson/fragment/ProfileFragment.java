package com.ycf.handson.fragment; // 假设这是你的 Fragment 所在包

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.ycf.handson.R;

public class ProfileFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
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

        // 3. 设置 ViewPager2 适配器
        viewPager.setAdapter(new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return null;
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            }

            @Override
            public int getItemCount() {
                return 0;
            }
        });

        // 4. 将 TabLayout 和 ViewPager2 关联起来
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(tabTitles[position])
        ).attach();

        // 5. 设置返回按钮逻辑
        btnBack.setOnClickListener(v -> {
            // 在 Fragment 中实现“返回”操作，通常是返回到上一个 Fragment 或关闭 Activity
            if (getActivity() != null) {
                // 如果是在 Activity 中，可以结束 Activity
                // getActivity().finish();

                // 如果是在 Fragment 栈中，可以弹出 Fragment
                getParentFragmentManager().popBackStack();
            }
        });

    }
}