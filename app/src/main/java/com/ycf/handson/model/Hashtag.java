package com.ycf.handson.model;

import lombok.Data;

// 对应 post.hashtag 列表中的每一个标签。
@Data
public class Hashtag {
    // 起始下标（闭区间）
    private int start;

    // 终止下标（开区间）
    private int end;
}