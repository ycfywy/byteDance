package com.ycf.handson.manager;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class LikeStatusManager {
    private static final String PREFS_NAME = "LikePrefs";
    private static final String KEY_LIKED_POST_IDS = "liked_post_ids";

    private final SharedPreferences prefs;

    public LikeStatusManager(Context context) {

        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /**
     *
     *
     *
     * @param postId
     * @return
     */
    public boolean isLiked(String postId) {
        Set<String> s = prefs.getStringSet(KEY_LIKED_POST_IDS, new HashSet<>());
        return s.contains(postId);
    }


    /**
     *
     *
     *
     * @param postId
     * @param isLiked
     * @return
     */
    public boolean toggleLike(String postId, boolean isLiked) {
        Set<String> s = prefs.getStringSet(KEY_LIKED_POST_IDS, new HashSet<>());
        Set<String> newLikedIds = new HashSet<>(s);

        if (isLiked) {
            newLikedIds.add(postId);
        } else {
            newLikedIds.remove(postId);
        }

        prefs.edit().putStringSet(KEY_LIKED_POST_IDS, newLikedIds).apply();
        return isLiked;

    }


}
