package com.white.hot.doremember.base;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.white.hot.doremember.R;
import com.white.hot.doremember.utils.AppManager;
import com.white.hot.doremember.widget.BaseActionBar;
import com.zhy.autolayout.AutoLayoutActivity;
import com.zhy.autolayout.utils.AutoUtils;
import org.xutils.x;

/**
 * Created by Jerry-Lee on 2016/8/3.
 * Function is Activity基类
 * Modify By
 * Modify Function is
 */
public class BaseActivity extends AutoLayoutActivity {

    public BaseActionBar actionBar;
    /**
     * 所有activity最底层布局
     */
    private RelativeLayout container;
    //无数据页
    private LinearLayout llNoData;

    public static DisplayImageOptions options;

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

    public void displayImageLoaderImg(ImageView iv, String imgPath){
        if(imgPath.startsWith("http")){
            ImageLoader.getInstance().displayImage(imgPath, iv, getImageLoaderOptions());
        }else{
            ImageLoader.getInstance().displayImage("file://" + imgPath, iv, getImageLoaderOptions());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        initWindowFeature();
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base);
        x.view().inject(this);

        AppManager.getInstance().put(this);

        container = (RelativeLayout) findViewById(R.id.activity_base_container);
        actionBar = (BaseActionBar) findViewById(R.id.activity_base_actionbar);
        llNoData = (LinearLayout) findViewById(R.id.nodata);

        AutoUtils.auto(actionBar);
        actionBar.setVisibility(View.GONE);
    }

    public void showNoDataPage(){
        llNoData.setVisibility(View.VISIBLE);
    }

    public void hideNoDataPage(){
        llNoData.setVisibility(View.GONE);
    }

    private void initWindowFeature() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }else{
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        LayoutInflater.from(this).inflate(layoutResID,
                (ViewGroup) findViewById(R.id.activity_base_content));
    }

    private Dialog proDialog;

    private TextView tvText;

    /**
     * 弹出进度框
     */
    public void showProgress() {
        showProgress(null, true);
    }

    /**
     * 弹出进度框(背景无阴影)
     */
    public void showProgressNoDim() {
        showProgress(null, false);
    }

    /**
     * 弹出进度框
     *
     * @param proText
     * @param isDim
     */
    public void showProgress(String proText, boolean isDim) {
        if (proDialog == null) {
            proDialog = new Dialog(this, isDim ? R.style.MyDialogStyle
                    : R.style.MyDialogStyleNoDim);
            View view = View.inflate(this, R.layout.progress_bar, null);
            tvText = (TextView) view.findViewById(R.id.show_text);
            view.setVisibility(View.VISIBLE);
            proDialog.setContentView(view);
        }
        setProText(proText);
        if (!proDialog.isShowing())
            proDialog.show();
    }

    /**
     * 设置进度文本
     *
     * @param proText
     */
    public void setProText(CharSequence proText) {
        if (tvText != null)
            tvText.setText("加载中...");
    }

    /**
     * 隐藏进度框
     */
    public void hideProgress() {
        if (proDialog != null) {
            proDialog.dismiss();
        }
    }

    /**
     * 检查进度框是否显示
     *
     * @return
     */
    public boolean getShowStatus() {
        if (proDialog.isShowing())
            return true;
        else
            return false;
    }

    /**
     * 弹出Toast
     *
     * @param charSequence
     */
    public void showToast(CharSequence charSequence) {
        Toast.makeText(this, charSequence, Toast.LENGTH_SHORT).show();
    }
}
