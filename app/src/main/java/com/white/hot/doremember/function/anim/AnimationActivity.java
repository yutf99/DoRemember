package com.white.hot.doremember.function.anim;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationSet;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.github.johnpersano.supertoasts.library.Style;
import com.github.johnpersano.supertoasts.library.SuperToast;
import com.white.hot.doremember.R;
import com.white.hot.doremember.base.BaseActivity;
import com.white.hot.doremember.constant.ActivityCode;
import com.white.hot.doremember.function.anim.imgscan_mode.AlbumActivity;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.List;

import butterknife.Bind;

@ContentView(R.layout.activity_animation)
public class AnimationActivity extends BaseActivity {

    @ViewInject(R.id.iv)
    ImageView iv;
    @ViewInject(R.id.broswer)
    Button broswer;
    @ViewInject(R.id.btn_anim_property)
    Button btnAnimProperty;
    @ViewInject(R.id.btn_anim_tween)
    Button btnAnimTween;
    @ViewInject(R.id.btn_anim_frame)
    Button btnAnimFrame;
    @ViewInject(R.id.btn_anim_fan)
    Button btnAnimFan;
    @ViewInject(R.id.btn_noti_s3)
    Button btnNotiS3;
    @ViewInject(R.id.btn_noti_s4)
    Button btnNotiS4;
    @ViewInject(R.id.btn_noti_s5)
    Button btnNotiS5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initActionBar();
    }

    private boolean isPropertyAnimActive;

    @Event(value = {R.id.broswer, R.id.iv,
            R.id.btn_anim_property, R.id.btn_anim_tween, R.id.btn_anim_frame,
            R.id.btn_anim_fan,R.id.btn_noti_s3, R.id.btn_noti_s4,R.id.btn_noti_s5,R.id.btn_noti_s6})
    private void click(View v) {
        switch (v.getId()) {
            case R.id.broswer:
                startActivityForResult(new Intent(this, AlbumActivity.class), ActivityCode.REQ_ANIM_IMG_CHOOSE);
                break;
            case R.id.iv:
                new SuperToast(this)
                        .setFrame(Style.FRAME_KITKAT)
                        .setAnimations(Style.ANIMATIONS_POP)
                        .setText("点击图片")
                        .show();
                break;
            case R.id.btn_anim_property:
                isPropertyAnimActive = !isPropertyAnimActive;
                if (isPropertyAnimActive) {
                    btnAnimProperty.setText("复位");
                }else{
                    btnAnimProperty.setText("属性动画");
                }
                propertyAnimIt(!isPropertyAnimActive);
                break;
            case R.id.btn_anim_tween:
                tweanAnimIt();
                break;
            case R.id.btn_anim_frame:
                frameAnimIt();
                break;
            case R.id.btn_anim_fan:
                startActivity(new Intent(this, FanActivity.class));
                break;
            case R.id.btn_noti_s3:
                startActivity(new Intent(this, ListViewActivity.class));
                break;
            case R.id.btn_noti_s4:
                startActivity(new Intent(this, PlatteActivity.class));
                break;
            case R.id.btn_noti_s5:
                startActivity(new Intent(this, GridViewActivity.class));
                break;
            case R.id.btn_noti_s6:
                startActivity(new Intent(this, ReboundActivity.class));
                break;
        }
    }

    private void frameAnimIt() {
        iv.setImageResource(R.drawable.anim_anim_frame);
        AnimationDrawable a = (AnimationDrawable) iv.getDrawable();
        a.start();
    }

    private void tweanAnimIt() {
        //加载xml
//        AnimationSet set = (AnimationSet) AnimationUtils.loadAnimation(this, R.anim.anim_anim_twean);
//        iv.setAnimation(set);
//        iv.startAnimation(set);
        //动态创建
        TranslateAnimation animT = new TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_SELF, 0.0f,
                TranslateAnimation.RELATIVE_TO_SELF, 1.0f,
                TranslateAnimation.RELATIVE_TO_SELF, 0.0f,
                TranslateAnimation.RELATIVE_TO_SELF, 0.0f);
        animT.setDuration(1000);
        ScaleAnimation animS = new ScaleAnimation(1.0f, 0.4f, 1.0f, 0.4f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        animS.setDuration(3000);
        AnimationSet set2 = new AnimationSet(true);
        set2.addAnimation(animT);
        set2.setInterpolator(new OvershootInterpolator());
        iv.startAnimation(set2);
    }

    private void propertyAnimIt(boolean reset) {
        //加载xml文件
//        ObjectAnimator o = (ObjectAnimator) AnimatorInflater.loadAnimator(this, R.animator.anim_anim_property_alpha);
//        Animator as = AnimatorInflater.loadAnimator(this, R.animator.anim_anim_property);
//        as.setDuration(500);
//        as.setTarget(iv);
//        as.start();


        //动态创建
        ObjectAnimator animator;
        if (!reset) {
            animator = ObjectAnimator.ofFloat(iv, "rotation", 0f, -270);
        } else {
            animator = ObjectAnimator.ofFloat(iv, "rotation", -270f, 0f);
        }
        iv.setPivotX(iv.getWidth() / 2);
        iv.setPivotY(iv.getHeight() / 2);
        animator.setDuration(500);
        animator.setInterpolator(new OvershootInterpolator());
//        animator.setRepeatMode(ValueAnimator.REVERSE);
//        animator.setRepeatCount(2);
        animator.setTarget(iv);
        animator.start();
    }

    private List<String> imgPaths;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ActivityCode.REQ_ANIM_IMG_CHOOSE) {
            if (resultCode == ActivityCode.RES_ANIM_IMG_CHOOSE) {
                imgPaths = (List<String>) data.getSerializableExtra("path");
                if (imgPaths != null && imgPaths.size() > 0) {
                    displayImageLoaderImg(iv, imgPaths.get(0));
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initActionBar() {
        actionBar.setActionBarBackgroundColor(getResources().getColor(R.color.BlueGrade4));
        actionBar.setVisibility(View.VISIBLE);
        actionBar.setTitleText("动画", Color.WHITE);
        actionBar.showLeftBack(R.drawable.back);
    }
}
