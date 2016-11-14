package com.white.hot.doremember.function.sharesdk;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.white.hot.doremember.R;
import com.white.hot.doremember.base.BaseActivity;

public class ShareSDKActivity extends BaseActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_sdk);
        initActionBar();
    }

    private void initActionBar()
    {
        actionBar.setActionBarBackgroundColor(getResources().getColor(R.color.CyanGrade3));
        actionBar.setVisibility(View.VISIBLE);
        actionBar.setTitleText("ShareSDK", Color.WHITE);
        actionBar.showLeftBack(R.drawable.back);
    }
}
