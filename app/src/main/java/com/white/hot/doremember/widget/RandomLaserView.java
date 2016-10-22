package com.white.hot.doremember.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.SystemClock;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.white.hot.doremember.utils.ColorMachine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * TODO: document your custom view class.
 */
public class RandomLaserView extends View
{

    private TextPaint mTextPaint;
    private Paint laserPaint;

    private float width;
    private float height;

    public RandomLaserView(Context context)
    {
        super(context);
        init(null, 0);
    }

    public RandomLaserView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(attrs, 0);
    }

    public RandomLaserView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle)
    {

        mTextPaint = new TextPaint();
        laserPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        laserPaint.setStyle(Paint.Style.STROKE);
        laserPaint.setStrokeWidth(2);
        laserPaint.setColor(Color.RED);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        run(10);
    }


    @Override
    protected void onDraw(Canvas canvas)
    {

        if (listLine != null && listLine.size() > 0)
        {
            for (Iterator<Line> it = listLine.iterator(); it.hasNext(); )
            {
                Line l = it.next();
                if (l.isOver())
                {
                    it.remove();
                }
            }
            for (Line l : listLine)
            {
                l.drawLine(canvas);
            }
        }

    }

    private final List<Line> listLine = new ArrayList<>();
    private boolean isExit;

    private void run(final int lineCount)
    {
        final Random random = new Random();

        new Thread()
        {
            public void run()
            {
                while (!isExit)
                {
                    synchronized (listLine)
                    {
                        if (listLine.size() < lineCount)
                        {
                            int startX = random.nextInt((int) width) + 1;
                            int startY = random.nextInt((int) height) + 1;
                            //改变X还是Y
                            boolean xOrY = random.nextBoolean();
                            //增加还是减少
                            boolean addOrDecrease = random.nextBoolean();
                            Line l = new Line(startX, startY, xOrY, addOrDecrease);
                            listLine.add(l);
                        }
                        for (int i = 0; i < listLine.size(); i++)
                        {
                            Line l = listLine.get(i);
                            l.move();
                        }
                        postInvalidate();

                        SystemClock.sleep(5);
                    }

                }
            }

        }.start();
    }

    @Override
    protected void onDetachedFromWindow()
    {
        isExit = true;
        super.onDetachedFromWindow();
    }

    public class Line
    {

        float startX;
        float startY;
        float endX;
        float endY;
        Paint paint;
        boolean xOrY;
        boolean addOrDecrease;
        private boolean isMoveOver;

        public Line()
        {
            endX = startX = 0;
            endY = startY = 0;
            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(3);
            paint.setColor(ColorMachine.getColor());
        }

        private Line(float startX, float startY, boolean xOrY, boolean addOrDecrease)
        {
            endX = this.startX = startX;
            endY = this.startY = startY;
            this.xOrY = xOrY;
            this.addOrDecrease = addOrDecrease;
            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(3);
            paint.setColor(ColorMachine.getColor());
        }

        public boolean isOver()
        {
            return this.isMoveOver;
        }

        public void setStartX(float x)
        {
            this.startX = x;
        }

        public void setStartY(float y)
        {
            this.startY = y;
        }

        public float getStartX()
        {
            return startX;
        }

        public float getStartY()
        {
            return startY;
        }

        public float getEndX()
        {
            return endX;
        }

        public float getEndY()
        {
            return endY;
        }

        private void move()
        {
            if (xOrY)
            {
                if (addOrDecrease)
                {
                    if (endX == width)
                    {
                        modifyStartX(1);
                    } else
                    {
                        modifyEndX(1);
                    }
                } else
                {
                    if (endX == 0)
                    {
                        modifyStartX(1);
                    } else
                    {
                        modifyEndX(1);
                    }
                }
            } else
            {
                if (addOrDecrease)
                {
                    if (endY == height)
                    {
                        modifyStartY(1);
                    } else
                    {
                        modifyEndY(1);
                    }
                } else
                {
                    if (endY == 0)
                    {
                        modifyStartY(1);
                    } else
                    {
                        modifyEndY(1);
                    }
                }
            }
        }

        /**
         * @param addX 增量
         */
        private void modifyStartX(int addX)
        {
            if (addOrDecrease)
            {
                if (!isMoveOver)
                {
                    startX += addX;
                    if (startX == width)
                    {
                        isMoveOver = true;
                    }
                }
            } else
            {
                if (!isMoveOver)
                {
                    startX -= addX;
                    if (startX == 0)
                    {
                        isMoveOver = true;
                    }
                }

            }
        }

        private void modifyEndX(int addX)
        {
            if (addOrDecrease)
            {
                endX += addX;
            } else
            {
                endX -= addX;
            }
        }

        private void modifyEndY(int addY)
        {
            if (addOrDecrease)
            {
                endY += addY;
            } else
            {
                endY -= addY;
            }
        }

        private void modifyStartY(int addY)
        {
            if (addOrDecrease)
            {
                if (!isMoveOver)
                {
                    startY += addY;
                    if (startY == height)
                    {
                        isMoveOver = true;
                    }
                }
            } else
            {
                if (!isMoveOver)
                {
                    startY -= addY;
                    if (startY == 0)
                    {
                        isMoveOver = true;
                    }
                }
            }
        }

        private void drawLine(Canvas canvas)
        {
            canvas.drawLine(startX, startY, endX, endY, this.paint);
        }
    }
}

