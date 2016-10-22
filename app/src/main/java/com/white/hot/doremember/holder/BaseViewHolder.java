package com.white.hot.doremember.holder;

import android.graphics.Bitmap;
import android.view.View;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.white.hot.doremember.R;
import com.zhy.autolayout.utils.AutoUtils;


/**
 * Created by ytf on 2016/08/10.
 * Description:
 * Modify by xx on 2016/08/10.
 * Modify detail:
 */
public abstract class BaseViewHolder<T> {
    protected T data;
    protected View v;

    public DisplayImageOptions options;

    public BaseViewHolder(){
        init();
    }
    
    public BaseViewHolder(T data){
        this.data = data;
        init();
    }

    private void init(){
        v = getViewLayout();
        v.setTag(this);
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                // 显示的渐变效果
                .displayer(new FadeInBitmapDisplayer(300))
                .bitmapConfig(Bitmap.Config.RGB_565)
                .cacheOnDisk(true)
                .showImageOnFail(R.drawable.default_bg).build();
    }

    /***
     * 将item的视图传入待解析
     * @return item的布局
     */
    protected abstract View getViewLayout();

    public void setData(T d, int position){
        this.data = d;
        refreshView(position);
    }

    /***
     *
     * @return 返回contentView，即item的布局
     */
    public View getContentView(){
        return v;
    }

    /***
     * 数据显示更新调用此方法完成
     */
    protected abstract void refreshView(int position);
}
