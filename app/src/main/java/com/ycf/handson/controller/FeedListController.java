package com.ycf.handson.controller;

import android.app.Activity;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.ycf.handson.R;
import com.ycf.handson.adapter.FeedAdapter;
import com.ycf.handson.model.FeedResponse;
import com.ycf.handson.network.ApiService;

/**
 * FeedListController 是用于管理瀑布流 RecyclerView 所有配置和数据流的控制器。
 * 实现了 Activity 的极简调用
 */
public class FeedListController implements ApiService.FeedCallback {

    private static final int FEED_RECYCLE_VIEW_ID = R.id.recycler_view_feed;
    private static final int SPAN_COUNT = 2;

    private static final int VISIBLE_THRESHOLD = 5;
    private final Activity activity;
    private final RecyclerView recyclerView;
    private final FeedAdapter adapter;
    private final ApiService apiService;


    private boolean isLoading = false;  // 是否正在加载数据，防止重复请求
    private static final int LOAD_LIMIT = 20; // 每次加载的条数

    public FeedListController(Activity activity, RecyclerView recyclerView) {
        this.activity = activity;
        this.recyclerView = recyclerView;
        this.adapter = new FeedAdapter(activity);
        this.apiService = new ApiService();

        setupRecycleView();
    }

    private void setupRecycleView() {
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(SPAN_COUNT, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);


        // 增加监听滑动的逻辑
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                // TODO 理解这里寻找最后一个可见item的逻辑
                if (dy <= 0) return;
                int totalCount = manager.getItemCount();
                int[] lastVisibleItems = manager.findLastVisibleItemPositions(null);
                int lastVisibleItemPosition = getLastVisibleItem(lastVisibleItems);
                if ((totalCount - lastVisibleItemPosition) < VISIBLE_THRESHOLD) {
                    if (!isLoading) {
                        loadFeed();
                    }
                }
            }
        });
    }

    /**
     * 辅助方法：获取 StaggeredGridLayoutManager 中最后可见的位置
     */
    private int getLastVisibleItem(int[] lastVisibleItemPositions) {
        int maxSize = 0;
        for (int i = 0; i < lastVisibleItemPositions.length; i++) {
            if (i == 0) {
                maxSize = lastVisibleItemPositions[i];
            } else if (lastVisibleItemPositions[i] > maxSize) {
                maxSize = lastVisibleItemPositions[i];
            }
        }
        return maxSize;
    }

    public static FeedListController attach(Activity activity) {
        RecyclerView recyclerView = activity.findViewById(FEED_RECYCLE_VIEW_ID);
        if (recyclerView == null) {
            String resourceName = activity.getResources().getResourceEntryName(FEED_RECYCLE_VIEW_ID);
            throw new IllegalStateException("RecyclerView with ID R.id." + resourceName + " not found in the layout.");
        }

        FeedListController controller = new FeedListController(activity, recyclerView);
        controller.loadFeed();
        return controller;
    }


    private void loadFeed() {
        if (isLoading) {
            return;
        }
        isLoading = true;
        apiService.fetchFeed(10, false, this);
    }


    @Override
    public void onSuccess(FeedResponse response) {
        activity.runOnUiThread(() -> {
            if (response.getPost_list() != null && !response.getPost_list().isEmpty()) {
                adapter.appendData(response.getPost_list());

            } else {
                Toast.makeText(activity, "UI: 未获取到帖子数据", Toast.LENGTH_SHORT).show();
            }

            isLoading = false;

        });
    }

    @Override
    public void onFailure(String errorMessage) {
        activity.runOnUiThread(() -> {
            isLoading = false;
            Toast.makeText(activity, "UI: 加载失败: " + errorMessage, Toast.LENGTH_LONG).show();
        });

    }
}
