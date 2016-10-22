package com.white.hot.doremember.dialog;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ProgressBar;

import com.facebook.drawee.view.SimpleDraweeView;
import com.white.hot.doremember.R;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by ytf on 2016/08/20.
 * Description
 * Modify by xx at 2016/08/20.
 * Modify detail
 */
public class DialogViewAnim extends Dialog {

    private ProgressBar pbCircle;

    private SimpleDraweeView sdv1;
    private SimpleDraweeView sdv2;

    public DialogViewAnim(Context context) {
        this(context, R.style.customDialog);
        initAnim();
    }

    public DialogViewAnim(Context context, int themeResId) {
        super(context, R.style.customDialog);
        initAnim();
    }

    protected DialogViewAnim(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initAnim();
    }

    TranslateAnimation tanimL;
    TranslateAnimation tanimR;
    private void initAnim()
    {
        tanimL = new TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_PARENT, -1.0f,
                TranslateAnimation.RELATIVE_TO_PARENT, 0.0f,
                TranslateAnimation.RELATIVE_TO_PARENT, 0.0f,
                TranslateAnimation.RELATIVE_TO_PARENT, 0.0f);
        tanimL.setInterpolator(new OvershootInterpolator());
        tanimL.setDuration(1000);

        tanimR = new TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_PARENT, 2.0f,
                TranslateAnimation.RELATIVE_TO_PARENT, 0.0f,
                TranslateAnimation.RELATIVE_TO_PARENT, 0.0f,
                TranslateAnimation.RELATIVE_TO_PARENT, 0.0f);
        tanimR.setInterpolator(new OvershootInterpolator());
        tanimR.setDuration(1000);
    }

    private Window window;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_view_anim);
//
        window = getWindow(); //得到对话框
        WindowManager.LayoutParams lp = window.getAttributes();
        //activity背景变暗取消
        lp.dimAmount = 0f;
        window.setAttributes(lp);
        window.setWindowAnimations(R.style.DialogViewAnim);//设置窗口弹出动画
//        window.setBackgroundDrawableResource(R.color.transparent); //设置对话框背景为透明

        sdv1 = (SimpleDraweeView) findViewById(R.id.sdv1);
        sdv2 = (SimpleDraweeView) findViewById(R.id.sdv2);
        pbCircle = (ProgressBar) findViewById(R.id.pb_circle);


//        AlphaAnimation anim = new AlphaAnimation(1.0f, 0.0f);
//        anim.setDuration(3000);
//        pbCircle.setAnimation(anim);
//        anim.start();

        sdv1.setImageURI(Uri.parse("http://img4.duitang.com/uploads/item/201510/11/20151011190254_4vwNK.jpeg"));
        sdv2.setImageURI(Uri.parse("http://img4.duitang.com/uploads/item/201510/11/20151011190254_4vwNK.jpeg"));

//        sdv1.setAnimation(tanimL);
//        sdv2.setAnimation(tanimR);

//        tanimL.start();
//        tanimR.start();
    }
}
