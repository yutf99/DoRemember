package com.white.hot.doremember.bean;

/**
 * Created by ytf on 2016/12/9.
 * descption:
 */

public class CrashLog
{
    private int id;
    private byte log_type;
    private String log_time;
    private String log_url;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public byte getLog_type()
    {
        return log_type;
    }

    public void setLog_type(byte log_type)
    {
        this.log_type = log_type;
    }

    public String getLog_time()
    {
        return log_time;
    }

    public void setLog_time(String log_time)
    {
        this.log_time = log_time;
    }

    public String getLog_url()
    {
        return log_url;
    }

    public void setLog_url(String log_url)
    {
        this.log_url = log_url;
    }
}
