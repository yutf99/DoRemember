package com.white.hot.doremember.function.net;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.white.hot.doremember.R;
import com.white.hot.doremember.base.BaseActivity;
import com.white.hot.doremember.utils.NetUtil;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@ContentView(R.layout.activity_net)
public class NetActivity extends BaseActivity
{


    @ViewInject(R.id.btn_net)
    Button btnNet;
    @ViewInject(R.id.tv_net)
    TextView tvNet;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        initActionBar();
    }

    @Event(value = R.id.btn_net)
    private void click(View v)
    {
        NetUtil.doGet("https://www.baidu.com", new Handler(){
            @Override
            public void handleMessage(Message msg)
            {
                switch (msg.what)
                {
                    case NetUtil.WHAT_NET_OK:
                        String s = msg.getData().getString(NetUtil.NET_OK_CONTENT_KEY);
                        tvNet.setText(s);
                        break;
                }

            }
        });
    }

    private void initActionBar()
    {
        actionBar.setActionBarBackgroundColor(getResources().getColor(R.color.CyanGrade3));
        actionBar.setVisibility(View.VISIBLE);
        actionBar.setTitleText("网络", Color.WHITE);
        actionBar.showLeftBack(R.drawable.back);
    }
}
