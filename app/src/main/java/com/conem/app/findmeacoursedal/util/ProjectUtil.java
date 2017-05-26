package com.conem.app.findmeacoursedal.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by mj on 5/26/2017.
 */

public class ProjectUtil {

    private static final String SET_KEY = "set_key";
    protected static final String DAL_PREFERENCE = "DAL_PREFERENCE";

    public static void setSharedSet(Context context, Set<String> savedSet) {
        context.getSharedPreferences(DAL_PREFERENCE, Context.MODE_PRIVATE).edit().clear().apply();
        context.getSharedPreferences(DAL_PREFERENCE, Context.MODE_PRIVATE).edit()
                .putStringSet(SET_KEY, savedSet).apply();
    }

    public static Set<String> getSharedPreferencesString(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(DAL_PREFERENCE, Context.MODE_PRIVATE);
        return sharedPreferences.getStringSet(SET_KEY, new HashSet<>());
    }

    /**
     * Read url text
     *
     * @param url url to read
     * @return return Url body in string
     */
    public static String readUrl(String url) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            URL url1 = new URL(url);
            String inputLine;
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(url1.openStream()));
            while ((inputLine = in.readLine()) != null)
                stringBuilder.append(inputLine);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return stringBuilder.toString();
    }
}
