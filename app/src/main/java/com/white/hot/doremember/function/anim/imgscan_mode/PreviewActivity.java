package com.white.hot.doremember.function.anim.imgscan_mode;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.white.hot.doremember.R;

public class PreviewActivity extends AppCompatActivity implements View.OnClickListener{

    private FrameLayout navibar;
    private ViewPager vp;
    private ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置沉浸式样式
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_preview);
        adjustTitleBar();
        initView();
    }

    private void initView() {
        navibar = (FrameLayout) findViewById(R.id.fl);
        vp = (ViewPager) findViewById(R.id.vp);
        ivBack = (ImageView) findViewById(R.id.iv_back);
        ivBack.setOnClickListener(this);
    }

    private void adjustTitleBar() {

        int identifyId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if(identifyId > 0){
            int resultHeight = getResources().getDimensionPixelSize(identifyId);
            findViewById(R.id.fl).setPadding(0, resultHeight, 0, 0);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                break;
        }
    }
}
