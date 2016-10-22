package com.white.hot.doremember.function.anim.imgscan_mode.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.view.ScaleGestureDetector;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/10/16.
 */

public class ZoomImageView extends ImageView implements
        ScaleGestureDetector.OnScaleGestureListener, View.OnTouchListener, ViewTreeObserver.OnGlobalLayoutListener
{

    private float[] matrixValues = new float[9];
    private GestureDetector detector;

    public ZoomImageView(Context context)
    {
        super(context);
        init(context);
    }

    public ZoomImageView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }

    public ZoomImageView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context)
    {
        setScaleType(ScaleType.FIT_XY);
        detector = new GestureDetector(context, new GestureDetector
                .SimpleOnGestureListener()
        {
            @Override
            public boolean onDoubleTap(MotionEvent e)
            {

                return super.onDoubleTap(e);
            }
        });
        setOnTouchListener(this);
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector)
    {
        Toast.makeText(getContext(), "onscale", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector)
    {
        Toast.makeText(getContext(), "onScaleBegin", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector)
    {
        Toast.makeText(getContext(), "onScaleEnd", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        return false;
    }

    @Override
    public void onGlobalLayout()
    {
        Toast.makeText(getContext(), "onGlobalLayout", Toast.LENGTH_SHORT).show();
    }
}
