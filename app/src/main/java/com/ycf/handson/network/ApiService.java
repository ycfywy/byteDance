package com.ycf.handson.network;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.ycf.handson.model.FeedResponse;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ApiService {
    private static final String TAG = "ApiService";
    private static final String BASE_URL = "https://college-training-camp.bytedance.com/feed/";
    private final OkHttpClient client;
    private final Gson gson;


    public interface FeedCallback {
        void onSuccess(FeedResponse response);

        void onFailure(String errorMessage);
    }

    public ApiService() {
        this.client = new OkHttpClient();
        this.gson = new Gson();
    }

    /**
     *
     * @param count           请求作品数量
     * @param acceptVideoClip 是否支持下发视频片段
     * @param callback        回调函数
     */
    public void fetchFeed(int count, boolean acceptVideoClip, final FeedCallback callback) {

        // 构造url
        HttpUrl url = Objects.requireNonNull(HttpUrl.parse(BASE_URL)).newBuilder()
                .addQueryParameter("count", String.valueOf(count))
                .addQueryParameter("accept_video_clip", acceptVideoClip ? "true" : "false")
                .build();


        Request request = new Request.Builder().url(url).build();

        // 异步请求 设置call back
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "Network request failed: " + e.getMessage());
                callback.onFailure("网络请求失败: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String json = response.body().string();
                        Log.d(TAG, "Received JSON: " + json);
                        System.out.println("==========" + json);
                        FeedResponse feedResponse = gson.fromJson(json, FeedResponse.class);
                        if (feedResponse.getStatusCode() == 0) {
                            Log.i(TAG, "Data fetched successfully. Posts count: " + feedResponse.getPostList().size());
                            callback.onSuccess(feedResponse);
                        } else {
                            String errorMsg = "接口状态码错误: " + feedResponse.getStatusCode();
                            Log.w(TAG, errorMsg);
                            callback.onFailure(errorMsg);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "JSON parsing error: " + e.getMessage(), e);
                        callback.onFailure("JSON 解析失败或数据模型不匹配: " + e.getMessage());
                    }
                } else {
                    // HTTP 状态码非 2xx 或响应体为空
                    String errorMsg = "HTTP Error: " + response.code();
                    Log.e(TAG, errorMsg);
                    callback.onFailure(errorMsg);
                }

            }
        });
    }
}
