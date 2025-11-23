package com.ycf.handson.model;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Data;

@Data
public class Music implements Parcelable {
    // 音量
    private int volume;
    // 起始播放位置（单位：ms）
    private int seek_time;
    // 音乐链接
    private String url;

    // --- Parcelable 实现 ---

    public Music() {
    }

    protected Music(Parcel in) {
        volume = in.readInt();
        seek_time = in.readInt();
        url = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(volume);
        dest.writeInt(seek_time);
        dest.writeString(url);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Music> CREATOR = new Creator<Music>() {
        @Override
        public Music createFromParcel(Parcel in) {
            return new Music(in);
        }

        @Override
        public Music[] newArray(int size) {
            return new Music[size];
        }
    };
}