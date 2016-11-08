package com.white.hot.doremember.function.audio;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.white.hot.doremember.R;
import com.white.hot.doremember.base.BaseActivity;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import butterknife.Bind;
import jp.wasabeef.blurry.Blurry;

@ContentView(R.layout.activity_audio_player)
public class AudioPlayerActivity extends BaseActivity {

    @ViewInject(R.id.btn1)
    Button btn1;
    @ViewInject(R.id.btn2)
    Button btn2;
    @ViewInject(R.id.btn3)
    Button btn3;
    @ViewInject(R.id.btn4)
    Button btn4;
    @ViewInject(R.id.btns)
    LinearLayout btns;
    @ViewInject(R.id.img)
    ImageView img;
    @ViewInject(R.id.tv_center)
    private TextView tvCenter;
    @ViewInject(R.id.blur_level)
    private SeekBar blurLevel;

    @ViewInject(R.id.blur_layout)
    private RelativeLayout blurLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initActionBar();
        blurLevel.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {

            }
        });
    }

    @Event(value={R.id.btn1,R.id.btn2,R.id.btn3,R.id.btn4})
    private void click(View v){
        switch (v.getId()){
            case R.id.btn1:
                showProgress();
                break;
            case R.id.btn2:
                Blurry.with(this)
                        .radius(100)
                        .sampling(8)
//                        .color(Color.argb(66, 255, 255, 0))
                        .async()
                        .animate(500)
                        .onto(blurLayout);
                break;
            case R.id.btn3:
                Blurry.with(this).capture(tvCenter).into(img);
                break;
            case R.id.btn4:
                startActivity(new Intent(AudioPlayerActivity.this, SoundPoolActivity.class));
                break;
        }
    }

    private void initActionBar() {
        actionBar.setActionBarBackgroundColor(getResources().getColor(R.color.BlueGrade4));
        actionBar.setVisibility(View.VISIBLE);
        actionBar.setTitleText("媒体播放", Color.WHITE);
        actionBar.showLeftBack(R.drawable.back);
    }



}
