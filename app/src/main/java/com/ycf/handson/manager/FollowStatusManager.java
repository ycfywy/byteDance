// com.ycf.handson.manager.FollowStatusManager.java
package com.ycf.handson.manager;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * FollowStatusManager 负责在本地 SharedPreferences 中管理用户的关注状态。
 */
public class FollowStatusManager {

    private static final String PREF_NAME = "FollowStatusPrefs";
    private final SharedPreferences prefs;

    public FollowStatusManager(Context context) {
        // 使用 PRIVATE 模式创建 SharedPreferences 文件
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    /**
     * 检查某个作者是否已被关注
     *
     * @param authorId 作者的唯一ID
     * @return 如果已关注返回 true，否则返回 false (默认未关注)
     */
    public boolean isFollowing(String authorId) {
        return prefs.getBoolean(authorId, false);
    }

    /**
     * 切换作者的关注状态
     *
     * @param authorId 作者的唯一ID
     * @return 切换后的新状态 (true: 关注, false: 未关注)
     */
    public boolean toggleFollowStatus(String authorId) {
        boolean currentStatus = isFollowing(authorId);
        boolean newStatus = !currentStatus;

        prefs.edit()
                .putBoolean(authorId, newStatus)
                .apply(); // 使用 apply 异步保存，避免阻塞主线程

        return newStatus;
    }
}