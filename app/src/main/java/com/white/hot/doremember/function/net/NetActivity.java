package com.white.hot.doremember.function.net;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.white.hot.doremember.R;
import com.white.hot.doremember.base.BaseActivity;
import com.white.hot.doremember.utils.NetUtil;
import com.white.hot.doremember.utils.OkHttpManager;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.io.File;
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
    private OkHttpManager manager;
    @ViewInject(R.id.et_net)
    private EditText etNet;

    @ViewInject(R.id.pb)
    private ProgressBar pb;
    @ViewInject(R.id.btn_net_down)
    private Button down;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        initActionBar();
        manager = OkHttpManager.newInstance();
        pb.setMax(100);
    }

    @Event(value = {R.id.btn_net, R.id.btn_net_down})
    private void click(View v)
    {
        switch (v.getId())
        {
            case R.id.btn_net:
                String url = etNet.getText().toString();
                manager.doGet(url, new OkHttpManager.ResultCallback<String>()
                {
                    @Override
                    public void onError(String simpleMsg, Exception e)
                    {
                        Toast.makeText(NetActivity.this, "错误", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(String response)
                    {
                        tvNet.setText(response);
                    }
                });
                break;
            case R.id.btn_net_down:
                manager.download("http://img.bbs.csdn.net/upload/201504/30/1430400631_458766.png",
                        Environment.getExternalStorageDirectory() + "/1.jpg", new OkHttpManager.ResultCallback<File>()
                        {
                            @Override
                            public void onError(String simpleMsg, Exception e)
                            {

                            }

                            @Override
                            public void onSuccess(File response)
                            {
                                Toast.makeText(NetActivity.this, "成功", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onProgress(long progress, long allLength, boolean done)
                            {
                                int p = (int) (progress * 100f / allLength);
                                pb.setProgress(p);
                            }
                        });
                break;
        }

    }

    private void initActionBar()
    {
        actionBar.setActionBarBackgroundColor(getResources().getColor(R.color.CyanGrade3));
        actionBar.setVisibility(View.VISIBLE);
        actionBar.setTitleText("网络", Color.WHITE);
        actionBar.showLeftBack(R.drawable.back);
    }
}
