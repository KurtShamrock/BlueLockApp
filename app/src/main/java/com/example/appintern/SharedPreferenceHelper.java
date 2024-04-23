package com.example.appintern;


import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceHelper {

    private static final String SHARED_PREFS_FILE = "BlueLock";
    private final SharedPreferences sharedPreferences;

    public SharedPreferenceHelper(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
    }

    public void saveStringValue(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getStringValue(String key) {
        return sharedPreferences.getString(key, null);
    }
}
