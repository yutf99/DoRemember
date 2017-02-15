package com.white.hot.doremember.function.anim;

import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.white.hot.doremember.R;
import com.white.hot.doremember.base.BaseActivity;
import com.white.hot.doremember.utils.DrawableUtils;
import com.white.hot.doremember.widget.BatteryIcon;
import com.white.hot.doremember.widget.PopBarrage;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import butterknife.Bind;

@ContentView(R.layout.activity_vertical_flip_adver)
public class VerticalFlipAdverActivity extends BaseActivity
{

    @ViewInject(R.id.vf)
    ViewFlipper vf;
    @ViewInject(R.id.v)
    TextView v;
    @ViewInject(R.id.bi)
    BatteryIcon bi;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        initActionBar();
        View v1 = View.inflate(this, R.layout.vertical_adver_flip,null);
        ((TextView)v1.findViewById(R.id.msg)).setText("小米6来了：晓龙835+8G运存！");
        View v2 = View.inflate(this, R.layout.vertical_adver_flip,null);
        ((TextView)v2.findViewById(R.id.msg)).setText("227斤的胖MM，掀起上衣后，美爆全场！");
        vf.addView(v1);
        vf.addView(v2);
        v.setPadding(20,20,20,20);
        v.setBackground(DrawableUtils.createStrokeShape(20, Color.BLUE));
        bi.setInnerColor(Color.BLUE);
        bi.setOutColor(Color.BLACK);
        bi.setOnRecharging(true);
    }

    private void initActionBar()
    {
        actionBar.setActionBarBackgroundColor(getResources().getColor(R.color.BlueGrade4));
        actionBar.setVisibility(View.VISIBLE);
        actionBar.setTitleText("广告", Color.WHITE);
        actionBar.showLeftBack(R.drawable.back);
    }
}
