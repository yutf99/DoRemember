package com.white.hot.doremember.function.chart;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.load.resource.gif.GifDrawableLoadProvider;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.white.hot.doremember.R;
import com.white.hot.doremember.base.BaseActivity;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import butterknife.Bind;

@ContentView(R.layout.activity_chart)
public class ChartActivity extends BaseActivity
{

    @ViewInject(R.id.iv)
    ImageView vf;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        initActionBar();
    }

    private void initActionBar()
    {
        actionBar.setActionBarBackgroundColor(getResources().getColor(R.color.BlueGrade4));
        actionBar.setVisibility(View.VISIBLE);
        actionBar.setTitleText("图表", Color.WHITE);
        actionBar.showLeftBack(R.drawable.back);
    }

    public void click(View v)
    {
        Glide.with(this)
                .load("http://wx2.sinaimg.cn/mw690/685d4d6cgy1fckkeajaovg20b407gb2b.gif")
                .asGif()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(vf);
    }
}
