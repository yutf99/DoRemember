package com.white.hot.doremember.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 网络请求工具类
 */
public class NetUtil
{

    public static boolean isNetworkAvailable(Context context)
    {
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null)
        {
            return false;
        } else
        {
            // 获取NetworkInfo对象
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

            if (networkInfo != null && networkInfo.length > 0)
            {
                for (int i = 0; i < networkInfo.length; i++)
                {
                    // 判断当前网络状态是否为连接状态
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static String GetNetworkType(Context context)
    {
        String strNetworkType = "未连接";

        NetworkInfo networkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
        {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI)
            {
                strNetworkType = "WIFI";
                WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                if (manager != null)
                {
                    WifiInfo info = manager.getConnectionInfo();
                    int rssi = info.getRssi();
                    strNetworkType += "\u3000WIFI信号强度：[" + (100 - Math.abs(rssi)) + "]";
//                    if(rssi >= -50 && rssi <=0)
//                    {
//                        strNetworkType += "\u3000信号强度："+"[良好]";
//                    }else if(rssi >= -70 && rssi < -50)
//                    {
//                        strNetworkType += "\u3000信号强度："+"[中等]";
//                    }else if(rssi >= -100 && rssi < -70)
//                    {
//                        strNetworkType += "\u3000信号强度："+"[差]";
//                    }
                }
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE)
            {
                String _strSubTypeName = networkInfo.getSubtypeName();

                int networkType = networkInfo.getSubtype();
                switch (networkType)
                {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN: //api<8 : replace by 11
                        strNetworkType = "2G";
                        break;
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
                    case TelephonyManager.NETWORK_TYPE_EHRPD:  //api<11 : replace by 12
                    case TelephonyManager.NETWORK_TYPE_HSPAP:  //api<13 : replace by 15
                        strNetworkType = "3G";
                        break;
                    case TelephonyManager.NETWORK_TYPE_LTE:    //api<11 : replace by 13
                        strNetworkType = "4G";
                        break;
                    default:
                        if (_strSubTypeName.equalsIgnoreCase("TD-SCDMA") || _strSubTypeName.equalsIgnoreCase("WCDMA") || _strSubTypeName.equalsIgnoreCase("CDMA2000"))
                        {
                            strNetworkType = "3G";
                        } else
                        {
                            strNetworkType = _strSubTypeName;
                        }

                        break;
                }

            }
        }
        return strNetworkType;
    }

    private static int timeout = 8;
    private static OkHttpClient clientDefault = new OkHttpClient.Builder().connectTimeout(timeout, TimeUnit.SECONDS).build();
    private static OkHttpClient clientCustom = new OkHttpClient.Builder().connectTimeout(timeout, TimeUnit.SECONDS).build();
    private static Request simpleGetReq;

    public static final String NET_FAILED_MSG = "连接异常！";
    public static final String NET_FAILED_SIMPLE_MSG_KEY = "simpleMessage";
    public static final String NET_FAILED_DETAIL_MSG_KEY = "detailMessage";
    public static final String NET_OK_MSG_KEY = "Message";
    public static final String NET_OK_CONTENT_KEY = "content";
    public static final String NET_CODE = "code";
    public static final int WHAT_NET_FAILED = 0x09;
    public static final int WHAT_NET_OK = 0x10;
    public static final int WHAT_NET_EXCEPTION = 0x11;

    /***
     * <p><b>发送一个GET请求，结果通过handler返回，数据放在bundle中</b></p><br/>
     * <b>成功返回</b>     (1).[消息(String) <b>NET_OK_MSG_KEY</b>(bundle中的key,后面大写都是key)] 2.[内容(String) <b>NET_OK_CONTENT_KEY</b>]<br/>
     * <b>连接异常返回</b> (2).[简单的消息(String) <b>NET_FAILED_SIMPLE_MSG_KEY</b>] 2.[详细消息(String) <b>NET_FAILED_DETAIL_MSG_KEY</b>]<br/>
     * <b>404,505之类的错误返回</b> (3).[错误代码(int) <b>NET_CODE</b>] 2.[简单信息(String) <b>NET_FAILED_SIMPLE_MSG_KEY</b>]<br/>
     *
     * @param url         链接地址
     * @param connTimeOut 超时时间
     * @param handler     消息处理
     */
    public static void doGet(String url, int connTimeOut, final Handler handler)
    {
        if (clientCustom == null)
        {
            clientCustom = new OkHttpClient.Builder().connectTimeout(connTimeOut, TimeUnit.SECONDS).build();
        } else
        {
            if (connTimeOut != timeout)
            {
                clientCustom = clientCustom.newBuilder().connectTimeout(connTimeOut, TimeUnit.SECONDS).build();
            }
        }
        if (simpleGetReq == null)
        {
            simpleGetReq = new Request.Builder().get().url(url).build();
        } else
        {
            simpleGetReq = simpleGetReq.newBuilder().url(url).build();
        }
        Call call = clientCustom.newCall(simpleGetReq);
        call.enqueue(new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                if (!call.isCanceled())
                {
                    call.cancel();
                }
                String s = e.getMessage();
                Message msg = handler.obtainMessage();
                msg.what = WHAT_NET_FAILED;
                Bundle bundle = new Bundle();
                bundle.putString(NET_FAILED_SIMPLE_MSG_KEY, NET_FAILED_MSG);
                bundle.putString(NET_FAILED_DETAIL_MSG_KEY, s);
                msg.setData(bundle);
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                if (response.isSuccessful())
                {
                    String message = response.message();
                    String content = response.body().string();
                    Message msg = handler.obtainMessage();
                    msg.what = WHAT_NET_OK;
                    Bundle bundle = new Bundle();
                    bundle.putString(NET_OK_MSG_KEY, message);
                    bundle.putString(NET_OK_CONTENT_KEY, content);
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                } else
                {
                    int code = response.code();
                    String message = response.message();
                    Message msg = handler.obtainMessage();
                    msg.what = WHAT_NET_EXCEPTION;
                    Bundle bundle = new Bundle();
                    bundle.putString(NET_FAILED_SIMPLE_MSG_KEY, message);
                    bundle.putInt(NET_CODE, code);
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                }
            }
        });
    }

    /***
     * <p><b>发送一个GET请求，结果通过handler返回，数据放在bundle中</b></p><br/>
     * <b>成功</b>     (1).[消息(String) <b>NET_OK_MSG_KEY</b>(bundle中的key,后面大写都是key)] 2.[内容(String) <b>NET_OK_CONTENT_KEY</b>]<br/>
     * <b>连接异常</b> (2).[简单的消息(String) <b>NET_FAILED_SIMPLE_MSG_KEY</b>] 2.[详细消息(String) <b>NET_FAILED_DETAIL_MSG_KEY</b>]<br/>
     *<b>404,505之类的错误</b> (3).[错误代码(int) <b>NET_CODE</b>] 2.[简单信息(String) <b>NET_FAILED_SIMPLE_MSG_KEY</b>]<br/>
     * @param url 链接地址
     * @param handler 消息处理
     */
    public static void doGet(String url, final Handler handler)
    {
        if (simpleGetReq == null)
        {
            simpleGetReq = new Request.Builder().get().url(url).build();
        } else
        {
            simpleGetReq = simpleGetReq.newBuilder().url(url).build();
        }
        Call call = clientDefault.newCall(simpleGetReq);
        call.enqueue(new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                if (!call.isCanceled())
                {
                    call.cancel();
                }
                String s = e.getMessage();
                Message msg = handler.obtainMessage();
                msg.what = WHAT_NET_FAILED;
                Bundle bundle = new Bundle();
                bundle.putString(NET_FAILED_SIMPLE_MSG_KEY, NET_FAILED_MSG);
                bundle.putString(NET_FAILED_DETAIL_MSG_KEY, s);
                msg.setData(bundle);
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                if (response.isSuccessful())
                {
                    String message = response.message();
                    String content = response.body().string();
                    Message msg = handler.obtainMessage();
                    msg.what = WHAT_NET_OK;
                    Bundle bundle = new Bundle();
                    bundle.putString(NET_OK_MSG_KEY, message);
                    bundle.putString(NET_OK_CONTENT_KEY, content);
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                } else
                {
                    int code = response.code();
                    String message = response.message();
                    Message msg = handler.obtainMessage();
                    msg.what = WHAT_NET_EXCEPTION;
                    Bundle bundle = new Bundle();
                    bundle.putString(NET_FAILED_SIMPLE_MSG_KEY, message);
                    bundle.putInt(NET_CODE, code);
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                }
            }
        });
    }
}


