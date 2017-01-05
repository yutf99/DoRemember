package com.white.hot.blur;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.internal.$Gson$Types;
import com.google.gson.internal.ObjectConstructor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.framed.Header;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.ForwardingSink;
import okio.ForwardingSource;
import okio.Okio;
import okio.Sink;
import okio.Source;

/**
 * Created by ytf on 2016/11/9.
 * descption: 网络请求类
 */

public class OkHttpManager
{

    private static OkHttpManager mInstance;
    private Handler mHandler;
    private Gson mGson;
    //默认超时时间8秒
    private static int timeout = 8;
    private OkHttpClient clientDefault;
    private static Request simpleGetReq;
    private static Request simplePostReq;

    private static final String NET_FAILED_MSG = "未知异常！";
    private static final String NET_FAILED_JSON_MSG = "Json 转换异常";
    private static final String NET_FAILED_LINK_UNAVIlABLE_MSG = "无法连接";
    private static final String NET_FAILED_TIMEOUT_MSG = "连接超时";
    private static final String NET_FAILED_FILE_MSG = "文件不存在";

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

    public static OkHttpManager getInstance()
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

    /***
     * <b>发送一个get请求</b>, 回调要传入类型
     *
     * @param url      链接
     * @param callback 回调函数
     */
    public void doGet(String url, final ResultCallback<? extends Object> callback)
    {
        if (callback == null)
        {
            throw new RuntimeException("回调不能为空");
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

    /***
     * <b>发送一个post请求</b>, 回调要传入类型
     *
     * @param url            链接
     * @param callback       回调函数
     * @param reqBodyParam  请求体参数
     * @param reqHeader     请求头参数
     */
    public void doPost(String url, final ResultCallback<? extends Object> callback, Map<String, String> reqBodyParam, Map<String, String> reqHeader)
    {
        if (callback == null)
        {
            throw new RuntimeException("回调不能为空");
        }
        if (simplePostReq == null)
        {
            if(reqHeader == null)
            {
                simplePostReq = new Request.Builder().post(reqBodyParam == null ? buildEmptyBody() :buildParams(reqBodyParam).build()).url(url).build();
            }else
            {
                //构造请求头
                Request.Builder builder = new Request.Builder();
                Set<String> set = reqHeader.keySet();
                for(Iterator<String> it = set.iterator();it.hasNext();)
                {
                    String key = it.next();
                    String value = reqHeader.get(key);
                    builder.addHeader(key, value);
                }
                simplePostReq = builder.post(reqBodyParam == null ? buildEmptyBody() :buildParams(reqBodyParam).build()).url(url).build();
            }
        } else
        {
            if(reqHeader == null)
            {
                simplePostReq = simplePostReq.newBuilder().post(reqBodyParam == null ? buildEmptyBody() :buildParams(reqBodyParam).build()).url(url).build();
            }else
            {
                //构造请求头
                Request.Builder builder = simplePostReq.newBuilder();
                Set<String> set = reqHeader.keySet();
                for(Iterator<String> it = set.iterator();it.hasNext();)
                {
                    String key = it.next();
                    String value = reqHeader.get(key);
                    builder.addHeader(key, value);
                }
                simplePostReq = builder.post(reqBodyParam == null ? buildEmptyBody() :buildParams(reqBodyParam).build()).url(url).build();
            }
        }
        deliveryRequest(simplePostReq, callback);
    }

//    public void doPost(String url, final ResultCallback<? extends Object> callback, Object obj)
//    {
//        Map<String, String> param = new HashMap<>();
//    }

    /***
     * <b>构建参数</b>
     *
     * @param map 参数
     * @return
     */
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

    private RequestBody buildEmptyBody()
    {
        return RequestBody.create(null, new byte[0]);
    }

    /***
     * <b>传递请求</b>
     *
     * @param request
     * @param callback
     */
    private void deliveryRequest(Request request, final ResultCallback callback)
    {
        clientDefault.newCall(request).enqueue(new Callback()
        {
            @Override
            public void onFailure(Call call, final IOException e)
            {
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
                    failedResponse(e, callback);
                } catch (IOException e)
                {
                    failedResponse(e, callback);
                }
            }
        });
    }

    /***
     * <b>下载, 如果要拿到进度，需要复写callback的<b>onProgress()</b>方法</b>, 回调方法要传入类型
     *
     * @param url              下载链接<br/>
     * @param fileAbsolutePath 文件绝对路径<br/>
     * @param callback         回调<br/>
     */
    public void download(final String url, final String fileAbsolutePath, final ResultCallback callback)
    {
        final Request request = new Request.Builder()
                .url(url)
                .build();
        final DownloadProgressListener progressListener = new DownloadProgressListener()
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
                    successResponse(file, callback);
                } catch (IOException e)
                {
                    e.printStackTrace();
                    failedResponse(e, callback);
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

    /***
     * 文件上传
     * @param url url
     * @param fileAbsolutePath 文件绝对路径
     * @param fileParamName 文件上传的参数名,相当于post的param
     * @param callback 回调函数
     */
    public void upload(final String url, final String fileAbsolutePath, final String fileParamName, final ResultCallback callback)
    {
        if(callback == null)
        {
            throw new RuntimeException("回调不能为空");
        }
        File file = new File(fileAbsolutePath);
        if(!file.exists())
        {
            failedResponse(new FileNotFoundException(), callback);
            return;
        }
        final UploadProgressListener listener = new UploadProgressListener()
        {
            @Override
            public void onRequestProgress(long bytesWrite, long contentLength, boolean done)
            {
                onUploadProgress(bytesWrite, contentLength, done, callback);
            }
        };
        String fileName = getFileName(fileAbsolutePath);
        RequestBody reqBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);

        MultipartBody mBody = new MultipartBody.Builder("--------------------------").setType(MultipartBody.FORM)
                .addFormDataPart(fileParamName , fileName , new ProgressRequestBody(reqBody, listener))
                .build();
        clientDefault = clientDefault.newBuilder().build();
        if (simplePostReq == null)
        {
            simplePostReq = new Request.Builder().post(mBody).url(url).build();
        }else
        {
            simplePostReq = simplePostReq.newBuilder().url(url).post(mBody).build();
        }
        Call call = clientDefault.newCall(simplePostReq);
        call.enqueue(new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
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
                    failedResponse(e, callback);
                } catch (IOException e)
                {
                    failedResponse(e, callback);
                }
            }
        });
    }

    public String getFileName(String filePath)
    {
        if (TextUtils.isEmpty(filePath))
            return "";
        return filePath.substring(filePath.lastIndexOf(File.separator) + 1);
    }

    /***
     * 文件下载的响应体
     */
    private static class ProgressResponseBody extends ResponseBody
    {

        private final ResponseBody responseBody;
        private final DownloadProgressListener progressListener;
        private BufferedSource bufferedSource;

        public ProgressResponseBody(ResponseBody responseBody, DownloadProgressListener progressListener)
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

    interface DownloadProgressListener
    {
        /**
         * @param bytesRead     已下载字节数
         * @param contentLength 总字节数
         * @param done          是否下载完成
         */
        void update(long bytesRead, long contentLength, boolean done);
    }

    /***
     * 文件上传的请求体
     */
    public  class ProgressRequestBody extends RequestBody
    {
        //实际的待包装请求体
        private final RequestBody requestBody;
        //进度回调接口
        private final UploadProgressListener progressListener;
        //包装完成的BufferedSink
        private BufferedSink bufferedSink;

        /**
         * 构造函数，赋值
         * @param requestBody 待包装的请求体
         * @param progressListener 回调接口
         */
        public ProgressRequestBody(RequestBody requestBody, UploadProgressListener progressListener) {
            this.requestBody = requestBody;
            this.progressListener = progressListener;
        }

        /**
         * 重写调用实际的响应体的contentType
         * @return MediaType
         */
        @Override
        public MediaType contentType() {
            return requestBody.contentType();
        }

        /**
         * 重写调用实际的响应体的contentLength
         * @return contentLength
         * @throws IOException 异常
         */
        @Override
        public long contentLength() throws IOException {
            return requestBody.contentLength();
        }

        /**
         * 重写进行写入
         * @param sink BufferedSink
         * @throws IOException 异常
         */
        @Override
        public void writeTo(BufferedSink sink) throws IOException {
            if (bufferedSink == null) {
                //包装
                bufferedSink = Okio.buffer(sink(sink));
            }
            //写入
            requestBody.writeTo(bufferedSink);
            //必须调用flush，否则最后一部分数据可能不会被写入
            bufferedSink.flush();

        }

        /**
         * 写入，回调进度接口
         * @param sink Sink
         * @return Sink
         */
        private Sink sink(Sink sink) {
            return new ForwardingSink(sink) {
                //当前写入字节数
                long bytesWritten = 0L;
                //总字节长度，避免多次调用contentLength()方法
                long contentLength = 0L;

                @Override
                public void write(Buffer source, long byteCount) throws IOException {
                    super.write(source, byteCount);
                    if (contentLength == 0) {
                        //获得contentLength的值，后续不再调用
                        contentLength = contentLength();
                    }
                    //增加当前写入的字节数
                    bytesWritten += byteCount;
                    //回调
                    progressListener.onRequestProgress(bytesWritten, contentLength, bytesWritten == contentLength);
                }
            };
        }
    }

    interface UploadProgressListener
    {
        /**
         * @param bytesWrite     已上传字节数
         * @param contentLength 总字节数
         * @param done          是否上传完成
         */
        void onRequestProgress(long bytesWrite, long contentLength, boolean done);
    }

    private void failedResponse(final Exception e, final ResultCallback callback)
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
                        callback.onError(NET_FAILED_JSON_MSG, e);
                    } else if (e instanceof ConnectException)
                    {
                        callback.onError(NET_FAILED_LINK_UNAVIlABLE_MSG, e);
                    } else if (e instanceof SocketTimeoutException)
                    {
                        callback.onError(NET_FAILED_TIMEOUT_MSG, e);
                    } else if (e instanceof FileNotFoundException)
                    {
                        callback.onError(NET_FAILED_FILE_MSG, e);
                    } else
                    {
                        callback.onError(NET_FAILED_MSG, e);
                    }
                }
            }
        });
    }

    private void onDownloadProgress(final long progress, final long allLength, final boolean done, final ResultCallback callback)
    {
        mHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                callback.onDownloadProgress(progress, allLength, done);
            }
        });
    }

    private void onUploadProgress(final long progress, final long allLength, final boolean done, final ResultCallback callback)
    {
        mHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                callback.onUploadProgress(progress, allLength, done);
            }
        });
    }

    private void successResponse(final Object object, final ResultCallback callback)
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

    /***
     * 回调类，用于返回结果到前台
     *
     * @param <T> 传入一个泛型，会自动根据类型产生对象，在post中用处较多
     */
    public static abstract class ResultCallback<T extends Object>
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
                throw new RuntimeException("ResultCallback泛型缺少对象类型");
            }
            ParameterizedType parameterized = (ParameterizedType) superclass;
            return $Gson$Types.canonicalize(parameterized.getActualTypeArguments()[0]);
        }

        public abstract void onError(String simpleMsg, Exception e);

        public void onDownloadProgress(long progress, long allLength, boolean done){}

        public void onUploadProgress(long progress, long allLength, boolean done){}

        public abstract void onSuccess(T response);
    }

    /***
     * 此方法用于设置https访问时证书，仅用于单向验证，inputstream为证书文件的输入流，可以将证书放到main/assets目录下,也可以
     * 服务端证书crt文件使用rfc命令得到字符串用 new Buffer().writeUtf(str).inputStream()得到输入流，需要包含-----BEGIN CERTIFICATE-----和-----END CERTIFICATE-----
     * @param certificate
     */
    public void setCertificate(String alias, String password, InputStream certificate)
    {
        try
        {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
            int index = 0;
//            for (InputStream certificate : certificates)
//            {
//                String certificateAlias = Integer.toString(index++);
            keyStore.setCertificateEntry(alias, certificateFactory.generateCertificate(certificate));

            try
            {
                if (certificate != null)
                    certificate.close();
            } catch (IOException e)
            {
            }
//            }

            SSLContext sslContext = SSLContext.getInstance("TLS");

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(
                    KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, password.toCharArray());

            TrustManagerFactory trustManagerFactory =
                    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());

            trustManagerFactory.init(keyStore);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                throw new IllegalStateException("Unexpected default trust managers:"
                        + Arrays.toString(trustManagers));
            }else
            {
                sslContext.init(null,new TrustManager[]{trustManagers[0]},new SecureRandom());
                clientDefault = clientDefault.newBuilder().sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustManagers[0]).build();
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
