package com.white.hot.doremember.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.white.hot.doremember.utils.DrawableUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by ytf on 2017/2/6.
 * Description: 弹幕控件
 */

public class PopBarrage extends ViewGroup
{

    private final int DEFAULT_FONT_SIZE = 30;
    private final int DEFAULT_STROKE_WIDTH = 3;
    private final int DEFAULT_PADDING = 18;

    private int width;
    private int height;

    private List<Barrage> listBarrage;

    public PopBarrage(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        listBarrage = new ArrayList<>();
        new Thread()
        {
            @Override
            public void run()
            {
                while (!needStop)
                {
                    SystemClock.sleep(100);
                    handler.sendEmptyMessage(0);
                }
                listBarrage.clear();
            }
        }.start();
    }

    private boolean needStop;

    @Override
    protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
        needStop = true;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        for(int i = 0; i < getChildCount();i++)
        {
            Barrage v = (Barrage) getChildAt(i);
            Log.e("s", v.getMeasuredWidth()+"---");
            v.measure(0,0);
            v.layout(v.locationX, v.locationY, v.locationX+v.getMeasuredWidth(), v.locationY+v.getMeasuredHeight());
//            v.layout(50, 50, 250, 250);
        }
    }

    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            if(listBarrage != null && listBarrage.size() > 0)
            {
                for(Barrage b : listBarrage)
                {
                    b.move();
                }
            }
        }
    };

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
    }

    public void addBarrage(String content)
    {
        Barrage b = new Barrage(getContext());
        b.setContent(content, 0);
        addView(b);
        listBarrage.add(b);
    }

    class Barrage extends TextView
    {
        public String content;
        public Paint mPaint;
        public int type;
        public int locationY;
        public int locationX;
        TextView tv;
        private Random rd = new Random();

        public Barrage(Context context)
        {
            super(context);
        }

        public void setContent(String content, int type)
        {
            this.content = content;
            this.type = type;

            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPaint.setTextSize(30);
            GradientDrawable bg = DrawableUtils.createStrokeShape(10, Color.RED);
//             tv = new TextView(PopBarrage.this.getContext());
            setTextSize(TypedValue.COMPLEX_UNIT_PX, DEFAULT_FONT_SIZE);
            setTextColor(Color.RED);
            setPadding(DEFAULT_PADDING,DEFAULT_PADDING,DEFAULT_PADDING,DEFAULT_PADDING);
            setBackground(bg);
            setText(content);
            locationX = rd.nextInt(width);
            locationY = rd.nextInt(height);
        }

        public void move()
        {
            locationX -= 2;
            if(locationX <= 0)
            {
                locationX = 0;
            }
            setTranslationX(locationX);
            postInvalidate();
        }
    }
}
