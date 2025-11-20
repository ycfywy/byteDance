package com.ycf.handson.model;

import java.util.List;

// 对应 post_list 中的每一个作品对象。
public class Post {
    // 作品ID
    public String post_id;

    // 作品标题
    public String title;

    // 作品正文
    public String content;

    // 作品创建时间戳
    public long create_time;

    // 作者信息对象
    public Author author;

    // 图片/视频片段数据列表
    public List<Clip> clips;

    // 音乐信息对象
    public Music music;

    // 话题标签列表
    public List<Hashtag> hashtag;
}