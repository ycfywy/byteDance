package com.ycf.handson.manager;

import android.media.MediaPlayer;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.ycf.handson.R;
import com.ycf.handson.model.Music;

import java.io.IOException;

public class BgmManager {

    private static final String TAG = "BgmManager";

    private final ImageView bgmIconView;

    private Music currentMusic;
    private MediaPlayer mediaPlayer;

    private final int playIconResId = R.drawable.ic_volume_up;
    private final int pauseIconResId = R.drawable.ic_volume_off; // 暂停/静音图标

    public BgmManager(ImageView bgmIconView, Music music) {

        this.bgmIconView = bgmIconView;
        this.currentMusic = music;

        init();
    }

    public static BgmManager create(ImageView bgmIconView, Music music) {
        return new BgmManager(bgmIconView, music);
    }

    public void init() {

        release();
        if (currentMusic == null || currentMusic.getUrl() == null || currentMusic.getUrl().isEmpty()) {
            bgmIconView.setVisibility(View.GONE);
            return;
        }
        initializePlayer();
        bgmIconView.setVisibility(View.VISIBLE);
        bgmIconView.setOnClickListener(v -> togglePlayback());
    }

    private void togglePlayback() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            pause();
        } else {
            play();
        }
    }


    public void play() {
        if (currentMusic == null) return;

        if (mediaPlayer == null) {
            initializePlayer();
        }
        // 如果播放器已经准备好，直接开始/继续
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    public void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            bgmIconView.setImageResource(pauseIconResId); // 切换为播放图标
        }
    }

    private void initializePlayer() {

        mediaPlayer = new MediaPlayer();

        try {
            Log.d(TAG, currentMusic.toString());
            mediaPlayer.setDataSource(currentMusic.getUrl());

            // 从网络中加载资源 设置为异步
            mediaPlayer.prepareAsync();

            // 设置准备好之后的回调
            mediaPlayer.setOnPreparedListener(mp -> {

                Log.d(TAG, "MediaPlayer prepared. Starting playback.");

                mp.start();
                int seekTime = currentMusic.getSeek_time();
                Log.d(TAG, currentMusic.toString());

                if (seekTime > 0) {
                    mp.seekTo(seekTime);
                }
                mp.setLooping(true);

            });

            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                Log.e(TAG, "Playback error: " + what);
                release(); // 发生错误时释放资源
                return true;
            });
        } catch (IOException e) {
            Log.e(TAG, "Error setting data source: ", e);
            release();
        }
    }

    public void release() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }

        bgmIconView.setImageResource(playIconResId);
    }

}
