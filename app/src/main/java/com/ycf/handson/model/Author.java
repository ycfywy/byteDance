package com.ycf.handson.model;

import lombok.Data;

@Data
public class Author {
    // 用户ID
    private String user_id;

    // 用户昵称
    private String nickname;

    // 头像链接
    private String avatar;
}
