package com.ycf.handson.model;

import java.util.List;

public class FeedResponse {
    // 状态码（0：成功，其它：失败）
    public int status_code;

    // 是否还有更多作品（取值：0或1）
    public int has_more;
    // 作品列表
    public List<Post> post_list;
}
