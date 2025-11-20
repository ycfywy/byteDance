package com.ycf.handson.model;

import lombok.Data;

// 对应 post.clips 列表中的每一个片段。
@Data
public class Clip {
    // 片段类型（0：图片，1：视频）
    private int type;

    // 宽度
    private int width;

    // 高度
    private int height;

    // 资源链接
    private String url;
}
