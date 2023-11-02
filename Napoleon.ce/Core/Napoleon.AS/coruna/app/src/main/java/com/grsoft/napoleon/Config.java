package com.grsoft.napoleon;

import android.content.Context;
import android.content.SharedPreferences;

public class Config {
    static final String SP = "sh_pref";
    static final String URI_GOOD = "good_uri";
    static final String URI_BAD = "bad_uri";

    public static String goodUri = "";
    public static String badUri = "";

    public static void load(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP, Context.MODE_PRIVATE);
        goodUri = sp.getString(URI_GOOD, "");
        badUri = sp.getString(URI_BAD, "");
    }

    public static void save(Context context) {
        SharedPreferences.Editor e = context.getSharedPreferences(SP, Context.MODE_PRIVATE).edit();
        e.putString(URI_GOOD, goodUri);
        e.putString(URI_BAD, badUri);
        e.commit();
    }
}
