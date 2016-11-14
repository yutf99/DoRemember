package com.white.hot.doremember.widget;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;

import com.white.hot.doremember.utils.UIHelper;

/**
 * Created by ytf on 2016/11/9.
 * descption:
 */

public class CircleProgressBar extends View implements ViewTreeObserver.OnGlobalLayoutListener
{
    private int MIN_RADIUS = (int) UIHelper.dp2px(getContext(), 4);
    private int radius;
    private int width;
    private int height;
    private Paint mPaint;
    private int firstColor;
    private int SecondColor;

    public CircleProgressBar(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public CircleProgressBar(Context context)
    {
        super(context);
    }

    private boolean onLayout;

    @Override
    public void onGlobalLayout()
    {
        if(!onLayout)
        {
            onLayout = true;
        }
    }
}
