package com.ycf.handson.controller;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.ycf.handson.R;
import com.ycf.handson.manager.FollowStatusManager;
import com.ycf.handson.model.Author;
import com.ycf.handson.model.Post;

/**
 * 详情页顶部栏（Header）的逻辑控制器。
 * 负责 View 绑定、作者信息展示和关注功能。
 */
public class DetailHeaderController {

    private final Activity activity;
    private final Context context;

    // --- 顶部栏 Views ---
    private ImageView btnBack;
    private ShapeableImageView ivAuthorAvatar;
    private TextView tvAuthorName;
    private TextView btnFollow;

    // --- Manager ---
    private final FollowStatusManager followStatusManager;

    /**
     * 构造函数：初始化 View 并设置基础监听器。
     *
     * @param activity            宿主 Activity，用于 findViewById 和 finish 操作。
     * @param followStatusManager 关注状态管理器。
     */
    public DetailHeaderController(Activity activity, FollowStatusManager followStatusManager) {
        this.activity = activity;
        this.context = activity;
        this.followStatusManager = followStatusManager;
        initViews();
        setupListeners();
    }

    /**
     * 绑定顶部栏 Views。
     */
    private void initViews() {
        btnBack = activity.findViewById(R.id.btn_back);
        ivAuthorAvatar = activity.findViewById(R.id.iv_author_avatar);
        tvAuthorName = activity.findViewById(R.id.tv_author_name);
        btnFollow = activity.findViewById(R.id.btn_follow);
    }

    /**
     * 设置返回按钮监听器。
     */
    private void setupListeners() {
        // 返回按钮：直接结束 Activity
        btnBack.setOnClickListener(v -> activity.finish());
    }

    /**
     * 绑定 Post 数据，更新顶部栏 UI。
     *
     * @param post 当前帖子的数据模型。
     */
    public void bind(Post post) {
        Author author = post.getAuthor();

        if (author != null) {
            tvAuthorName.setText(author.getNickname());
            Glide.with(context).load(author.getAvatar()).into(ivAuthorAvatar);
            updateFollowButtonUI(author.getUser_id());
            btnFollow.setOnClickListener(v -> toggleFollow(author.getUser_id()));

        } else {
            tvAuthorName.setText("未知作者");
            btnFollow.setVisibility(View.GONE);
        }
    }

    /**
     * 切换关注状态并更新 UI。
     *
     * @param userId 用户的 ID。
     */
    private void toggleFollow(String userId) {
        // 调用 Manager 切换状态
        followStatusManager.toggleFollowStatus(userId);
        updateFollowButtonUI(userId);
    }

    /**
     * 根据当前关注状态更新关注按钮的样式。
     *
     * @param userId 用户的 ID。
     */
    private void updateFollowButtonUI(String userId) {
        boolean isFollow = followStatusManager.isFollowing(userId);

        if (isFollow) {
            btnFollow.setText("已关注");
            btnFollow.setTextColor(ContextCompat.getColor(context, R.color.followed_text_color));
            btnFollow.setBackgroundResource(R.drawable.bg_btn_followed);
        } else {
            btnFollow.setText("关注");
            btnFollow.setTextColor(ContextCompat.getColor(context, R.color.unfollow_text_color));
            btnFollow.setBackgroundResource(R.drawable.bg_btn_unfollowed);
        }
    }
}