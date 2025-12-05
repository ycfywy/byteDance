package com.ycf.handson.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.Data;

@Data
public class FeedResponse {
    // 状态码（0：成功，其它：失败）
    @SerializedName("status_code")
    private int statusCode;

    // 是否还有更多作品（取值：0或1）
    @SerializedName("has_more")
    private int hasMore;
    // 作品列表
    @SerializedName("post_list")
    private List<Post> postList;
}
