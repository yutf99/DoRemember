package com.white.hot.doremember.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

/**
 * Created by ytf on 2017/1/12.
 * Description:
 */

public class RefreshLoadMoreLayout extends ScrollView
{

    public RefreshLoadMoreLayout(Context context)
    {
        super(context);
        init();
    }

    public RefreshLoadMoreLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    private void init()
    {

    }

    float x1,x2,y1,y2;

    @Override
    public boolean onTouchEvent(MotionEvent ev)
    {
        if(getScrollY() != 0){
            return super.onTouchEvent(ev);
        }

        switch(ev.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return false;
    }



    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt)
    {
        super.onScrollChanged(l, t, oldl, oldt);

        Log.e("s", "onScrollChanged: "+String.valueOf(getScrollY()));
        Log.e("s", "l = " + l + "---" + "---t = " + t + "---" + "---oldl = " + oldl + "---" + "---oldt = " + oldt);
    }

    @Override
    protected void dispatchDraw(Canvas canvas)
    {
        super.dispatchDraw(canvas);
    }

    private void drawWhiteCircle(Canvas canvas)
    {

    }

}
