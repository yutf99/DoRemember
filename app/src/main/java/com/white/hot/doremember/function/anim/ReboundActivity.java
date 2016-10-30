package com.white.hot.doremember.function.anim;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringSystem;
import com.white.hot.doremember.R;
import com.white.hot.doremember.base.BaseActivity;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import butterknife.Bind;

@ContentView(R.layout.activity_rebound)
public class ReboundActivity extends BaseActivity
{

    @ViewInject(R.id.iv)
    ImageView iv;
    @ViewInject(R.id.btn_reboud)
    Button btnReboud;

    private SpringSystem springSys;
    private Spring spring;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        initActionBar();
        initSpring();
    }

    private void initSpring()
    {
        springSys = SpringSystem.create();
        spring = springSys.createSpring();
        spring.addListener(new SimpleSpringListener()
        {
            @Override
            public void onSpringUpdate(Spring spring) {
                float value = (float) spring.getCurrentValue();
                float scale = 1f - (value * 0.5f);
                iv.setScaleX(scale);
                iv.setScaleY(scale);
            }
        });
    }

    @Event(R.id.btn_reboud)
    private void click(View v)
    {
        spring.setEndValue(1);
    }


    private void initActionBar()
    {
        actionBar.setActionBarBackgroundColor(getResources().getColor(R.color.BlueGrade4));
        actionBar.setVisibility(View.VISIBLE);
        actionBar.setTitleText("弹簧", Color.WHITE);
        actionBar.showLeftBack(R.drawable.back);
    }
}
