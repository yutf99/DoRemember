package com.white.hot.doremember.base;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.white.hot.doremember.R;
import com.white.hot.doremember.crash.CrashHandler;
import com.zhy.autolayout.config.AutoLayoutConifg;

import org.xutils.x;

import java.io.File;

/**
 * Created by ytf on 2016/08/20.
 * Description
 * Modify by xx at 2016/08/20.
 * Modify detail
 */
public class BaseApplication extends Application {

    private static Handler handler;
    private static BaseApplication context;

    @Override
    public void onCreate()
    {
        super.onCreate();
        context = this;
        handler = new Handler();
        //初始化xUtils3
        x.Ext.init(this);
        //初始化Autolayout
        AutoLayoutConifg.getInstance().useDeviceSize();
        //初始化ImageLoader
        initImageLoader();
        //初始化全局异常处理
//        CrashHandler.getInstance().init(this);
        //初始化frasco
//        Fresco.initialize(this);
    }

    public void initImageLoader()
    {
        File cacheDir = StorageUtils.getCacheDirectory(getApplicationContext());
        int maxSize = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxSize / 8;
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getApplicationContext())
                .threadPoolSize(6)
                .threadPriority(Thread.NORM_PRIORITY - 1)
                .diskCacheFileCount(100)
                .diskCache(new UnlimitedDiskCache(cacheDir))
                .memoryCacheSize(cacheSize).build();
        ImageLoader.getInstance().init(config);
    }

    private static DisplayImageOptions options;

    public static DisplayImageOptions getImageLoaderOptions() {
        if(options == null){
            options = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .displayer(new FadeInBitmapDisplayer(300))
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .cacheOnDisk(true)
                    // 显示的渐变效果
                    .showImageOnFail(R.drawable.default_holder)
                    .showImageOnLoading(R.drawable.default_holder).build();
        }
        return options;
    }

    public static Handler getHandler()
    {
        return handler;
    }

    public static Context getGlobalContext()
    {
        return context;
    }
}
