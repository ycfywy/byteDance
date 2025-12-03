package com.ycf.handson;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.exoplayer.ExoPlayer;

import com.ycf.handson.controller.DetailContentController;
import com.ycf.handson.controller.DetailFooterController;
import com.ycf.handson.controller.DetailHeaderController;
import com.ycf.handson.manager.FollowStatusManager;
import com.ycf.handson.manager.LikeStatusManager;
import com.ycf.handson.manager.MediaPreloadManager;
import com.ycf.handson.model.Post;

import lombok.Getter;


public class DetailActivity extends AppCompatActivity {
    public static final String EXTRA_POST_ID = "EXTRA_POST_ID";
    private static final String TAG = "DETAIL_ACTIVITY";


    // --- Controller 实例 ---
    private DetailHeaderController detailHeaderController;
    private DetailContentController detailContentController;
    private DetailFooterController detailFooterController; // 新增底部控制器


    @Getter
    private Post currentPost;

    @Getter
    private int currentIndex;
    private MediaPreloadManager preloadManager;
    private ExoPlayer player;
    private FollowStatusManager followStatusManager;
    private LikeStatusManager likeStatusManager; // 新增 LikeStatusManager

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        // 初始化 Manager 和 Player
        preloadManager = MediaPreloadManager.getInstance(this);
        player = preloadManager.getPlayer();
        followStatusManager = new FollowStatusManager(this);
        likeStatusManager = new LikeStatusManager(this); // 初始化 LikeStatusManager


        // 初始化 Controllers 并注入依赖
        detailHeaderController = new DetailHeaderController(this, followStatusManager);
        detailContentController = new DetailContentController(this, preloadManager, player);
        detailFooterController = new DetailFooterController(this, likeStatusManager); // 注入 LikeStatusManager


        // 4. 获取数据并更新UI
        if (getIntent() != null) {
            Post post = getIntent().getParcelableExtra("post_data");
            int position = getIntent().getIntExtra("post_position", 0);

            if (post != null) {
                currentPost = post;
                currentIndex = position;
                updateUI(currentPost, currentIndex);
            }
        }
    }

    private void updateUI(Post post, int index) {
        // 1. 更新顶部栏
        detailHeaderController.bind(post);

        // 2. 更新内容区、媒体、画廊
        detailContentController.bind(post);

        // 3. 更新底部栏动作
        detailFooterController.bind(post);
    }


    @Override
    protected void onPause() {
        super.onPause();
        detailContentController.pauseMedia();
    }

    @Override
    protected void onRestart() {
        Log.d(TAG, "onRestart started. Pausing media.");
        super.onRestart();
        detailContentController.resumeMedia();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        detailContentController.pauseMedia();
    }
}