package com.ycf.handson.model;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Data;

@Data
public class Hashtag implements Parcelable {
    // 起始下标（闭区间）
    private int start;
    // 终止下标（开区间）
    private int end;

    // --- Parcelable 实现 ---

    public Hashtag() {
    }

    protected Hashtag(Parcel in) {
        start = in.readInt();
        end = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(start);
        dest.writeInt(end);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Hashtag> CREATOR = new Creator<Hashtag>() {
        @Override
        public Hashtag createFromParcel(Parcel in) {
            return new Hashtag(in);
        }

        @Override
        public Hashtag[] newArray(int size) {
            return new Hashtag[size];
        }
    };
}