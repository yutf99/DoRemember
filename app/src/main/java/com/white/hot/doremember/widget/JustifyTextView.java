package com.white.hot.doremember.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.SystemClock;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by ytf on 2016/08/24.
 * Description:
 * Modify by xx on 2016/08/24.
 * Modify detail:
 */
public class JustifyTextView extends TextView {

    private int mLineY;
    private int mViewWidth;
    public static final String TWO_CHINESE_BLANK = "  ";

    public JustifyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    private int lin;

    @Override
    protected void onDraw(Canvas canvas) {
        TextPaint paint = getPaint();
        paint.setColor(getCurrentTextColor());
        paint.drawableState = getDrawableState();
        mViewWidth = getMeasuredWidth();
        String text = getText().toString();
        mLineY = 0;
        mLineY += getTextSize();
        Layout layout = getLayout();

        // layout.getLayout()在4.4.3出现NullPointerException
        if (layout == null) {
            return;
        }

        Paint.FontMetrics fm = paint.getFontMetrics();

        int textHeight = (int) (Math.ceil(fm.descent - fm.ascent));
        textHeight = (int) (textHeight * layout.getSpacingMultiplier() + layout.getSpacingAdd());

        lin = isDrawLineAutoControl ? layout.getLineCount() : getLineNum() + 1;

        //拿到最大值
        if(lin > lineCount){
            lineCount = lin;
        }

        for (int i = 0; i < lin; i++) {

            int lineStart = layout.getLineStart(i);
            int lineEnd = layout.getLineEnd(i);
            String str = text.substring(lineStart, lineEnd);
            int blankCount = isFirstLineOfParagraph(str);
            if(blankCount >= 1){
                str = "\u3000\u3000"+str.substring(blankCount);
            }
//            float width = StaticLayout.getDesiredWidth(str, lineStart, str.length(), getPaint());
            float width = StaticLayout.getDesiredWidth(str, paint);
//            String line = text.substring(lineStart, lineEnd);
            String line = str;

            if(!isNotify){
                if(wordCount < (lineEnd - lineStart)) {
                    wordCount = lineEnd - lineStart;
                }
            }

            if(i < lin-1) {
                if (needScale(line)) {
                    drawScaledText(canvas, lineStart, line, width);
                } else {
                    canvas.drawText(line, 0, mLineY, paint);
                }
            }
            else {
                canvas.drawText(line, 0, mLineY, paint);
            }
            mLineY += textHeight;
        }
        if(l != null && (!isNotify)){
            l.valid(getCharNum(), wordCount, lineCount);
            isNotify = true;
            wordCount = 0;
            lineCount = 0;
        }
    }

    //是否已经调过接口了，防止多次调用引起混乱
    private boolean isNotify;
    //是否让控件自己决定绘制多少行，否表示根据屏幕大小控制绘制的行数
    private boolean isDrawLineAutoControl;
    //每行最大可显示字符数
    public int wordCount;
    //每页最大可显示行数
    public int lineCount;

    public void setAutoControl(){
        isDrawLineAutoControl = true;
    }

    private LayoutValidListener l;

    public void setLayoutValidListener(LayoutValidListener l){
        this.l = l;
    }

    public interface LayoutValidListener{
        void valid(int count, int maxChar, int lin);
    }

    /**
     * 获取当前页总字数
     */
    public int getCharNum() {
        Layout layout = getLayout();
        return layout.getLineEnd(getLineNum());
    }

    public int getLineNum() {
        Layout layout = getLayout();
        int topOfLastLine;
        if(!isDrawLineAutoControl){
            topOfLastLine = getHeight() - getPaddingTop() - getPaddingBottom() - getLineHeight()- 30;
        }else{
            topOfLastLine = getHeight() - getPaddingTop() - getPaddingBottom() - getLineHeight();
        }
        return layout.getLineForVertical(topOfLastLine);
    }

    private void drawScaledText(Canvas canvas, int lineStart, String line,
                                float lineWidth) {
        float x = 0;
        int blankCount = isFirstLineOfParagraph(line);
        if (blankCount > 0) {
            String blanks = "　　";
            canvas.drawText(" ", x, mLineY, getPaint());
            float bw = StaticLayout.getDesiredWidth(blanks, getPaint());
            x += bw;

            line = line.substring(blankCount);
        }

        int gapCount = line.length() - 1;
        int i = 0;

        float d = (mViewWidth - lineWidth) / gapCount;
        for (; i < line.length(); i++) {
            String c = String.valueOf(line.charAt(i));
            float cw = StaticLayout.getDesiredWidth(c, getPaint());
            canvas.drawText(c, x, mLineY, getPaint());
            x += cw + d;
        }
    }

    private float gapCountGloable;

    private boolean isFirstLineOfParagraph(int lineStart, String line) {
        return line.contains("　　");
    }
    private int isFirstLineOfParagraph(String line) {
//        return line.length() > 3 && line.charAt(0) == '中'
//                && line.charAt(1) == '中';
        char c;
        int count=0;
        if(line.length() > 2) {
            for(int i=0;i<line.length();i++){
                c = line.charAt(i);
                if(c == '\t' || c == '\u3000' || c == ' '){
                    count++;
                }else{
                    break;
                }
            }
        }
        return count;
    }

    private boolean needScale(String line) {
        if (line == null || line.length() == 0) {
            return false;
        } else {
            return line.charAt(line.length() - 1) != '\n';
        }
    }
}
