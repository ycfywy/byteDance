package com.ycf.handson.controller;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.ycf.handson.R;
import com.ycf.handson.manager.LikeStatusManager;
import com.ycf.handson.model.Post;

/**
 * 详情页底部栏（Footer）的逻辑控制器。
 * 负责评论输入占位符点击、点赞/评论/收藏/分享动作及其状态更新。
 */
public class DetailFooterController {

    private final Context context;

    // --- 底部栏 Views ---
    private ConstraintLayout layoutBottomBar;
    private TextView inputCommentPlaceholder;

    // 动作组
    private LinearLayout btnActionLike;
    private ImageView ivLike;
    private TextView tvLikeCount;

    private LinearLayout btnActionComment;
    private LinearLayout btnActionStar;
    private LinearLayout btnActionShare;

    // --- Manager ---
    private final LikeStatusManager likeStatusManager;

    public DetailFooterController(Activity activity, LikeStatusManager likeStatusManager) {
        this.context = activity;
        this.likeStatusManager = likeStatusManager;
        initViews(activity);
        setupListeners();
    }

    /**
     * 绑定底部栏 Views。
     */
    private void initViews(Activity activity) {
        // 外部容器
        layoutBottomBar = activity.findViewById(R.id.layout_bottom_bar);

        // 评论输入占位符
        inputCommentPlaceholder = activity.findViewById(R.id.input_comment_placeholder);

        // 点赞
        btnActionLike = activity.findViewById(R.id.btn_action_like);
        ivLike = activity.findViewById(R.id.iv_like);
        tvLikeCount = activity.findViewById(R.id.tv_like_count);

        // 评论
        btnActionComment = activity.findViewById(R.id.btn_action_comment);

        // 收藏/星标
        btnActionStar = activity.findViewById(R.id.btn_action_star);

        // 分享
        btnActionShare = activity.findViewById(R.id.btn_action_share);
    }

    /**
     * 设置监听器，处理所有底部栏的点击事件。
     */
    private void setupListeners() {

        // 1. 评论输入占位符点击事件
        inputCommentPlaceholder.setOnClickListener(v -> {
            // TODO: 实际应用中，这里应该弹出评论输入框或跳转到评论 Activity
            Toast.makeText(context, "打开评论输入框...", Toast.LENGTH_SHORT).show();
            Log.d("FOOTER_CONTROLLER", "Comment placeholder clicked");
        });

        // 2. 点赞点击事件
        btnActionLike.setOnClickListener(v -> {
            // TODO: 实现 toggleLikeStatus(postId) 逻辑
            Toast.makeText(context, "点赞/取消点赞", Toast.LENGTH_SHORT).show();
        });

        // 3. 评论按钮点击事件
        btnActionComment.setOnClickListener(v -> {
            // TODO: 跳转到评论列表或滚动到评论区
            Toast.makeText(context, "查看评论列表", Toast.LENGTH_SHORT).show();
        });

        // 4. 收藏/星标点击事件
        btnActionStar.setOnClickListener(v -> {
            // TODO: 实现 toggleStarStatus(postId) 逻辑
            Toast.makeText(context, "收藏/取消收藏", Toast.LENGTH_SHORT).show();
        });

        // 5. 分享点击事件
        btnActionShare.setOnClickListener(v -> {
            // TODO: 弹出分享菜单
            Toast.makeText(context, "弹出分享菜单", Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * 绑定 Post 数据，更新底部栏 UI。
     *
     * @param post 当前帖子的数据模型。
     */
    public void bind(Post post) {
        if (post == null) return;

        // 1. 更新点赞数量
        // 假设 Post 模型中有 getLikeCount() 方法
        tvLikeCount.setText(String.valueOf(post.getLike_count()));

        // 2. 更新评论数量 (需要额外的 TextView，但布局中没有 ID，这里假设它有)
        // TextView tvCommentCount = btnActionComment.findViewById(R.id.tv_comment_count);
        // if (tvCommentCount != null) {
        //     tvCommentCount.setText(String.valueOf(post.getCommentCount()));
        // }

        // 3. 更新点赞图标状态 (需要 LikeStatusManager 配合 postId 判断)
        // 例如：
        /*
        boolean isLiked = likeStatusManager.isLiked(post.getPostId());
        if (isLiked) {
            ivLike.setImageResource(R.drawable.ic_heart_filled);
        } else {
            ivLike.setImageResource(R.drawable.ic_heart_outline);
        }
        */
    }
}