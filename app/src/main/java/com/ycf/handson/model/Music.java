package com.ycf.handson.model;

import lombok.Data;

// 对应 post.music 对象。
@Data
public class Music {
    // 音量
    private int volume;

    // 起始播放位置（单位：ms）
    private int seek_time;

    // 音乐链接
    private String url;
}