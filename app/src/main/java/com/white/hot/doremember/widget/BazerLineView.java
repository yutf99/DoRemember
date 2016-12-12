package com.white.hot.doremember.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposePathEffect;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by ytf on 2016/10/28.
 * Description:
 * Modify by xx on 2016/10/28.
 * Modify detail:
 */
public class BazerLineView extends View {

    private int width;
    private int height;
    private Path path;
    private Paint paint;
    private Thread mThread;
    private boolean isCleanUp;

    public BazerLineView(Context context) {
        super(context);
        init();
    }

    public BazerLineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private ScheduledExecutorService ser;
    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.CYAN);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);
        path = new Path();
        paint.setPathEffect(new CornerPathEffect(10));
        ser = Executors.newScheduledThreadPool(1);
        ser.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                isCleanUp = true;
                postInvalidate();
            }
        }, 0,5, TimeUnit.SECONDS);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(ser != null)
        {
            try {
                ser.awaitTermination(100, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getWidth();
        height = getHeight();
    }

    private boolean isStart;
    private int x;
    private int y;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch(action)
        {
            case MotionEvent.ACTION_DOWN:
                x = (int) event.getX();
                y = (int) event.getY();
                path.moveTo(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(event.getX(), event.getY());
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.BLACK);
        if(isCleanUp)
        {
            isCleanUp = false;
            canvas.translate(10, 10);
//            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        }else
        {

        }
        canvas.drawPath(path, paint);
    }
}
