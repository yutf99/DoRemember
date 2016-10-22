package com.white.hot.doremember.utils;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.SharedPreferencesCompat;

import com.white.hot.doremember.base.BaseApplication;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/***
 * created by ytf on 2016-09-26
 */

public class PreferenceUtils {
    
    public static String PREFERENCE_NAME = "pf";

    public static void put(String key, Object object)
    {

        SharedPreferences sp = BaseApplication.getGlobalContext()
                .getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        if (object instanceof String)
        {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer)
        {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean)
        {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float)
        {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long)
        {
            editor.putLong(key, (Long) object);
        } else
        {
            editor.putString(key, object.toString());
        }

        editor.commit();
    }

    public static void put(String prefName, String key, Object object)
    {

        SharedPreferences sp = BaseApplication.getGlobalContext()
                .getSharedPreferences(prefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        if (object instanceof String)
        {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer)
        {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean)
        {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float)
        {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long)
        {
            editor.putLong(key, (Long) object);
        } else
        {
            editor.putString(key, object.toString());
        }

        editor.commit();
    }

    public static Object get(String key, Object defaultObject)
    {
        SharedPreferences sp = BaseApplication.getGlobalContext()
                .getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);

        if (defaultObject instanceof String)
        {
            return sp.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer)
        {
            return sp.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean)
        {
            return sp.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float)
        {
            return sp.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long)
        {
            return sp.getLong(key, (Long) defaultObject);
        }
        return null;
    }

    public static Object get(String prefName, String key, Object defaultObject)
    {
        SharedPreferences sp = BaseApplication.getGlobalContext()
                .getSharedPreferences(prefName, Context.MODE_PRIVATE);

        if (defaultObject instanceof String)
        {
            return sp.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer)
        {
            return sp.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean)
        {
            return sp.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float)
        {
            return sp.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long)
        {
            return sp.getLong(key, (Long) defaultObject);
        }
        return null;
    }

    /***
     * 移除对应的键
     * @param key 键的名称
     */
    public static void removeKey(String key)
    {
        SharedPreferences sp = BaseApplication.getGlobalContext()
                .getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.commit();
    }

    /***
     * 移除所有的键
     */
    public static void removeAllKey()
    {
        SharedPreferences sp = BaseApplication.getGlobalContext()
                .getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        Map<String,?> map = sp.getAll();
        Set<String> set = map.keySet();
        for(Iterator<String> it = set.iterator();it.hasNext();){
            String key = it.next();
            removeKey(key);
        }
    }

    /***
     * 移除对应的键
     * @param key 键的名称
     */
    public static void removeKey(String prefName, String key)
    {
        SharedPreferences sp = BaseApplication.getGlobalContext()
                .getSharedPreferences(prefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.commit();
    }

    /***
     * 移除所有的键
     */
    public static void removeAllKey(String prefName)
    {
        SharedPreferences sp = BaseApplication.getGlobalContext()
                .getSharedPreferences(prefName, Context.MODE_PRIVATE);
        Map<String,?> map = sp.getAll();
        Set<String> set = map.keySet();
        for(Iterator<String> it = set.iterator();it.hasNext();){
            String key = it.next();
            removeKey(prefName, key);
        }
    }

    /**
     * 清除所有数据
     */
    public static void clear()
    {
        SharedPreferences sp = BaseApplication.getGlobalContext()
                .getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.commit();
    }

    /**
     * 清除所有数据
     */
    public static void clear(String prefName)
    {
        SharedPreferences sp = BaseApplication.getGlobalContext()
                .getSharedPreferences(prefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.commit();
    }
}
