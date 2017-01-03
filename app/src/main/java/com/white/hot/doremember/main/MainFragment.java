package com.white.hot.doremember.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.white.hot.doremember.R;
import com.white.hot.doremember.adapter.MainFragmentFunctionAdapter;
import com.white.hot.doremember.base.BaseFragment;
import com.white.hot.doremember.function.FileEncryption.FileEncryptionActivity;
import com.white.hot.doremember.function.anim.AnimationActivity;
import com.white.hot.doremember.function.audio.AudioPlayerActivity;
import com.white.hot.doremember.function.bluetooth.BluetoothActivity;
import com.white.hot.doremember.function.explosion.ExplosionActivity;
import com.white.hot.doremember.function.notification.NotificationActivity;
import com.white.hot.doremember.function.net.NetActivity;
import com.white.hot.doremember.function.snapshot.ScreenCaptureImageActivity;
import com.white.hot.doremember.function.wlan.WlanActivity;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/9/24.
 */
@ContentView(R.layout.fragment_main)
public class MainFragment extends BaseFragment
{

    private MainFragmentFunctionAdapter adapter;
    private ArrayList<String> datas;

    @ViewInject(R.id.gv_function_item)
    private GridView gvFunction;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        datas = getArguments().getStringArrayList("datas");
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        if (datas != null && datas.size() > 0)
        {
            adapter = new MainFragmentFunctionAdapter(mContext, datas);
            gvFunction.setAdapter(adapter);
        }
        gvFunction.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                switch (position)
                {
                    case 0:
                        startActivity(new Intent(mContext, NotificationActivity.class));
                        break;
                    case 1:
                        startActivity(new Intent(mContext, AudioPlayerActivity.class));
                        break;
                    case 2:
                        startActivity(new Intent(mContext, NetActivity.class));
                        break;
                    case 3:
                        startActivity(new Intent(mContext, FileEncryptionActivity.class));
                        break;
                    case 4:
                        startActivity(new Intent(mContext, AnimationActivity.class));
                        break;
                    case 5:
                        startActivity(new Intent(mContext, BluetoothActivity.class));
                        break;
                    case 6:
                        startActivity(new Intent(mContext, ScreenCaptureImageActivity.class));
                        break;
                    case 7:
                        startActivity(new Intent(mContext, WlanActivity.class));
                        break;
                    case 8:
                        startActivity(new Intent(mContext, ExplosionActivity.class));
                        break;
                }
            }
        });
    }
}
