package com.ycf.handson.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import lombok.Data;

@Data
public class Post implements Parcelable {
    // 作品ID
    private String post_id;
    // 作品标题
    private String title;
    // 作品正文
    private String content;
    // 作品创建时间戳
    private long create_time;

    // Parcelable 对象
    private Author author;
    // Parcelable 列表
    private List<Clip> clips;
    // Parcelable 对象
    private Music music;
    // Parcelable 列表
    private List<Hashtag> hashtag;

    // 基本类型
    private int like_count = ThreadLocalRandom.current().nextInt(1, 500);
    private boolean isLiked;

    // --- Parcelable 实现 ---

    public Post() {
        // 确保有一个无参构造函数
    }

    protected Post(Parcel in) {
        post_id = in.readString();
        title = in.readString();
        content = in.readString();
        create_time = in.readLong();

        // 读取 Author (Parcelable)
        author = in.readParcelable(Author.class.getClassLoader());

        // 读取 List<Clip> (TypedList)
        clips = in.createTypedArrayList(Clip.CREATOR);

        // 读取 Music (Parcelable)
        music = in.readParcelable(Music.class.getClassLoader());

        // 读取 List<Hashtag> (TypedList)
        hashtag = in.createTypedArrayList(Hashtag.CREATOR);

        like_count = in.readInt();
        // Parcel 不支持直接读写 boolean，通常用 byte/int 替代，但 readByte/writeByte 已弃用
        isLiked = in.readInt() != 0; // 0 = false, 1 = true
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(post_id);
        dest.writeString(title);
        dest.writeString(content);
        dest.writeLong(create_time);

        // 写入 Author (Parcelable)
        dest.writeParcelable(author, flags);

        // 写入 List<Clip> (TypedList)
        dest.writeTypedList(clips);

        // 写入 Music (Parcelable)
        dest.writeParcelable(music, flags);

        // 写入 List<Hashtag> (TypedList)
        dest.writeTypedList(hashtag);

        dest.writeInt(like_count);
        dest.writeInt(isLiked ? 1 : 0); // 写入 boolean
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Post> CREATOR = new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };
}