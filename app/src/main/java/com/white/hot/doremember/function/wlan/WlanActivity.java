package com.white.hot.doremember.function.wlan;

import android.content.Context;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.white.hot.doremember.R;
import com.white.hot.doremember.adapter.DefaultAdapter;
import com.white.hot.doremember.base.BaseActivity;
import com.white.hot.doremember.holder.BaseViewHolder;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@ContentView(R.layout.activity_wlan)
public class WlanActivity extends BaseActivity
{

    @ViewInject(R.id.button1)
    private Button mTestButton;
    @ViewInject(R.id.IPTEXTVIEWID)
    private TextView tvTip;
    @ViewInject(R.id.SHOWIPLISTVIEWID)
    private ListView showiplistview;
    @ViewInject(R.id.wait)
    private ProgressBar pbWait;

    private WifiManager mWifiManager;
    public List<String> ping; // ping 后的结果集
    boolean isOver;
    private String ipSegament;
    private ThreadPoolExecutor threadPool;
    private Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        initActionBar();

        ping = new ArrayList<>();
        mWifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        threadPool = new ThreadPoolExecutor(20, 20, 5, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(256));
    }

    private void initActionBar()
    {
        actionBar.setActionBarBackgroundColor(getResources().getColor(R.color.CyanGrade3));
        actionBar.setVisibility(View.VISIBLE);
        actionBar.setTitleText("局域网", Color.WHITE);
        actionBar.showLeftBack(R.drawable.back);
    }

    @Event(R.id.button1)
    private void click(View v)
    {
        switch (v.getId())
        {
            case R.id.button1:
                pbWait.setVisibility(View.VISIBLE);
                tvTip.setText("正在扫描...");
                new Thread()
                {
                    public void run()
                    {

                        String ip = intToIp(mWifiManager.getConnectionInfo().getIpAddress());
                        ipSegament = ip.substring(0, ip.lastIndexOf("."));
                        for (int i = 0; i < 256; i++)
                        {
                            if (i != 1)
                            {
                                String wlanIp = ipSegament + "." + i;
                                Inet4Address i4 =Inet4Address;
                                runPingIPprocess(wlanIp);
                            }
                        }
                    }
                }.start();
                break;
            default:
                break;
        }
    }

    private String intToIp(int i)
    {
        return (i & 0xFF) + "." +((i >> 8) & 0xFF) + "." +((i >> 16) & 0xFF) + "." +(i >> 24 & 0xFF);
    }


    private int allIp = 0;
    public void runPingIPprocess(final String ipString)
    {

        final Runnable pingThread = new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    Process p = Runtime.getRuntime().exec("ping -c 1 -w 3 " + ipString);
                    int status = p.waitFor();
                    if (status == 0)
                    {
                        ping.add(ipString);
                        mUpdaHandler.sendEmptyMessage(0);
                    }
                    allIp++;
                    if(allIp == 255)
                    {
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                pbWait.setVisibility(View.GONE);
                                tvTip.setText("扫描完成");
                            }
                        });
                    }
                } catch (Exception e)
                {
                    e.printStackTrace();
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            Toast.makeText(WlanActivity.this, "出现异常", Toast.LENGTH_SHORT).show();
                        }
                    });
                    allIp++;
                    if(allIp == 255)
                    {
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                Toast.makeText(WlanActivity.this, "扫描完成", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                }
            }
        };
        threadPool.execute(pingThread);
    }


    private Handler mUpdaHandler = new Handler()
    {

        @Override
        public void handleMessage(Message msg)
        {

            switch (msg.what)
            {
                case 0:
                    if(adapter == null)
                    {
                        adapter = new Adapter(WlanActivity.this,  ping);
                        showiplistview.setAdapter(adapter);
                    }else
                    {
                        adapter.notifyDataSetChanged();
                    }
                    break;
                case 1:
                    String[] s = (String[]) msg.obj;
                    tvTip.setText("本机IP:" + s[1] + "\n路由器IP:" + s[0]);
                    break;
                case 2:
                    break;
                default:
                    break;
            }
        }
    };

    class Adapter extends DefaultAdapter<String>
    {

        public Adapter(Context context, List<String> datas)
        {
            super(context, datas);
        }

        @Override
        protected BaseViewHolder<String> getHolderInstance()
        {
            return new ViewHolder();
        }

        class ViewHolder extends BaseViewHolder<String>
        {

            @ViewInject(R.id.tvIp)
            TextView tvIP;

            @Override
            protected View getViewLayout()
            {
                return View.inflate(WlanActivity.this, R.layout.item_wlan_ip, null);
            }

            @Override
            protected void refreshView(int position)
            {
                tvIP.setText(data);
            }
        }
    }
}
