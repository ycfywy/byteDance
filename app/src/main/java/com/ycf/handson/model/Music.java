package com.ycf.handson.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Music implements Parcelable {
    // 音量
    private int volume;
    // 起始播放位置（单位：ms）
    @SerializedName("seek_time")
    private int seekTime;
    // 音乐链接
    private String url;

    // --- Parcelable 实现 ---


    protected Music(Parcel in) {
        volume = in.readInt();
        seekTime = in.readInt();
        url = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(volume);
        dest.writeInt(seekTime);
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