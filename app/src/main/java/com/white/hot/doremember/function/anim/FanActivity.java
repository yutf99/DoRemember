package com.white.hot.doremember.function.anim;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.white.hot.doremember.R;
import com.white.hot.doremember.base.BaseActivity;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

@ContentView(R.layout.activity_fan)
public class FanActivity extends BaseActivity {

    @ViewInject(R.id.iv_fan)
    private ImageView ivFan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        initActionBar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        class1Rotate(1);
    }

    private void initActionBar() {
        actionBar.setActionBarBackgroundColor(getResources().getColor(R.color.BlueGrade4));
        actionBar.setVisibility(View.VISIBLE);
        actionBar.setTitleText("动画", Color.WHITE);
        actionBar.showLeftBack(R.drawable.back);
    }

    @Event(value=R.id.rg_control, type=RadioGroup.OnCheckedChangeListener.class)
    private void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId)
        {
            case R.id.class_1:
                class1Rotate(1);
                break;
            case R.id.class_2:
                class1Rotate(2);
                break;
            case R.id.class_3:
                class1Rotate(3);
                break;
            case R.id.class_4:
                class1Rotate(4);
                break;
        }
    }

    private void class1Rotate(int classR)
    {
        ivFan.clearAnimation();
        RotateAnimation r = new RotateAnimation(0, 360,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        int duration = 1000;
        switch(classR)
        {
            case 1:
                duration = 4000;
                break;
            case 2:
                duration = 2000;
                break;
            case 3:
                duration = 1000;
                break;
            case 4:
                duration = 300;
                break;
        }
        r.setDuration(duration);
        r.setInterpolator(new LinearInterpolator());
        r.setRepeatCount(Animation.INFINITE);
        ivFan.startAnimation(r);
    }
}
