package com.white.hot.doremember.function.audio;

import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.white.hot.doremember.R;
import com.white.hot.doremember.base.BaseActivity;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.OnClick;

@ContentView(R.layout.activity_sound_pool)
public class SoundPoolActivity extends BaseActivity
{

    @ViewInject(R.id.btn_drum)
    Button btnDrum;
    @ViewInject(R.id.btn_clap)
    Button btnClap;
    @ViewInject(R.id.btn_bass)
    Button btnBass;
    @ViewInject(R.id.btn_hat)
    Button btnHat;
    @ViewInject(R.id.btn_piano1)
    Button btnPiano1;
    @ViewInject(R.id.btn_snare)
    Button btnSnare;
    @ViewInject(R.id.activity_sound_pool)
    LinearLayout activitySoundPool;

    private SoundPool soundPool;
    private AudioManager audioManager;
    private HashMap<Integer, Integer> soundMap = new HashMap<>();
    private int maxVolume;
    private boolean isLoaded;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        initActionBar();
        init();
    }

    private void init()
    {
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        /* 5.0创建方法
        SoundPool.Builder builder = new SoundPool.Builder();
        builder.setMaxStreams(6);
        //AudioAttributes是一个封装音频各种属性的方法
        AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
        attrBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC);
        //设置音频流的合适的属性
        builder.setAudioAttributes(attrBuilder.build());
        //加载一个AudioAttributes
        soundPool = builder.build();*/

        //兼容方式创建
        soundPool = new SoundPool(6, AudioManager.STREAM_MUSIC, 5);

        soundMap.put(1, soundPool.load(this, R.raw.drum, 1));
        soundMap.put(2, soundPool.load(this, R.raw.clap, 1));
        soundMap.put(3, soundPool.load(this, R.raw.bass, 1));
        soundMap.put(4, soundPool.load(this, R.raw.hat, 1));
        soundMap.put(5, soundPool.load(this, R.raw.piano1, 1));
        soundMap.put(6, soundPool.load(this, R.raw.snare, 1));
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener()
        {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status)
            {
                isLoaded = true;
            }
        });
        Touch t = new Touch();
        btnDrum.setOnTouchListener(t);
        btnClap.setOnTouchListener(t);
        btnBass.setOnTouchListener(t);
        btnHat.setOnTouchListener(t);
        btnPiano1.setOnTouchListener(t);
        btnSnare.setOnTouchListener(t);
    }

    private void initActionBar()
    {
        actionBar.setActionBarBackgroundColor(getResources().getColor(R.color.BlueGrade4));
        actionBar.setVisibility(View.VISIBLE);
        actionBar.setTitleText("声音池", Color.WHITE);
        actionBar.showLeftBack(R.drawable.back);
    }

    private long[] time = new long[6];

    public class Touch implements View.OnTouchListener
    {

        @Override
        public boolean onTouch(View v, MotionEvent event)
        {
            int action = event.getAction();
            if (action == MotionEvent.ACTION_DOWN)
            {
                if (isLoaded)
                {
                    switch (v.getId())
                    {
                        case R.id.btn_drum:
                            time[0] =
                                    soundPool.play(soundMap.get(1), maxVolume * 0.8f, maxVolume * 0.8f, 1, 0, 1);
                            break;
                        case R.id.btn_clap:
                            soundPool.play(soundMap.get(2), maxVolume * 0.8f, maxVolume * 0.8f, 1, 0, 1);
                            break;
                        case R.id.btn_bass:
                            soundPool.play(soundMap.get(3), maxVolume * 0.8f, maxVolume * 0.8f, 1, 0, 1);
                            break;
                        case R.id.btn_hat:
                            soundPool.play(soundMap.get(4), maxVolume * 0.8f, maxVolume * 0.8f, 1, 0, 1);
                            break;
                        case R.id.btn_piano1:
                            soundPool.play(soundMap.get(5), maxVolume * 0.8f, maxVolume * 0.8f, 1, 0, 1);
                            break;
                        case R.id.btn_snare:
                            soundPool.play(soundMap.get(6), maxVolume * 0.8f, maxVolume * 0.8f, 1, 0, 1);
                            break;
                    }
                }
            }
            return false;
        }
    }

    private boolean isRelease;

    public class ScheduleTask extends Thread
    {
        int step = 1;
        int ms;

        public ScheduleTask(int ms)
        {
            this.ms = ms;
        }
        public void run()
        {
            while(!isRelease)
            {
                step++;
                if(step == ms);
            }
            isRelease = true;
        }
    }

    @Override
    protected void onDestroy()
    {
        if (soundPool != null)
        {
            soundPool.release();
        }
        super.onDestroy();
    }
}
