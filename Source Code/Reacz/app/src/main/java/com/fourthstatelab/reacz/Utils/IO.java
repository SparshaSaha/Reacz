package com.fourthstatelab.reacz.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.fourthstatelab.reacz.Config;

/**
 * Created by sid on 7/25/17.
 */

public class IO {
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor sharedPreferenceEditor;
    private static SharedPreferences getSharedPreferences(Context context){
        if(sharedPreferences==null)
            return context.getSharedPreferences(Config.PREFERENCE_NAME,Context.MODE_PRIVATE);
        else
            return sharedPreferences;
    }

    private static SharedPreferences.Editor getSharedPreferenceEditor(Context context){
        if(sharedPreferenceEditor==null)
            return context.getSharedPreferences(Config.PREFERENCE_NAME,Context.MODE_PRIVATE).edit();
        else
            return sharedPreferenceEditor;
    }

    //STRING PREFERENCES
    public static void put(Context context,String key,String value){
        getSharedPreferenceEditor(context).putString(key,value);
    }
    public static String get(Context context,String key, String defaultValue){
        return getSharedPreferences(context).getString(key,defaultValue);
    }

    //INTEGER PREFERENCES
    public static void put(Context context,String key,int value){
        getSharedPreferenceEditor(context).putInt(key,value);
    }
    public static int get(Context context,String key, int defaultValue){
        return getSharedPreferences(context).getInt(key,defaultValue);
    }
}
