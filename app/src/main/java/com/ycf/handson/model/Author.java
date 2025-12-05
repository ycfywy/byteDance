package com.ycf.handson.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Author implements Parcelable {
    // 用户ID
    @SerializedName("user_id")
    private String userId;
    // 用户昵称
    private String nickname;
    // 头像链接
    private String avatar;

    // --- Parcelable 实现 ---


    protected Author(Parcel in) {
        userId = in.readString();
        nickname = in.readString();
        avatar = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
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