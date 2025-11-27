package com.ycf.handson.controller;

import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

    private static final int SWIPE_REFRESH_LAYOUT_ID = R.id.swipe_refresh_layout;

    private static final int LOAD_ERROR_LAYOUT_ID = R.id.load_error_view;

    private static final int SPAN_COUNT = 2;

    private static final int VISIBLE_THRESHOLD = 5;
    private final Activity activity;
    private final RecyclerView recyclerView;
    private final SwipeRefreshLayout swipeRefreshLayout;


    private final LinearLayout loadErrorLayout;
    private final FeedAdapter adapter;
    private final ApiService apiService;


    private boolean isLoading = false;  // 是否正在加载数据，防止重复请求
    private static final int LOAD_LIMIT = 20; // 每次加载的条数

    public FeedListController(Activity activity, RecyclerView recyclerView, SwipeRefreshLayout swipeRefreshLayout, LinearLayout loadErrorLayout) {
        this.activity = activity;
        this.recyclerView = recyclerView;
        this.adapter = new FeedAdapter(activity);
        this.loadErrorLayout = loadErrorLayout;
        this.apiService = new ApiService();
        this.swipeRefreshLayout = swipeRefreshLayout;
        setupRecycleView();
        setupSwipeRefresh();
        setupLoadError();
    }

    private void setupLoadError() {

        View btn = loadErrorLayout.findViewById(R.id.btn_retry);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFeed();
            }
        });

    }

    public void setupSwipeRefresh() {

        swipeRefreshLayout.setOnRefreshListener(this::refreshFeed);
    }

    // 一个供外界调用的刷新函数
    public void triggerRefresh() {
        // 确保在主线程执行 UI 操作
        activity.runOnUiThread(this::refreshFeed);
    }

    public void refreshFeed() {
        if (isLoading) {
            swipeRefreshLayout.setRefreshing(false);
            return;
        }

        if (!swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(true);
        }

        adapter.clear();
        isLoading = true;
        apiService.fetchFeed(LOAD_LIMIT, true, this);
    }

    private void setupRecycleView() {
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(SPAN_COUNT, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);


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

//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//
//                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//
//                    int centerIndex = findCenterVisibleItemPosition(recyclerView, manager);
//
//                    if (centerIndex != RecyclerView.NO_POSITION) {
//                        // 找到位于中心的 Item 索引: centerIndex
//                        Log.d("CenterItem", "中心可见 Item 索引: " + centerIndex);
//
//                        // 现在你可以用这个索引来更新你的 PreloadManagerWrapper
//                        // preloadManagerWrapper.setCurrentPlayingIndex(centerIndex);
//
//                        MediaPreloadManager
//                    }
//                }
//            }
//        });
    }

//    private int findCenterVisibleItemPosition(RecyclerView recyclerView, StaggeredGridLayoutManager manager) {
//
//        int recyclerViewCenterY = recyclerView.getHeight() / 2;
//
//        int centerItemPosition = RecyclerView.NO_POSITION;
//        double minDistance = Double.MAX_VALUE;
//
//        // 2. 遍历当前所有可见的 Item
//        int firstVisible = manager.findFirstVisibleItemPositions();
//        int lastVisible = manager.findLastVisibleItemPositions();
//
//        for (int i = firstVisible; i <= lastVisible; i++) {
//            // 获取当前索引对应的 Item View
//            View childView = manager.findViewByPosition(i);
//
//            if (childView == null) {
//                continue;
//            }
//
//            // 3. 计算当前 Item View 的中心 Y 坐标
//            // childCenterY = childView.getTop() + (childView.getHeight() / 2)
//            int childCenterY = childView.getTop() + (childView.getHeight() / 2);
//
//            // 4. 计算 Item 中心与 RecyclerView 中心之间的距离
//            double distance = Math.abs(childCenterY - recyclerViewCenterY);
//
//            // 5. 找出距离最小（即最接近中心）的 Item
//            if (distance < minDistance) {
//                minDistance = distance;
//                centerItemPosition = i;
//            }
//        }
//
//        return centerItemPosition;
//    }

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

        SwipeRefreshLayout swipeRefreshLayout = activity.findViewById(SWIPE_REFRESH_LAYOUT_ID);
        if (swipeRefreshLayout == null) {
            String resourceName = activity.getResources().getResourceEntryName(SWIPE_REFRESH_LAYOUT_ID);
            throw new IllegalStateException("SwipeRefreshLayout with ID R.id." + resourceName + " not found in the layout.");
        }

        LinearLayout loadErrorLayout = activity.findViewById(LOAD_ERROR_LAYOUT_ID);


        FeedListController controller = new FeedListController(activity, recyclerView, swipeRefreshLayout, loadErrorLayout);
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
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }

            if (loadErrorLayout.getVisibility() == View.VISIBLE) {
                loadErrorLayout.setVisibility(View.GONE);
            }

            if (recyclerView.getVisibility() == View.GONE) {
                recyclerView.setVisibility(View.VISIBLE);
            }


        });
    }

    @Override
    public void onFailure(String errorMessage) {
        activity.runOnUiThread(() -> {
            isLoading = false;

            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }

            if (loadErrorLayout.getVisibility() == View.GONE) {
                loadErrorLayout.setVisibility(View.VISIBLE);
            }

            if (recyclerView.getVisibility() == View.VISIBLE) {
                recyclerView.setVisibility(View.GONE);
            }


        });

    }
}
