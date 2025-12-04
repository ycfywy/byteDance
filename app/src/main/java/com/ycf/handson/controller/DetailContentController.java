package com.ycf.handson.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.viewpager2.widget.ViewPager2;

import com.ycf.handson.R;
import com.ycf.handson.TagDetailActivity;
import com.ycf.handson.adapter.GalleryAdapter;
import com.ycf.handson.manager.MediaPreloadManager;
import com.ycf.handson.model.Clip;
import com.ycf.handson.model.Hashtag;
import com.ycf.handson.model.Post;
import com.ycf.handson.utils.DateUtil;

import java.util.List;

/**
 * 详情页内容区（Content）的逻辑控制器。
 * 负责媒体、内容文本（包括富文本渲染）、画廊和指示器。
 */
public class DetailContentController {

    private final Context context;
    private final MediaPreloadManager preloadManager;
    private final ExoPlayer player;
    private Boolean isPlay = true;

    // --- 内容区 Views ---
    private ViewPager2 viewPagerGallery;
    private LinearLayout layoutIndicators;
    private ImageView ivBgmIcon;
    private TextView tvPostTitle;
    private TextView tvPostContent;
    private TextView tvPostDate;

    public DetailContentController(Activity activity, MediaPreloadManager preloadManager, ExoPlayer player) {
        this.context = activity;
        this.preloadManager = preloadManager;
        this.player = player;
        initViews(activity);
        setupListeners();
    }

    private void initViews(Activity activity) {
        // 内容区
        viewPagerGallery = activity.findViewById(R.id.view_pager_gallery);
        layoutIndicators = activity.findViewById(R.id.indicator_container);
        ivBgmIcon = activity.findViewById(R.id.iv_bgm_icon);
        tvPostTitle = activity.findViewById(R.id.tv_post_title);
        tvPostContent = activity.findViewById(R.id.tv_post_content);
        tvPostDate = activity.findViewById(R.id.tv_post_date);
    }

    private void setupListeners() {
        // BGM 播放/暂停控制
        ivBgmIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPlay = !isPlay;
                player.setPlayWhenReady(isPlay);

                ivBgmIcon.setImageResource(isPlay ? R.drawable.ic_volume_up : R.drawable.ic_volume_off);
            }
        });
    }

    /**
     * 绑定 Post 数据，更新内容区 UI。
     */
    public void bind(Post post) {
        if (post == null) return;


        preloadManager.play(post.getMusic());
        tvPostTitle.setText(post.getTitle());

        renderPostContent(post, tvPostContent);
        tvPostDate.setText(DateUtil.formatRelativeTime(post.getCreate_time()));

        setupGallery(post.getClips());
    }

    // --- 富文本渲染逻辑 (从 Activity 移动到 Controller 内部) ---

    private void renderPostContent(Post post, TextView textView) {

        if (post == null || post.getContent() == null) {
            textView.setText("");
            return;
        }
        String content = post.getContent();
        List<Hashtag> hashtags = post.getHashtag();

        SpannableString spannableContent = new SpannableString(content);
        // 使用 Context 获取颜色
        int highlightColor = ContextCompat.getColor(context, R.color.hashtag_highlight);

        if (hashtags != null) {
            for (Hashtag tag : hashtags) {
                int start = tag.getStart();
                int end = tag.getEnd();

                if (start >= 0 && end <= content.length() && start < end) {
                    String tagText = content.substring(start, end);
                    // 使用内部方法获取 ClickableSpan
                    spannableContent.setSpan(getHashtagClickableSpan(tagText, highlightColor),
                            start,
                            end,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }

        textView.setText(spannableContent);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private ClickableSpan getHashtagClickableSpan(final String tagText, final int highlightColor) {
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

    // --- ViewPager2/指示器逻辑 (保持不变) ---

    private void setupGallery(List<Clip> clips) {
        if (clips != null && !clips.isEmpty()) {
            viewPagerGallery.setVisibility(View.VISIBLE);
            layoutIndicators.setVisibility(View.VISIBLE);

            GalleryAdapter adapter = new GalleryAdapter(context);
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
    }

    /**
     * 设置 ViewPager 或类似组件的指示器。
     *
     * @param count 指示器的总数。
     */
    private void setupIndicators(int count) {

        if (count <= 1) {
            layoutIndicators.setVisibility(View.GONE);
            return;
        }


        layoutIndicators.removeAllViews();

        final int margin3dpPx = (int) (context.getResources().getDisplayMetrics().density * 3);
        final int height3dpPx = (int) (context.getResources().getDisplayMetrics().density * 3);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                0,                          // width = 0dp, 配合 weight 实现等分宽度
                height3dpPx,                // height = 3dp 的像素值
                1.0f                        // weight = 1.0f, 实现平均分配宽度
        );


        params.setMargins(margin3dpPx / 2, 0, margin3dpPx / 2, 0);

        for (int i = 0; i < count; i++) {
            View indicator = new View(context);

            indicator.setLayoutParams(params);
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

    /**
     * 暂停媒体播放。供 Activity 生命周期调用。
     */
    public void pauseMedia() {
        if (player != null) {
            player.pause();
        }
    }


    public void resumeMedia() {
        if (player != null) {
            player.setPlayWhenReady(true);
        }
    }
}