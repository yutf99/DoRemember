package com.white.hot.doremember.crash;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Looper;
import android.os.Process;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.white.hot.doremember.bean.BaseResp;
import com.white.hot.doremember.bean.CrashLog;
import com.white.hot.doremember.utils.AppManager;
import com.white.hot.doremember.utils.FileUtils;
import com.white.hot.doremember.utils.NetUtil;
import com.white.hot.doremember.utils.OkHttpManager;
import com.white.hot.doremember.widget.BaseDialog;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ytf on 2016/12/7.
 * descption:
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler
{

    private static CrashHandler instance;
    private Context mContext;
    private Thread.UncaughtExceptionHandler defaultHandler;

    private CrashHandler()
    {
    }

    public static CrashHandler getInstance()
    {
        if (instance == null)
        {
            synchronized (CrashHandler.class)
            {
                if (instance == null)
                {
                    instance = new CrashHandler();
                }
            }
        }
        return instance;
    }

    public void init(Context context)
    {
        Thread.setDefaultUncaughtExceptionHandler(this);
        this.mContext = context;
        defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex)
    {
        if (isHandler(ex))
        {
            collectDeviceInfo(mContext);
            handlerException(ex);
        } else
        {
            defaultHandler.uncaughtException(thread, ex);
        }
    }

    private void handlerException(Throwable ex)
    {
        saveCrashInfo2File(ex);
        Log.e("s", "已经准备开始1");
        Activity activity = AppManager.getInstance().popTop();
        if(activity != null)
        {
            Looper.prepare();
            Log.e("s", "已经准备开始2");
//            Toast.makeText(activity, "退出", Toast.LENGTH_SHORT).show();
            Looper.loop();
            Process.killProcess(Process.myPid());
            System.exit(0);
//            BaseDialog bd = new BaseDialog.Builder(activity)
//                    .setMessage("抱歉！程序出现异常了，即将退出！")
//                    .setPositiveListener("确定", new View.OnClickListener()
//                    {
//                        @Override
//                        public void onClick(View v)
//                        {
//                            // 退出程序
//                            Process.killProcess(Process.myPid());
//                            // 关闭虚拟机，彻底释放内存空间
//                            System.exit(0);
//                        }
//                    }).create();
//            bd.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//            bd.show();
//            Looper.loop();
        }
    }

    //用来存储设备信息和异常信息
    private Map<String, String> infos = new HashMap<String, String>();

    /**
     * 收集设备参数信息
     *
     * @param ctx
     */
    public void collectDeviceInfo(Context ctx)
    {
        infos.clear();
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields)
        {
            try
            {
                field.setAccessible(true);
                infos.put(field.getName(), field.get(null).toString());
            } catch (Exception e)
            {
            }
        }
        boolean isAvailabel = NetUtil.isNetworkAvailable(ctx);
        infos.put("NetAvailabel", "网络"+(isAvailabel ? "可用" : "不可用"));
        if(isAvailabel)
        {
            infos.put("NetType", NetUtil.GetNetworkType(ctx));
        }
    }

    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
    /**
     * 保存错误信息到文件中
     *
     * @param ex
     * @return 返回文件名称, 便于将文件传送到服务器
     */
    private String saveCrashInfo2File(Throwable ex)
    {

        StringBuffer sb = new StringBuffer();
        sb.append("设备信息：\n");
        for (Map.Entry<String, String> entry : infos.entrySet())
        {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + ":  " + value + "\n");
        }
        sb.append("\n错误信息：\n");
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null)
        {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append(result);
        try
        {
            String time = formatter.format(new Date());
            String fileName = "/Crash-" + time + "_" + System.currentTimeMillis() + ".log";

            String path = FileUtils.createPathBasedOnApp("/.log");
            FileOutputStream fos = new FileOutputStream(path + fileName);
            fos.write(sb.toString().getBytes());
            fos.close();
            OkHttpManager.getInstance().upload("http://192.168.1.100:8080/MavenDemo/upload/uploadLog", path + fileName, "crashlog",
                    new OkHttpManager.ResultCallback<BaseResp<CrashLog>>()
            {
                @Override
                public void onError(String simpleMsg, Exception e)
                {
                    Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(BaseResp<CrashLog> response)
                {
                    Toast.makeText(mContext, response.getMsg(), Toast.LENGTH_SHORT).show();
                }
            });
            return fileName;
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private boolean isHandler(Throwable ex)
    {
        // 排序不需要捕获的情况
        if (ex == null)
        {
            return false;
        }
        return true;
    }
}
