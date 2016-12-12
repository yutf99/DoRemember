package com.white.hot.doremember.bean;

/**
 * Created by ytf on 2016/12/9.
 * descption:
 */

public class BaseResp<T>
{
    private int result;
    private String msg;
    private T data;

    public int getResult()
    {
        return result;
    }

    public void setResult(int result)
    {
        this.result = result;
    }

    public String getMsg()
    {
        return msg;
    }

    public void setMsg(String msg)
    {
        this.msg = msg;
    }

    public T getData()
    {
        return data;
    }

    public void setData(T data)
    {
        this.data = data;
    }
}
