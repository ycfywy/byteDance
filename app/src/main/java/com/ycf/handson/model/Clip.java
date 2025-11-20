package com.ycf.handson.model;

// 对应 post.clips 列表中的每一个片段。
public class Clip {
    // 片段类型（0：图片，1：视频）
    public int type;

    // 宽度
    public int width;

    // 高度
    public int height;

    // 资源链接
    public String url;
}
