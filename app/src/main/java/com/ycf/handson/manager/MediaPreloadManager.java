package com.ycf.handson.manager;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.media3.common.MediaItem;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.source.MediaSource;
import androidx.media3.exoplayer.source.preload.DefaultPreloadManager;
import androidx.media3.exoplayer.source.preload.TargetPreloadStatusControl;

import com.ycf.handson.model.Music;
import com.ycf.handson.model.Post;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import lombok.Getter;

/**
 * MediaPreloadManager 以单例模式实现，管理 ExoPlayer 的预加载逻辑。
 */

@OptIn(markerClass = UnstableApi.class)
public class MediaPreloadManager {

    // 1. 静态私有实例变量：用于保存唯一的单例实例
    private static MediaPreloadManager instance;

    /**
     * -- GETTER --
     * 获取 DefaultPreloadManager 实例，以便配置媒体项或控制预加载。
     */
    // 2. 核心成员变量：保存预加载管理器和播放器实例
    @Getter
    private final DefaultPreloadManager preloadManager;
    /**
     * -- GETTER --
     * 获取 ExoPlayer 实例，以便配置媒体项并开始播放。
     */
    @Getter
    private final ExoPlayer player;

    // 假设这是需要被外部更新的变量
    private int currentPlayingIndex = 5;


    private Map<String, MediaItem> postId2Media;

    // 3. 私有构造函数：阻止外部创建实例

    private MediaPreloadManager(@NonNull Context context) {

        // 预加载状态控制逻辑（匿名内部类）
        TargetPreloadStatusControl<Integer, DefaultPreloadManager.PreloadStatus> targetPreloadStatusControl =
                new TargetPreloadStatusControl<>() {
                    @Nullable
                    @Override
                    public DefaultPreloadManager.PreloadStatus getTargetPreloadStatus(Integer index) {

                        return DefaultPreloadManager.PreloadStatus.specifiedRangeLoaded(3000L);

                    }
                };

        DefaultPreloadManager.Builder builder = new DefaultPreloadManager.Builder(context, targetPreloadStatusControl);
        this.preloadManager = builder.build();
        this.player = builder.buildExoPlayer();
        this.postId2Media = new HashMap<>();


    }

    public static synchronized MediaPreloadManager getInstance(@NonNull Context context) {
        if (instance == null) {
            // 只有第一次调用时才创建实例
            instance = new MediaPreloadManager(context.getApplicationContext());
        }
        return instance;
    }


    public void play(Music music) {

        MediaItem mediaItem = MediaItem.fromUri(music.getUrl());
        MediaSource mediaSource = preloadManager.getMediaSource(mediaItem);
        if (mediaSource != null) {
            player.setMediaSource(mediaSource);

        } else {
            player.setMediaItem(mediaItem);
        }
        player.prepare();
        player.seekTo(music.getSeek_time());
        player.play();
    }

    public void pause() {

        player.pause();
    }

    public void release() {
        preloadManager.release();
    }


    public void preloadMedia(List<Post> post) {

        post.forEach(new Consumer<Post>() {
            @Override
            public void accept(Post post) {
                if (!postId2Media.containsKey(post.getPost_id())) {
                    postId2Media.put(post.getPost_id(), MediaItem.fromUri(post.getMusic().getUrl()));
                    preloadManager.add(Objects.requireNonNull(postId2Media.get(post.getPost_id())), 0);
                }
            }
        });

        Set<String> curCollect = post.stream().map(Post::getPost_id).collect(Collectors.toSet());
        Iterator<Map.Entry<String, MediaItem>> iterator = postId2Media.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, MediaItem> entry = iterator.next();
            String postId = entry.getKey();
            MediaItem mediaItem = entry.getValue();
            if (!curCollect.contains(postId)) {
                preloadManager.remove(mediaItem);
                iterator.remove();
            }
        }

        preloadManager.invalidate();
    }


}