package com.conem.app.findmeacoursedal.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by mj on 5/26/2017.
 */

public class ProjectUtil {

    private static final String SET_KEY = "set_key";

    public static void setSharedSet(Context context, Set<String> savedSet) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putStringSet(SET_KEY, savedSet).apply();
    }

    public static Set<String> getSharedPreferencesString(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getStringSet(SET_KEY, new HashSet<String>());
    }
}
