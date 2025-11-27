package com.ycf.handson.manager;

import static java.lang.Math.abs;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.source.MediaSource;
import androidx.media3.exoplayer.source.preload.DefaultPreloadManager;
import androidx.media3.exoplayer.source.preload.TargetPreloadStatusControl;

import java.util.List;

import lombok.Getter;

/**
 * MediaPreloadManager 以单例模式实现，管理 ExoPlayer 的预加载逻辑。
 */
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

    // 4. 静态公共获取方法：提供全局访问点
    // 采用 synchronized 确保在多线程环境下的线程安全 (Thread Safety)
    public static synchronized MediaPreloadManager getInstance(@NonNull Context context) {
        if (instance == null) {
            // 只有第一次调用时才创建实例
            instance = new MediaPreloadManager(context.getApplicationContext());
        }
        return instance;
    }


    public void play(MediaItem mediaItem) {

        MediaSource mediaSource = preloadManager.getMediaSource(mediaItem);

        if (mediaSource != null) {
            player.setMediaSource(mediaSource);

        } else {
            player.setMediaItem(mediaItem);
        }
        player.prepare();
        player.play();
    }

    public void pause() {

        player.pause();
    }

    public void release() {
        preloadManager.release();
    }


    public void setPreloadMediaItems(@NonNull List<MediaItem> mediaItems, int startWindowIndex, long startPositionMs) {

        // 1. 设置预加载列表
        // 注意：DefaultPreloadManager 的 setMediaItems 并不直接支持 startWindowIndex/startPositionMs，
        // 但我们会将这些信息用于 ExoPlayer 的设置。
        // DefaultPreloadManager 依赖于其 setCurrentPlayingIndex 来驱动预加载。

        // 由于 DefaultPreloadManager 没有 setMediaItems(List) 的方法，
        // 最好的做法是使用其内部的 add/remove 逻辑，或者像下面这样，先移除所有，再添加所有。

        // 清空旧的预加载任务
        preloadManager.reset();

        // 循环添加所有媒体项。索引 i 作为 rankingData 传入。
        for (int i = 0; i < mediaItems.size(); i++) {
            preloadManager.add(mediaItems.get(i), i);
        }

        // 2. 同时将媒体列表设置给播放器
        player.setMediaItems(mediaItems, startWindowIndex, startPositionMs);

        // 3. 更新当前播放索引，触发预加载（通常从 startWindowIndex 开始）
        updateCurrentPlayingIndex(startWindowIndex);

        // 4. 通知 PreloadManager 重新评估（虽然 setCurrentPlayingIndex 内部可能已经触发）
        preloadManager.invalidate();
    }

    public void preloadMedia(List<MediaItem> mediaItems) {

        for (int i = 0; i < mediaItems.size(); i++) {
            MediaItem mediaItem = mediaItems.get(i);
            preloadManager.add(mediaItem, i);
        }

        preloadManager.invalidate();
    }

    public void updateCurrentPlayingIndex(int newIndex) {
        if (this.currentPlayingIndex != newIndex) {
            this.currentPlayingIndex = newIndex;
            this.preloadManager.setCurrentPlayingIndex(newIndex);
            this.preloadManager.invalidate();
        }
    }

}