package com.white.hot.doremember.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.white.hot.doremember.R;

/**
 * Created by Administrator on 2016/10/19.
 */

public class CircleImageView extends ImageView
{

    private float borderWidthRate = 0.05f;
    private Paint mBorderPaint;
    private Paint mBmpPaint;
    private Paint mShadowPaint;
    private float radius;
    private float borderWidth;
    private float shadowWidth;
    private Bitmap mSrcBmp;
    private float centerX;
    private float centerY;

    public CircleImageView(Context context)
    {
        super(context);
        init();
    }

    public CircleImageView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init()
    {
        setScaleType(ScaleType.FIT_XY);
        if (getDrawable() != null)
        {
            mSrcBmp = drawable2Bitmap(getDrawable());
        }
        mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setColor(Color.WHITE);
        mBmpPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mShadowPaint.setStyle(Paint.Style.FILL);
    }

    public static Bitmap drawable2Bitmap(Drawable drawable){
        if(drawable instanceof BitmapDrawable){//转换成Bitmap
            return ((BitmapDrawable)drawable).getBitmap() ;
        }else if(drawable instanceof NinePatchDrawable){//.9图片转换成Bitmap
            Bitmap bitmap = Bitmap.createBitmap(
                    drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(),
                    drawable.getOpacity() != PixelFormat.OPAQUE ?
                            Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight());
            drawable.draw(canvas);
            return bitmap;
        }else{
            return null ;
        }
    }

    @Override
    public void setImageDrawable(Drawable drawable)
    {
        super.setImageDrawable(drawable);
        mSrcBmp = drawable2Bitmap(drawable);
    }

    @Override
    public void setImageBitmap(Bitmap bm)
    {
        super.setImageBitmap(bm);
        mSrcBmp = bm;
    }

    @Override
    public void setImageResource(int resId)
    {
        super.setImageResource(resId);
        mSrcBmp = BitmapFactory.decodeResource(getContext().getResources(), resId);
    }

    private int width;
    private int height;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int refWidth = 0;
        int refHeight = 0;
        if (mSrcBmp != null)
        {
            refWidth = mSrcBmp.getWidth();
            refHeight = mSrcBmp.getHeight();
            refWidth = refHeight = Math.min(refHeight, refWidth);
        }
        int widthMode;
        int heightMode;
        widthMode = MeasureSpec.getMode(widthMeasureSpec);
        heightMode = MeasureSpec.getMode(heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode == MeasureSpec.AT_MOST)
        {
            width = refWidth;
        }
        if (heightMode == MeasureSpec.AT_MOST)
        {
            height = refHeight;
        }

        width = height = Math.min(width, height);
        float temp = width / 2;
        borderWidth = temp * borderWidthRate;
        shadowWidth = borderWidth;
        if (allowDrawingStroke)
        {
            if (allowDrawingShadow)
            {
                radius = temp - borderWidth - shadowWidth;
            } else
            {
                radius = temp - borderWidth;
            }
        } else
        {
            radius = temp;
        }
        Matrix matrix = new Matrix();
        float rate = radius * 2.0f / refWidth;
        if (rate != 1)
        {
            matrix.setScale(rate, rate);
            mSrcBmp = Bitmap.createBitmap(mSrcBmp, 0, 0, mSrcBmp.getWidth(), mSrcBmp.getHeight(), matrix, true);
        }
        centerX = width / 2.0f;
        centerY = height / 2.0f;
        setMeasuredDimension(width, height);
    }

    private boolean allowDrawingStroke = true;

    /***
     * 是否允许绘制边框， 默认允许
     */
    public void disallowStrokeDrawing()
    {
        allowDrawingStroke = false;
    }

    private boolean allowDrawingShadow;

    /***
     * 是否允许绘制阴影，默认允许，必须在允许绘制边框的前提下才可以绘制阴影
     */
    public void allowShadowDrawing()
    {
        allowDrawingShadow = true;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        if(allowDrawingShadow)
        {
            drawShadow(canvas);
            drawCircleImage(canvas);
            if (allowDrawingStroke)
            {
                drawShader(canvas);
            }
        }else
        {
            drawCircleImage(canvas);
            if (allowDrawingStroke)
            {
                drawShader(canvas);
            }
        }
    }

    private void drawShadow(Canvas canvas)
    {

        Resources r = getContext().getResources();
        RadialGradient rg = new RadialGradient(centerX, centerY, centerX,
                new int[]{r.getColor(R.color.transparent), r.getColor(R.color.HalfBlack),r.getColor(R.color.transparent)},
                new float[]{0f, (radius + borderWidth) / centerX, 1.0f}, Shader.TileMode.CLAMP);
        Matrix matrix = new Matrix();
        matrix.setTranslate(0f, 5.0f);
        rg.setLocalMatrix(matrix);
        mShadowPaint.setShader(rg);
        canvas.drawCircle(centerX, centerY, centerX, mShadowPaint);
//        mShadowPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
//        canvas.drawColor(Color.TRANSPARENT);
    }

    private void drawShader(Canvas canvas)
    {
        mBorderPaint.setStrokeWidth(borderWidth);
        if (allowDrawingShadow)
        {
            canvas.drawCircle(centerX, centerY, centerX - shadowWidth - borderWidth / 2.0f, mBorderPaint);
        } else
        {
            canvas.drawCircle(centerX, centerY, centerX - borderWidth / 2.0f, mBorderPaint);
        }
    }

    private void drawCircleImage(Canvas canvas)
    {
        mBmpPaint.setShader(new BitmapShader(mSrcBmp, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        canvas.drawCircle(centerX, centerY, radius, mBmpPaint);
    }
}
