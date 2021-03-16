package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

class MyProjectSharedPreference {
    
    private static final String TAG = "MyProjectSharedPreference";
    private SharedPreferences prefs;

    MyProjectSharedPreference(Activity activity) {
        super();

        Log.d(TAG, "MyProjectSharedPreference: C'tor");
        prefs = activity.getSharedPreferences(activity.getString(R.string.prefsFileKey), Context.MODE_PRIVATE);
    }

    void save(String key, String text) {
        Log.d(TAG, "save: " + key + ":" + text);
        Editor editor = prefs.edit(); // whenever we change a function we need to call editor function to make changes
        editor.putString(key, text);
        editor.apply(); // commit T/F
    }


    String getValue(String key) {
        String text = prefs.getString(key, ""); // if the key is empty returns "" (empty)
        Log.d(TAG, "getValue: " + key + " = " + text);
        return text;
    }


    void clearAll() {
        Log.d(TAG, "clearAll: ");
        Editor editor = prefs.edit();
        editor.clear();
        editor.apply(); // commit T/F
    }

    void removeValue(String key) {
        Log.d(TAG, "removeValue: " + key);
        Editor editor = prefs.edit();
        editor.remove(key);
        editor.apply(); // commit T/F
    }
}