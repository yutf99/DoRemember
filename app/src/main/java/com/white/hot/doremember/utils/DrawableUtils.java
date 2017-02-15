package com.white.hot.doremember.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.Shape;

/**
 * Created by ytf on 2016/08/10.
 * Description:
 * Modify by xx on 2016/08/10.
 * Modify detail:
 */
public class DrawableUtils {

    public static GradientDrawable createShape(int radiusPx, int color){
        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadius(radiusPx);
        drawable.setColor(color);
        return drawable;
    }

    public static GradientDrawable createStrokeShape(int radiusPx, int color)
    {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadius(radiusPx);
        drawable.setColor(Color.TRANSPARENT);
        drawable.setStroke(3, color);
        return drawable;
    }

    public static StateListDrawable createSelectorDrawable(Drawable activeState, Drawable normalState){
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{android.R.attr.state_checked}, activeState);
        stateListDrawable.addState(new int[]{}, normalState);
        return stateListDrawable;
    }

    public static ColorStateList createColorStateList(int activeColor, int normalColor){
        int[][] states = new int[2][];
        states[0] = new int[]{android.R.attr.state_checked};
        states[1] = new int[]{};
        return new ColorStateList(states, new int[]{activeColor, normalColor});
    }

    public static ColorStateList createColorStateList(Context context, int resId){
        return context.getResources().getColorStateList(resId);
    }

    public static StateListDrawable createSelectorDrawable(Context context, int resId){
        return  (StateListDrawable) context.getResources().getDrawable(resId);
    }

}
