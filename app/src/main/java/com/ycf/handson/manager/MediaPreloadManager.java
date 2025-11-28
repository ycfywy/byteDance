package com.ycf.handson.manager;

import static java.lang.Math.abs;

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

import java.util.List;
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

    // 3. 私有构造函数：阻止外部创建实例

    private MediaPreloadManager(@NonNull Context context) {

        // 预加载状态控制逻辑（匿名内部类）
        TargetPreloadStatusControl<Integer, DefaultPreloadManager.PreloadStatus> targetPreloadStatusControl =
                new TargetPreloadStatusControl<>() {
                    @Nullable
                    @Override
                    public DefaultPreloadManager.PreloadStatus getTargetPreloadStatus(Integer index) {

                        // 获取成员变量 currentPlayingIndex
                        int distance = index - currentPlayingIndex;

                        if (distance == 1) {
                            return DefaultPreloadManager.PreloadStatus.specifiedRangeLoaded(5000L);
                        } else if (distance == -1) {
                            return DefaultPreloadManager.PreloadStatus.specifiedRangeLoaded(3000L);
                        } else if (distance == 2) {
                            return DefaultPreloadManager.PreloadStatus.TRACKS_SELECTED;
                        } else if (abs(distance) <= 4) {
                            return DefaultPreloadManager.PreloadStatus.SOURCE_PREPARED;
                        }

                        return null;
                    }
                };

        DefaultPreloadManager.Builder builder = new DefaultPreloadManager.Builder(context, targetPreloadStatusControl);
        this.preloadManager = builder.build();
        this.player = builder.buildExoPlayer();


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


    public void preloadMedia(List<Music> musics) {


        List<MediaItem> mediaItems = musics.stream().map(music -> {
            return MediaItem.fromUri(music.getUrl());
        }).collect(Collectors.toList());

        preloadManager.reset();

        for (int i = 0; i < mediaItems.size(); i++) {
            MediaItem mediaItem = mediaItems.get(i);
            preloadManager.add(mediaItem, i);
        }
        currentPlayingIndex = mediaItems.size() / 2;
        preloadManager.invalidate();
    }


}