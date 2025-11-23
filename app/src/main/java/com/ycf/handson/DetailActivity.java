package com.ycf.handson;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.ycf.handson.manager.LikeStatusManager;
import com.ycf.handson.model.Author;
import com.ycf.handson.model.Clip;
import com.ycf.handson.model.Post;

import java.util.List;

public class DetailActivity extends AppCompatActivity {
    public static final String EXTRA_POST_ID = "EXTRA_POST_ID";

    // --- 顶部栏 Views ---
    private ImageView btnBack;
    private ShapeableImageView ivAuthorAvatar;
    private TextView tvAuthorName;
    private TextView btnFollow;

    // --- 内容区 Views ---
    private ViewPager2 viewPagerGallery;
    private LinearLayout layoutIndicators; // 用于放置 ViewPager2 的指示器
    private TextView tvPostTitle;
    private TextView tvPostContent;
    private TextView tvPostDate;

    // --- 底部栏 Views ---
    private LinearLayout btnActionLike;
    private ImageView ivLike;
    private TextView tvLikeCount;
    // 忽略评论、收藏、分享按钮的绑定，但它们有 ID

    // --- 数据相关 ---
    private String currentPostId;
    private LikeStatusManager likeStatusManager;
    private Post currentPost; // 实际帖子数据对象

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        initViews();


        if (getIntent() != null) {
            Post post = getIntent().getParcelableExtra("EXTRA_POST_OBJECT");
            if (post != null) {
                currentPost = post;
                updateUI(currentPost);
            }
        }

    }

    private void initViews() {

        // 顶部栏
        btnBack = findViewById(R.id.btn_back);
        ivAuthorAvatar = findViewById(R.id.iv_author_avatar);
        tvAuthorName = findViewById(R.id.tv_author_name);
        btnFollow = findViewById(R.id.btn_follow);

        // 内容区
        viewPagerGallery = findViewById(R.id.view_pager_gallery);
        layoutIndicators = findViewById(R.id.layout_indicators);
        tvPostTitle = findViewById(R.id.tv_post_title);
        tvPostContent = findViewById(R.id.tv_post_content);
        tvPostDate = findViewById(R.id.tv_post_date);

        // 底部栏动作
        btnActionLike = findViewById(R.id.btn_action_like);
        ivLike = findViewById(R.id.iv_like);
        tvLikeCount = findViewById(R.id.tv_like_count);

    }

    private void updateUI(Post post) {

        // 1. 更新顶部栏
        Author author = post.getAuthor();
        if (author != null) {
            tvAuthorName.setText(author.getNickname());
            Glide.with(this).load(author.getAvatar()).into(ivAuthorAvatar);

        } else {
            tvAuthorName.setText("未知作者");
        }


        // 2. 内容区

        tvPostTitle.setText(post.getTitle());
        tvPostContent.setText(post.getContent());
        tvPostDate.setText("创建时间: " + post.getCreate_time());


        // 3. 如何实现轮播 ?
        List<Clip> clips = post.getClips();
        if (clips != null && !clips.isEmpty()) {
            // TODO: 需要实现 GalleryAdapter 来处理 ViewPager2
            // GalleryAdapter adapter = new GalleryAdapter(this, clips);
            // viewPagerGallery.setAdapter(adapter);
            // setupIndicators(clips.size());
        } else {
            viewPagerGallery.setVisibility(View.GONE);
            layoutIndicators.setVisibility(View.GONE);
        }

    }

}
