package com.white.hot.doremember.utils;

import android.app.Activity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by ytf on 2016/12/8.
 * descption: activity管理类
 */

public class AppManager
{
    private AppManager(){}

    private static AppManager instance;
    private List<Activity> listActivities;

    public static AppManager getInstance()
    {
        if(instance == null)
        {
            synchronized (AppManager.class)
            {
                if(instance == null)
                {
                    instance = new AppManager();
                }
            }
        }
        return instance;
    }

    public void put(Activity activity)
    {
        if(listActivities == null)
        {
            listActivities = new ArrayList<>();
        }
        listActivities.add(activity);
    }

    /***
     * 弹出顶层activity，同时会从栈中移除，一般用于activity销毁时调用，慎用
     * @return
     */
    public void pop()
    {
        if(listActivities != null && listActivities.size() > 0)
        {
            int size = listActivities.size();
            if(size > 1)
            {
                listActivities.remove(size - 1);
            }else
            {
                listActivities.remove(0);
            }
        }
    }

    /***
     * 弹出指定位置activity
     * @param position 位置
     * @return
     */
    public Activity pop(int position)
    {
        if(listActivities != null && listActivities.size() > 0)
        {
            return listActivities.get(position);
        }
        return null;
    }

    /***
     * 弹出顶层activity，不会从栈中移除
     * @return
     */
    public Activity popTop()
    {
        if(listActivities != null && listActivities.size() > 0)
        {
            int size = listActivities.size();
            if(size > 1)
            {
                return listActivities.get(size - 1);
            }else
            {
                return listActivities.get(0);
            }
        }
        return null;
    }

    /***
     * 清空activity栈
     */
    public void termilate()
    {
        if(listActivities != null && listActivities.size() > 0)
        {
            for(Iterator<Activity> it = listActivities.iterator(); it.hasNext();)
            {
                Activity b = it.next();
                if(!b.isDestroyed())
                {
                    b.finish();
                }
                it.remove();
            }
        }
    }
}
