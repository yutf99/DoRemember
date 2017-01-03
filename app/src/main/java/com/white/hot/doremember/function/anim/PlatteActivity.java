package com.white.hot.doremember.function.anim;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.white.hot.doremember.R;
import com.white.hot.doremember.base.BaseActivity;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

@ContentView(R.layout.activity_platte)
public class PlatteActivity extends BaseActivity
{
    @ViewInject(R.id.ll)
    private LinearLayout ll;
    @ViewInject(R.id.imageView)
    private ImageView iv;
    @ViewInject(R.id.btn)
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
  }

    @Event(value = {R.id.btn})
    private void click(View v)
    {
        switch (v.getId())
        {
            case R.id.btn:
                Bitmap bmp = ((BitmapDrawable)iv.getDrawable()).getBitmap();
                Palette.generateAsync(bmp, new Palette.PaletteAsyncListener()
                {
                    @Override
                    public void onGenerated(Palette palette)
                    {
                        Palette.Swatch s1 = palette.getVibrantSwatch(); //充满活力的色板

                        Palette.Swatch s2 = palette.getDarkVibrantSwatch(); //充满活力的暗色类型色板

                        Palette.Swatch s3 = palette.getLightVibrantSwatch(); //充满活力的亮色类型色板

                        Palette.Swatch s4 = palette.getMutedSwatch(); //黯淡的色板

                        Palette.Swatch s5 = palette.getDarkMutedSwatch(); //黯淡的暗色类型色板（翻译过来没有原汁原味的赶脚啊！）

                        Palette.Swatch s6 = palette.getLightMutedSwatch(); //黯淡的亮色类型色板

                        if(s1 != null)
                        {
                            TextView v = new TextView(PlatteActivity.this);
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(300, ViewGroup.LayoutParams.MATCH_PARENT);
                            v.setLayoutParams(params);
                            v.setBackgroundColor(s1.getRgb());
                            v.setText("活力板");
                            v.setTextColor(s1.getBodyTextColor());
                            ll.addView(v);
                        }
                        if(s2 != null)
                        {
                            TextView v = new TextView(PlatteActivity.this);
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(300, ViewGroup.LayoutParams.MATCH_PARENT);
                            v.setLayoutParams(params);
                            v.setBackgroundColor(s2.getRgb());
                            v.setText("暗活板");
                            v.setTextColor(s2.getBodyTextColor());
                            ll.addView(v);
                        }
                        if(s3 != null)
                        {
                            TextView v = new TextView(PlatteActivity.this);
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(300, ViewGroup.LayoutParams.MATCH_PARENT);
                            v.setLayoutParams(params);
                            v.setBackgroundColor(s3.getRgb());
                            v.setText("亮活板");
                            v.setTextColor(s3.getBodyTextColor());
                            ll.addView(v);
                        }
                        if(s4 != null)
                        {
                            TextView v = new TextView(PlatteActivity.this);
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(300, ViewGroup.LayoutParams.MATCH_PARENT);
                            v.setLayoutParams(params);
                            v.setBackgroundColor(s4.getRgb());
                            v.setText("暗色板");
                            v.setTextColor(s4.getBodyTextColor());
                            ll.addView(v);
                        }
                        if(s5 != null)
                        {
                            TextView v = new TextView(PlatteActivity.this);
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(300, ViewGroup.LayoutParams.MATCH_PARENT);
                            v.setLayoutParams(params);
                            v.setBackgroundColor(s5.getRgb());
                            v.setText("暗暗板");
                            v.setTextColor(s5.getBodyTextColor());
                            ll.addView(v);
                        }
                        if(s6 != null)
                        {
                            TextView v = new TextView(PlatteActivity.this);
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(300, ViewGroup.LayoutParams.MATCH_PARENT);
                            v.setLayoutParams(params);
                            v.setBackgroundColor(s6.getRgb());
                            v.setText("暗亮板");
                            v.setTextColor(s6.getBodyTextColor());
                            ll.addView(v);
                        }
                    }
                });
                break;
        }
    }
}
