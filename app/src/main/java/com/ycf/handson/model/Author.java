package com.ycf.handson.model;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Data;

@Data
public class Author implements Parcelable {
    // 用户ID
    private String user_id;
    // 用户昵称
    private String nickname;
    // 头像链接
    private String avatar;

    // --- Parcelable 实现 ---

    public Author() {
        // 无参构造函数，Lombok 生成或手动添加
    }

    protected Author(Parcel in) {
        user_id = in.readString();
        nickname = in.readString();
        avatar = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(user_id);
        dest.writeString(nickname);
        dest.writeString(avatar);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Author> CREATOR = new Creator<Author>() {
        @Override
        public Author createFromParcel(Parcel in) {
            return new Author(in);
        }

        @Override
        public Author[] newArray(int size) {
            return new Author[size];
        }
    };
}