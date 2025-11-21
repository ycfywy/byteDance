package com.ycf.handson.model;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import lombok.Data;


// 对应 post_list 中的每一个作品对象。
@Data
public class Post {
    // 作品ID
    private String post_id;

    // 作品标题
    private String title;

    // 作品正文
    private String content;

    // 作品创建时间戳
    private long create_time;

    // 作者信息对象
    private Author author;

    // 图片/视频片段数据列表
    private List<Clip> clips;

    // 音乐信息对象
    private Music music;

    // 话题标签列表
    private List<Hashtag> hashtag;

    // 点赞数
    private int like_count = ThreadLocalRandom.current().nextInt(1, 500);

}