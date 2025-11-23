package com.ycf.handson.model;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Data;

@Data
public class Clip implements Parcelable {
    // 片段类型（0：图片，1：视频）
    private int type;
    // 宽度
    private int width;
    // 高度
    private int height;
    // 资源链接
    private String url;

    // --- Parcelable 实现 ---

    public Clip() {
    }

    protected Clip(Parcel in) {
        type = in.readInt();
        width = in.readInt();
        height = in.readInt();
        url = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(type);
        dest.writeInt(width);
        dest.writeInt(height);
        dest.writeString(url);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Clip> CREATOR = new Creator<Clip>() {
        @Override
        public Clip createFromParcel(Parcel in) {
            return new Clip(in);
        }

        @Override
        public Clip[] newArray(int size) {
            return new Clip[size];
        }
    };
}