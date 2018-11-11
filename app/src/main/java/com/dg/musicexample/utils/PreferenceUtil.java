package com.dg.musicexample.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.dg.musicexample.player.PlayMode;
/**
 * Created by Giang Long on 11/8/2018.
 * Skype: gianglong7695@gmail.com (id: gianglong7695_1)
 * Phone: 0979 579 283
 */
public class PreferenceUtil {
    private static final String PREFS_NAME = "preference";
    private static final String PLAY_MODE = "play_mode";

    private static SharedPreferences preferences(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    private static SharedPreferences.Editor edit(Context context) {
        return preferences(context).edit();
    }


    public static PlayMode getPlayMode(Context context) {
        String playModeName = preferences(context).getString(PLAY_MODE, null);
        if (playModeName != null) {
            return PlayMode.valueOf(playModeName);
        }
        return PlayMode.getDefault();
    }

    public static void setPlayMode(Context context, PlayMode playMode) {
        edit(context).putString(PLAY_MODE, playMode.name()).commit();
    }

}
