package com.white.hot.doremember.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by ytf on 2017/2/11.
 * Description:
 */

public class BatteryIcon extends View
{
    private Paint mOutboxPaint, mInnerPaint;
    private final float MIN_WIDTH = 50;
    private final float MIN_HEIGHT = 22;
    private final int DEFAULT_OUT_COLOR = 0xFF000000;
    private final int DEFAULT_INNER_COLOR = 0xFF00FF00;
    private float cornor;
    private float  strokeWidth;
    private float innerPadding;
    private float batteryHatLength;

    private int outColor = DEFAULT_OUT_COLOR;
    private int innerColor = DEFAULT_INNER_COLOR;

    private RectF outRect, innerRect, batteryHatRect;
    private Path rechargingPath;

    public BatteryIcon(Context context)
    {
        super(context);
        init();
    }

    public BatteryIcon(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    private void init()
    {
        mOutboxPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mOutboxPaint.setStyle(Paint.Style.STROKE);
        mOutboxPaint.setColor(outColor);
        mInnerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mInnerPaint.setStyle(Paint.Style.FILL);
        mInnerPaint.setColor(innerColor);

        outRect = new RectF();
        innerRect = new RectF();
        batteryHatRect = new RectF();

        rechargingPath = new Path();
    }


    public void setInnerColor(int innerColor)
    {
        this.innerColor = innerColor;
        mInnerPaint.setColor(innerColor);
        postInvalidate();
    }

    public void setOutColor(int outColor)
    {
        this.outColor = outColor;
        mOutboxPaint.setColor(outColor);
        postInvalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int wmode = MeasureSpec.getMode(widthMeasureSpec);
        int hmode = MeasureSpec.getMode(heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        if(wmode == MeasureSpec.AT_MOST)
        {
            width = (int) Math.min(MIN_WIDTH, width);
        }else if(wmode == MeasureSpec.EXACTLY){}
        else
        {
            width = (int) MIN_WIDTH;
        }

        if(hmode == MeasureSpec.AT_MOST)
        {
            height = (int) Math.min(MIN_HEIGHT, height);
        }else if(hmode == MeasureSpec.EXACTLY){}
        else
        {
            height = (int) MIN_HEIGHT;
        }
        setMeasuredDimension(width, height);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        strokeWidth = height / 8f;
        batteryHatLength = height / 4f;
        mOutboxPaint.setStrokeWidth(strokeWidth);
        cornor = strokeWidth;
        innerPadding = strokeWidth / 2f;
        float halfStrokeWidth = strokeWidth / 2f;

        outRect.left = strokeWidth + halfStrokeWidth;
        outRect.top = halfStrokeWidth;
        outRect.right = width - halfStrokeWidth;
        outRect.bottom = height - halfStrokeWidth;

        innerRect.left = outRect.left + strokeWidth;
        innerRect.top = outRect.top + strokeWidth;
        innerRect.right = outRect.right - strokeWidth;
        innerRect.bottom = outRect.bottom - strokeWidth;

        float center = getMeasuredHeight()/2f;
        float halfHatLength = batteryHatLength /2f;

        batteryHatRect.left = halfStrokeWidth;
        batteryHatRect.top = center - halfHatLength;
        batteryHatRect.right = strokeWidth + halfStrokeWidth;
        batteryHatRect.bottom = center + halfHatLength;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        drawOut(canvas);
        if(onRecharging)
        {
            drawRechargingIcon(canvas);
        }else
        {
            drawInner(canvas);
        }
    }

    private void drawOut(Canvas canvas)
    {
        canvas.drawRoundRect(outRect, cornor, cornor, mOutboxPaint);
        canvas.drawRoundRect(batteryHatRect, cornor/3f*2, cornor/3f*2, mOutboxPaint);
    }


    private void drawRechargingIcon(Canvas canvas)
    {
        int w = getMeasuredWidth();
        int h = getMeasuredHeight();
        float hw = w / 2f;
        float hh = h / 2f;
        rechargingPath.moveTo(hw + hh/3f*2, strokeWidth);
        rechargingPath.lineTo(hw - hh/3f, hh);
        rechargingPath.lineTo(hw + hh/3f*2, hh);
        rechargingPath.lineTo(hw - hh/3f, h-strokeWidth);
        rechargingPath.lineTo(hw + hh/3f*2, strokeWidth);
        rechargingPath.close();
        canvas.drawPath(rechargingPath, mInnerPaint);
    }

    private boolean onRecharging = false;

    public void setOnRecharging(boolean tf)
    {
        onRecharging = tf;
        postInvalidate();
    }

    private void drawInner(Canvas canvas)
    {
        float cr = cornor/3f*2;
        if(level == 100)
        {
            canvas.drawRoundRect(innerRect,cr , cr, mInnerPaint);
        }else
        {
            float r = 1 - level / 100f;
            float innerWidth = innerRect.right - innerRect.left;
            innerRect.left = innerRect.left + innerWidth * r;
            canvas.drawRoundRect(innerRect, cr, cr, mInnerPaint);
            canvas.drawRect(innerRect.left, innerRect.top, innerRect.left + cr, innerRect.bottom, mInnerPaint);
        }

    }

    private int level = 0;

    public void setLevel(int v)
    {
        level = v;
        postInvalidate();
    }

}
