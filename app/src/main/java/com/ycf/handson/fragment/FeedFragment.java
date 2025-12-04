package com.ycf.handson.fragment; // 建议将 Fragment 放在单独的包中

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ycf.handson.DetailActivity;
import com.ycf.handson.R;
import com.ycf.handson.adapter.FeedAdapter;
import com.ycf.handson.manager.MediaPreloadManager;
import com.ycf.handson.model.FeedResponse;
import com.ycf.handson.model.Post;
import com.ycf.handson.network.ApiService;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * FeedFragment 用于展示瀑布流 Feed 列表。
 * 它继承了原 FeedListController 的所有数据和视图管理逻辑。
 */
public class FeedFragment extends Fragment implements ApiService.FeedCallback {

    // 静态常量保持不变
    private static final String TAG = "FeedFragment";
    private static final int SPAN_COUNT = 2;
    private static final int VISIBLE_THRESHOLD = 10;
    private static final int LOAD_LIMIT = 30;

    // 视图变量使用 ViewBinding 最佳，但为匹配原代码结构，此处使用 findViewById
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout loadErrorLayout;

    // 逻辑和数据管理变量
    private FeedAdapter adapter;
    private ApiService apiService;
    private MediaPreloadManager mediaPreloadManager;
    private boolean isLoading = false;


    private final ActivityResultLauncher<Intent> detailActivityLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        // 当 DetailActivity 结束时，会执行这里的代码
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null) {

                                int newLikeCount = data.getIntExtra("new_like_count", -1);
                                int position = data.getIntExtra("post_position", RecyclerView.NO_POSITION);

                                adapter.updatePostLike(position, newLikeCount);
                            }
                        }
                    });


    public void startDetailActivityForUpdate(Post post, int position) {

        Intent intent = new Intent(requireContext(), DetailActivity.class);
        intent.putExtra("post_data", post);
        intent.putExtra("post_position", position);

        detailActivityLauncher.launch(intent);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. 初始化视图：从 Fragment 的 View 中查找
        recyclerView = view.findViewById(R.id.recycler_view_feed);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        loadErrorLayout = view.findViewById(R.id.load_error_view);

        // 2. 检查关键视图是否存在
        if (recyclerView == null || swipeRefreshLayout == null) {
            Log.e(TAG, "必要的视图（RecyclerView 或 SwipeRefreshLayout）未找到，请检查 fragment_feed.xml");
            return;
        }

        // 3. 初始化逻辑组件
        Activity activity = requireActivity();
        this.apiService = new ApiService();
        this.mediaPreloadManager = MediaPreloadManager.getInstance(activity);
        this.adapter = new FeedAdapter(activity, mediaPreloadManager, this);


        // 4. 设置和启动
        setupRecycleView();
        setupSwipeRefresh();
        setupLoadError();

        // 初始加载数据
        loadFeed();
    }

    // --- 原 FeedListController 中的私有方法，现在是 Fragment 的私有方法 ---

    private void setupLoadError() {
        if (loadErrorLayout == null) return;

        View btn = loadErrorLayout.findViewById(R.id.btn_retry);
        if (btn != null) {
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadFeed();
                }
            });
        }
    }

    public void setupSwipeRefresh() {
        // 使用方法引用
        swipeRefreshLayout.setOnRefreshListener(this::refreshFeed);
    }

    // 供外部调用的刷新函数（例如，在 tab_home 被点击时）
    public void triggerRefresh() {
        // 确保在主线程执行 UI 操作，但 Fragment 所在的 Activity 运行在主线程，
        // 且 View 的操作默认在主线程，这里可以简化为直接调用 refreshFeed()
        refreshFeed();
    }

    public void refreshFeed() {
        if (isLoading || getActivity() == null) {
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

        // 滚动加载更多
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
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

        // 滚动停止时预加载媒体
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    Log.d(TAG, "Scroll stop, preload media begin");
                    List<Post> visiblePosts = findVisiblePostMusic(manager);
                    mediaPreloadManager.preloadMedia(visiblePosts);
                }
            }
        });
    }

    private List<Post> findVisiblePostMusic(StaggeredGridLayoutManager manager) {
        int[] firstVisibleItemPositions = manager.findFirstVisibleItemPositions(null);
        int[] lastVisibleItemPositions = manager.findLastVisibleItemPositions(null);

        // 使用 max() 确保找到真正的边界
        int l = Arrays.stream(firstVisibleItemPositions).min().orElse(0); // 应该用 min() 找到最小的起始位置
        int r = Arrays.stream(lastVisibleItemPositions).max().orElse(0);

        List<Post> postList = adapter.getPostList();

        return IntStream.range(l, r + 1)
                .filter(i -> i >= 0 && i < postList.size())
                .mapToObj(postList::get)
                .collect(Collectors.toList());
    }


    /**
     * 辅助方法：获取 StaggeredGridLayoutManager 中最后可见的位置
     */
    private int getLastVisibleItem(int[] lastVisibleItemPositions) {
        int maxSize = 0;
        for (int pos : lastVisibleItemPositions) {
            if (pos > maxSize) {
                maxSize = pos;
            }
        }
        return maxSize;
    }

    private void loadFeed() {
        if (isLoading || getActivity() == null) {
            return;
        }
        isLoading = true;
        // 首次加载，使用 10 个限制（原代码中的值）
        apiService.fetchFeed(LOAD_LIMIT, false, this);
    }

    // --- 内存清理：在 Fragment 销毁视图时清理资源 ---
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        recyclerView.setAdapter(null);
        recyclerView = null;
        swipeRefreshLayout = null;
        loadErrorLayout = null;

    }


    // --- ApiService.FeedCallback 实现 ---

    @Override
    public void onSuccess(FeedResponse response) {
        // 使用 getActivity() 来确保 Fragment 仍然依附于 Activity
        if (getActivity() == null) return;

        // 切换到主线程更新 UI
        getActivity().runOnUiThread(() -> {
            if (response.getPost_list() != null && !response.getPost_list().isEmpty()) {
                adapter.appendData(response.getPost_list());
            } else {
                Toast.makeText(requireContext(), "UI: 未获取到帖子数据", Toast.LENGTH_SHORT).show();
            }

            isLoading = false;
            if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }

            // 显示/隐藏错误视图
            if (loadErrorLayout != null) {
                loadErrorLayout.setVisibility(View.GONE);
            }
            if (recyclerView != null) {
                recyclerView.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onFailure(String errorMessage) {
        if (getActivity() == null) return;

        getActivity().runOnUiThread(() -> {
            isLoading = false;

            if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }

            // 显示错误视图，隐藏 RecyclerView
            if (loadErrorLayout != null) {
                loadErrorLayout.setVisibility(View.VISIBLE);
            }
            if (recyclerView != null) {
                recyclerView.setVisibility(View.GONE);
            }
        });
    }
}