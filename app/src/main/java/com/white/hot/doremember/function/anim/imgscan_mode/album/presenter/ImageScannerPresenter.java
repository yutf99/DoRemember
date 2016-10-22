package com.white.hot.doremember.function.anim.imgscan_mode.album.presenter;

import android.content.Context;
import android.support.v4.app.LoaderManager;

public interface ImageScannerPresenter {

    /**
     * 扫描获取图片文件夹列表
     *
     * @param context
     * @param loaderManager 获取系统图片的LoaderManager
     */
    public void startScanImage(Context context, LoaderManager loaderManager);

}
