package com.white.hot.doremember.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.white.hot.doremember.R;
import com.white.hot.doremember.utils.UIHelper;

/**
 * Created by Jerry-Lee on 2016/1/29.
 * Function is  标题栏
 * Modify By
 * Modify Function is
 */
public class BaseActionBar extends FrameLayout {

    private FrameLayout actionBar;
    private ImageView leftActionBar;
    private TextView titleActionBar;
    private ImageView rightActionBar;

    public BaseActionBar(Context context) {
        super(context);
        init();
    }

    public BaseActionBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BaseActionBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        View.inflate(getContext(), R.layout.base_actionbar, this);
        actionBar = (FrameLayout) findViewById(R.id.action_bar);
        leftActionBar = (ImageView) findViewById(R.id.action_bar_left_img);
        titleActionBar = (TextView) findViewById(R.id.action_bar_title);
        rightActionBar = (ImageView) findViewById(R.id.action_bar_right_img);

    }

    public FrameLayout getActionBar() {
        return actionBar;
    }

    /**
     * 设置标题栏整体背景色
     *
     * @param color
     */
    public void setActionBarBackgroundColor(int color) {
        setBackgroundColor(color);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            int statusHeight = UIHelper.getStatusHight();
            setPadding(0, statusHeight, 0, 0);
        }
    }

    /**
     * 设置标题栏背景图片
     *
     * @param drawable
     */
    public void setActionBarBackgroundDrawable(Drawable drawable) {
        actionBar.setBackgroundDrawable(drawable);
    }

    /**
     * 隐藏左边按钮
     */
    public void hideBack() {
        leftActionBar.setVisibility(View.GONE);
    }

    /**
     * 隐藏标题
     */
    public void hideTitle() {
        titleActionBar.setVisibility(View.GONE);
    }

    /**
     * 隐藏右边按钮
     */
    public void hideRight() {
        rightActionBar.setVisibility(View.GONE);
    }

    /**
     * 显示左边按钮为返回键
     *
     *
     */
    public void showLeftBack(int resId) {
        Drawable drawable = ContextCompat.getDrawable(this.getContext(), resId);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        leftActionBar.setImageDrawable(drawable);
        leftActionBar.setVisibility(View.VISIBLE);
        leftActionBar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity)getContext()).finish();
            }
        });
    }

    /**
     * 显示标题
     */
    public void showTitle() {
        titleActionBar.setVisibility(View.VISIBLE);
    }

    /**
     * 判断标题是否正在显示
     *
     * @return
     */
    public boolean titleIsShow() {
        if (titleActionBar.getVisibility() == View.VISIBLE) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 设置actionbar标题内容和颜色
     *
     * @param text
     * @param color
     */
    public void setTitleText(String text, int color) {
        titleActionBar.setText(text);
        titleActionBar.setTextColor(color);

        titleActionBar.setVisibility(View.VISIBLE);
    }

    /**
     * 设置右按钮样式和点击事件
     *
     * @param drawable
     * @param listener
     */
    public void setRightActionBar(Drawable drawable, OnClickListener listener) {
        rightActionBar.setImageDrawable(drawable);
        rightActionBar.setOnClickListener(listener);
        rightActionBar.setVisibility(View.VISIBLE);
    }
}
