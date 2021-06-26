package com.example.luckyleaf.repo;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import com.example.luckyleaf.myApp;

public class SharedPrefs {
    public static final String sensorIndex                     = "sensorIndex";

    private static SharedPrefs ourInstance = null;
    private SharedPreferences prefs;
    public static SharedPrefs instance(myApp app) {
        if (ourInstance == null)
        {
            ourInstance = new SharedPrefs(app);
        }
        return ourInstance;
    }

    private SharedPrefs(myApp app) {
        prefs = app.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
    }

    public String getString(@NonNull String prefsKey, String defValue){
        return prefs.getString(prefsKey, defValue);
    }

    public void putString(@NonNull String prefsKey, String value){
        prefs.edit().putString(prefsKey, value).apply();
    }

    public boolean getBoolean(@NonNull String prefsKey, boolean defValue)
    {
        return prefs.getBoolean(prefsKey, defValue);
    }
    public void putInt(@NonNull String prefsKey, int value){
        prefs.edit().putInt(prefsKey, value).apply();
    }

    public int getInt(@NonNull String prefsKey, int defValue)
    {
        return prefs.getInt(prefsKey, defValue);
    }
    public void putBoolean(@NonNull String prefsKey, boolean value)
    {
        prefs.edit().putBoolean(prefsKey, value).apply();
    }
}
