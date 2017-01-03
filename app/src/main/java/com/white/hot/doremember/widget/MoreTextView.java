package com.white.hot.doremember.widget;

import android.content.Context;
import android.graphics.Paint;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.white.hot.doremember.R;

/**
 * Created by ytf on 2016/12/15.
 * Description:
 */

public class MoreTextView extends LinearLayout implements ViewTreeObserver.OnGlobalLayoutListener
{
    private TextView tvContent, tvMore;

    public MoreTextView(Context context)
    {
        super(context);
        init();
    }

    public MoreTextView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    private void init()
    {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.more_textview, this, false);
        addView(v);
        tvContent = (TextView) v.findViewById(R.id.tv_content_message);
        tvMore = (TextView) v.findViewById(R.id.more);
    }

    @Override
    protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeGlobalOnLayoutListener(this);
    }

    private boolean isMeasured;

    private String content;

    public void setContent(CharSequence cs)
    {
        if(!TextUtils.isEmpty(cs))
        {
            content = cs.toString();
        }
    }

    private int maxLines = 3;

    public void setTvContentMaxLines(int maxLine)
    {
        this.maxLines = maxLine;
    }

    @Override
    public void onGlobalLayout()
    {
        if(!isMeasured)
        {
            isMeasured = true;
            int width = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();

            tvContent.setMaxLines(maxLines);
            tvContent.setEllipsize(TextUtils.TruncateAt.END);

            if(!TextUtils.isEmpty(content))
            {
                TextPaint tp  =tvContent.getPaint();
                float length = StaticLayout.getDesiredWidth(content, tp);

                contentLineCount = (int) Math.ceil(length / width);
                Paint.FontMetrics fm = tp.getFontMetrics();
                lineHeight = Math.floor(fm.descent - fm.ascent + tvContent.getLineSpacingExtra() + tvContent.getLineSpacingMultiplier())-1;
                if(length / width > maxLines)
                {
                    tvMore.setVisibility(View.VISIBLE);
                }else
                {
                    tvMore.setVisibility(View.GONE);
                }
                tvContent.setText(content);
                doSomthing();
            }
        }
    }

    private double lineHeight;
    private int contentLineCount;

    private void doSomthing()
    {
        tvMore.setOnClickListener(new View.OnClickListener()
        {
            boolean isExpand;

            @Override
            public void onClick(View v)
            {
                isExpand = !isExpand;
                tvContent.clearAnimation();
                final double deltaValue;
                final int startValue = tvContent.getMeasuredHeight();
                int durationMillis = 350;
                if (isExpand)
                {
                    deltaValue = lineHeight * contentLineCount - startValue;
                } else
                {
                    deltaValue = lineHeight * maxLines - startValue;
                }
                Animation animation = new Animation()
                {
                    protected void applyTransformation(float interpolatedTime, Transformation t)
                    {
                        tvContent.setHeight((int) (startValue + deltaValue * interpolatedTime));
                    }

                };
                animation.setDuration(durationMillis);
                animation.setAnimationListener(new Animation.AnimationListener()
                {
                    @Override
                    public void onAnimationStart(Animation animation)
                    {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation)
                    {
                        if(isExpand)
                        {
                            tvMore.setText("收起");
                        }else
                        {
                            tvMore.setText("显示更多");
                        }
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation)
                    {

                    }
                });
                tvContent.startAnimation(animation);
            }
        });
    }
}
