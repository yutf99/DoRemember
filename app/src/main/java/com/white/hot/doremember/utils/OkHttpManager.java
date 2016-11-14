package com.white.hot.doremember.utils;

import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.google.gson.internal.$Gson$Types;
import com.tencent.mm.sdk.platformtools.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * Created by ytf on 2016/11/9.
 * descption:
 */

public class OkHttpManager
{

    private static OkHttpManager mInstance;
    private Handler mHandler;
    private Gson mGson;

    private static int timeout = 8;
    private OkHttpClient clientDefault;
    //    private static OkHttpClient clientCustom = new OkHttpClient.Builder().connectTimeout(timeout, TimeUnit.SECONDS).build();
    private static Request simpleGetReq;
    private static Request simplePostReq;

    private static final String NET_FAILED_MSG = "连接异常！";
    private static final String NET_FAILED_SIMPLE_MSG_KEY = "simpleMessage";
    private static final String NET_FAILED_DETAIL_MSG_KEY = "detailMessage";
    private static final String NET_OK_MSG_KEY = "Message";
    private static final String NET_OK_CONTENT_KEY = "content";
    private static final String NET_CODE = "code";
    private static final int WHAT_NET_FAILED = 0x09;
    private static final int WHAT_NET_OK = 0x10;
    private static final int WHAT_NET_EXCEPTION = 0x11;

    private OkHttpManager()
    {

        mGson = new Gson();

        clientDefault = new OkHttpClient.Builder()
                .connectTimeout(timeout, TimeUnit.SECONDS)
                .readTimeout(timeout, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();
        mHandler = new Handler(Looper.getMainLooper());
    }

    public static OkHttpManager newInstance()
    {
        if (mInstance == null)
        {
            synchronized (OkHttpManager.class)
            {
                if (mInstance == null)
                {
                    mInstance = new OkHttpManager();
                }
            }
        }
        return mInstance;
    }

    public void doGet(String url, final ResultCallback callback)
    {
        if (callback == null)
        {
            throw new RuntimeException("callback can't be null");
        }
        if (simpleGetReq == null)
        {
            simpleGetReq = new Request.Builder().get().url(url).build();
        } else
        {
            simpleGetReq = simpleGetReq.newBuilder().get().url(url).build();
        }
        deliveryRequest(simpleGetReq, callback);
    }

    public void doPost(String url, final ResultCallback callback, Map<String, String> map)
    {
        if (callback == null)
        {
            throw new RuntimeException("callback can't be null");
        }
        if (simplePostReq == null)
        {
            simplePostReq = new Request.Builder().post(buildParams(map).build()).url(url).build();
        } else
        {
            simplePostReq = simplePostReq.newBuilder().post(buildParams(map).build()).url(url).build();
        }
        deliveryRequest(simplePostReq, callback);
    }

    private FormBody.Builder buildParams(Map<String, String> map)
    {
        FormBody.Builder builder = new FormBody.Builder();
        if (map != null && map.size() > 0)
        {
            Set<String> set = map.keySet();
            for (Iterator<String> it = set.iterator(); it.hasNext(); )
            {
                String key = it.next();
                String value = map.get(key);
                builder.add(key, value);
            }
        }
        return builder;
    }

    private void deliveryRequest(Request request, final OkHttpManager.ResultCallback callback)
    {
        clientDefault.newCall(request).enqueue(new Callback()
        {
            @Override
            public void onFailure(Call call, final IOException e)
            {
                cc();
                failedResponse(e, callback);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                try
                {
                    final String string = response.body().string();
                    if (callback.mType == String.class)
                    {
                        successResponse(string, callback);
                    } else
                    {
                        Object o = mGson.fromJson(string, callback.mType);
                        successResponse(o, callback);
                    }

                } catch (com.google.gson.JsonParseException e)//Json解析的错误
                {
                    cc();
                    failedResponse(e, callback);
                } catch (IOException e)
                {
                    failedResponse(e, callback);
                }
            }
        });
    }

    public void download(final String url, final String fileAbsolutePath, final ResultCallback callback)
    {
        final Request request = new Request.Builder()
                .url(url)
                .build();
        final ProgressListener progressListener = new ProgressListener()
        {
            @Override
            public void update(long bytesRead, long contentLength, boolean done)
            {
                onDownloadProgress(bytesRead, contentLength, done, callback);
            }
        };
        clientDefault = clientDefault.newBuilder().addInterceptor(new Interceptor()
        {
            @Override
            public Response intercept(Chain chain) throws IOException
            {
                Response originalResponse = chain.proceed(chain.request());
                return originalResponse.newBuilder().body(
                        new ProgressResponseBody(originalResponse.body(), progressListener))
                        .build();
            }
        }).build();
        final Call call = clientDefault.newCall(request);
        call.enqueue(new Callback()
        {
            @Override
            public void onFailure(Call call, final IOException e)
            {
                failedResponse(e, callback);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                try
                {
                    is = response.body().byteStream();
                    File file = new File(fileAbsolutePath);
                    fos = new FileOutputStream(file);
                    while ((len = is.read(buf)) != -1)
                    {
                        fos.write(buf, 0, len);
                    }
                    fos.flush();
                    //如果下载文件成功，第一个参数为文件的绝对路径
                    successResponse(file, callback);
                } catch (IOException e)
                {
                    e.printStackTrace();
//                    successResponse(response.request(), e, callback);
                } finally
                {
                    try
                    {
                        if (is != null) is.close();
                    } catch (IOException e)
                    {
                    }
                    try
                    {
                        if (fos != null) fos.close();
                    } catch (IOException e)
                    {
                    }
                }

            }
        });
    }

    /**
     * 添加进度监听的ResponseBody
     */
    private static class ProgressResponseBody extends ResponseBody
    {

        private final ResponseBody responseBody;
        private final ProgressListener progressListener;
        private BufferedSource bufferedSource;

        public ProgressResponseBody(ResponseBody responseBody, ProgressListener progressListener)
        {
            this.responseBody = responseBody;
            this.progressListener = progressListener;
        }

        @Override
        public MediaType contentType()
        {
            return responseBody.contentType();
        }

        @Override
        public long contentLength()
        {
            return responseBody.contentLength();
        }

        @Override
        public BufferedSource source()
        {
            if (bufferedSource == null)
            {
                bufferedSource = Okio.buffer(source(responseBody.source()));
            }
            return bufferedSource;
        }

        private Source source(Source source)
        {
            return new ForwardingSource(source)
            {
                long totalBytesRead = 0L;

                @Override
                public long read(Buffer sink, long byteCount) throws IOException
                {
                    long bytesRead = super.read(sink, byteCount);
                    totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                    progressListener.update(totalBytesRead, responseBody.contentLength(), bytesRead == -1);
                    return bytesRead;
                }
            };
        }
    }


    interface ProgressListener
    {
        /**
         * @param bytesRead     已下载字节数
         * @param contentLength 总字节数
         * @param done          是否下载完成
         */
        void update(long bytesRead, long contentLength, boolean done);
    }

    public void cc()
    {
        StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement e : stacktrace)
        {
            String s = e.getMethodName();
            Log.e("s", s);
        }
    }

    private void failedResponse(final Exception e, final OkHttpManager.ResultCallback callback)
    {
        mHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                if (callback != null)
                {
                    if (e instanceof com.google.gson.JsonParseException)
                    {
                        callback.onError("Jsno 转换异常", e);
                    } else
                    {
                        callback.onError(NET_FAILED_MSG, e);
                    }
                }
            }
        });
    }

    private void onDownloadProgress(final long progress, final long allLength, final boolean done, final OkHttpManager.ResultCallback callback)
    {
        mHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                callback.onProgress(progress, allLength, done);
            }
        });
    }

    private void successResponse(final Object object, final OkHttpManager.ResultCallback callback)
    {
        mHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                if (callback != null)
                {
                    callback.onSuccess(object);
                }
            }
        });
    }

    public static abstract class ResultCallback<T>
    {
        Type mType;

        public ResultCallback()
        {
            mType = getSuperclassTypeParameter(getClass());
        }

        public static Type getSuperclassTypeParameter(Class<?> subclass)
        {
            Type superclass = subclass.getGenericSuperclass();
            if (superclass instanceof Class)
            {
                throw new RuntimeException("Missing type parameter.");
            }
            ParameterizedType parameterized = (ParameterizedType) superclass;
            return $Gson$Types.canonicalize(parameterized.getActualTypeArguments()[0]);
        }

        public abstract void onError(String simpleMsg, Exception e);

        public void onProgress(long progress, long allLength, boolean done){}

        public abstract void onSuccess(T response);
    }

}
