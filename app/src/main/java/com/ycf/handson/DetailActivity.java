package com.ycf.handson;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.ycf.handson.adapter.GalleryAdapter;
import com.ycf.handson.manager.BgmManager;
import com.ycf.handson.manager.FollowStatusManager;
import com.ycf.handson.manager.LikeStatusManager;
import com.ycf.handson.manager.MediaPreloadManager;
import com.ycf.handson.model.Author;
import com.ycf.handson.model.Clip;
import com.ycf.handson.model.Hashtag;
import com.ycf.handson.model.Post;
import com.ycf.handson.utils.DateUtil;

import java.util.List;


public class DetailActivity extends AppCompatActivity {
    public static final String EXTRA_POST_ID = "EXTRA_POST_ID";

    private static final String TAG = "DETAIL_ACTIVITY";
    // --- 顶部栏 Views ---
    private ImageView btnBack;
    private ShapeableImageView ivAuthorAvatar;
    private TextView tvAuthorName;
    private TextView btnFollow;

    // --- 内容区 Views ---
    private ViewPager2 viewPagerGallery;
    private LinearLayout layoutIndicators; // 用于放置 ViewPager2 的指示器

    private ImageView ivBgmIcon;
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

    private BgmManager bgmManager;

    private MediaPreloadManager preloadManager;
    private ExoPlayer player;

    private Boolean isPlay = true;

    private FollowStatusManager followStatusManager;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        initViews();

        preloadManager = MediaPreloadManager.getInstance(this);
        player = preloadManager.getPlayer();
        followStatusManager = new FollowStatusManager(this);


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

        layoutIndicators = findViewById(R.id.indicator_container);

        ivBgmIcon = findViewById(R.id.iv_bgm_icon);
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

            updateFollowButtonUI(author.getUser_id());

            btnFollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleFollow(author.getUser_id());
                }
            });
        } else {
            tvAuthorName.setText("未知作者");
        }


        preloadManager.play(post.getMusic());


        ivBgmIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPlay = !isPlay;
                player.setPlayWhenReady(isPlay);
            }
        });


        tvPostTitle.setText(post.getTitle());
        renderPostContent(post, tvPostContent);
        tvPostDate.setText(DateUtil.formatRelativeTime(post.getCreate_time()));


        List<Clip> clips = post.getClips();
        if (clips != null && !clips.isEmpty()) {
            // TODO: 需要实现 GalleryAdapter 来处理 ViewPager2
            Log.d(TAG, clips.toString());
            GalleryAdapter adapter = new GalleryAdapter(this);
            adapter.appenData(clips);
            viewPagerGallery.setAdapter(adapter);
            setupIndicators(clips.size());

            viewPagerGallery.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    updateIndicatorSelected(position);
                }
            });

        } else {
            viewPagerGallery.setVisibility(View.GONE);
            layoutIndicators.setVisibility(View.GONE);
        }


        btnBack.setOnClickListener(v -> {
            finish();
        });

    }

    private void toggleFollow(String userId) {

        boolean newStatus = followStatusManager.toggleFollowStatus(userId);
        updateFollowButtonUI(userId);
        String message = newStatus ? "已关注" : "已取消关注";
    }

    private void updateFollowButtonUI(String userId) {

        boolean isFollow = followStatusManager.isFollowing(userId);
        if (isFollow) {

            btnFollow.setText("已关注");
            btnFollow.setTextColor(ContextCompat.getColor(this, R.color.followed_text_color));


            btnFollow.setBackgroundResource(R.drawable.bg_btn_followed);

        } else {
            btnFollow.setText("关注");
            btnFollow.setTextColor(ContextCompat.getColor(this, R.color.unfollow_text_color));

            btnFollow.setBackgroundResource(R.drawable.bg_btn_unfollowed);
        }
    }


    /**
     *
     * 使用SpannableString 高亮content关键词
     *
     * @param post     需要渲染内容的post
     * @param textView 装content的textView
     */
    public void renderPostContent(Post post, TextView textView) {

        if (post == null || post.getContent() == null) {
            textView.setText("");
            return;
        }
        String content = post.getContent();
        List<Hashtag> hashtags = post.getHashtag();

        SpannableString spannableContent = new SpannableString(content);
        int highlightColor = ContextCompat.getColor(this, R.color.hashtag_highlight);

        if (hashtags != null) {
            for (Hashtag tag : hashtags) {
                int start = tag.getStart();
                int end = tag.getEnd();

                if (start >= 0 && end <= content.length() && start < end) {
                    String tagText = content.substring(start, end);
                    spannableContent.setSpan(getHashtagClickableSpan(tagText, this, highlightColor),
                            start,
                            end,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }

        }

        textView.setText(spannableContent);
        textView.setMovementMethod(LinkMovementMethod.getInstance());

    }


    private ClickableSpan getHashtagClickableSpan(final String tagText, final Context context, final int highlightColor) {

        return new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent intent = new Intent(context, TagDetailActivity.class);
                intent.putExtra(TagDetailActivity.EXTRA_HASHTAG_TEXT, tagText);
                context.startActivity(intent);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(highlightColor);
                ds.setUnderlineText(false);
            }
        };
    }


    // indicator相关代码

    private void setupIndicators(int count) {

        if (count <= 1) {
            layoutIndicators.setVisibility(View.GONE);
            return;
        }

        layoutIndicators.removeAllViews();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1.0f
        );

        int marginPx = (int) (getResources().getDisplayMetrics().density * 4);
        params.setMargins(marginPx, 0, marginPx, 0);

        for (int i = 0; i < count; i++) {
            View indicator = new View(this);

            indicator.setLayoutParams(params);
            indicator.getLayoutParams().height = (int) (getResources().getDisplayMetrics().density * 3);
            indicator.setBackgroundResource(R.drawable.indicator_bar_unselected);
            layoutIndicators.addView(indicator);
        }

        updateIndicatorSelected(0);

    }

    private void updateIndicatorSelected(int i) {
        int childCount = layoutIndicators.getChildCount();
        for (int i1 = 0; i1 < childCount; i1++) {
            View childAt = layoutIndicators.getChildAt(i1);
            if (i1 == i) {
                childAt.setBackgroundResource(R.drawable.indicator_bar_selected);
            } else {
                childAt.setBackgroundResource(R.drawable.indicator_bar_unselected);
            }
        }

    }


    @Override
    protected void onPause() {
        super.onPause();
        player.pause();

    }

    @Override
    protected void onRestart() {
        Log.d(TAG, "onRestart started");
        super.onRestart();
        player.pause();
        ;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.pause();
    }


}
