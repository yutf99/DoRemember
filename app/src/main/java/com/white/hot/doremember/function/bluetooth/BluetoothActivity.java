package com.white.hot.doremember.function.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.white.hot.doremember.R;
import com.white.hot.doremember.base.BaseActivity;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@ContentView(R.layout.activity_bluetooth)
public class BluetoothActivity extends BaseActivity
{
    @ViewInject(R.id.btn)
    private Button btn;

    @ViewInject(R.id.lv)
    private ListView lv;

    @ViewInject(R.id.tv_state)
    private TextView tvState;

    @ViewInject(R.id.pb_wait)
    private ProgressBar pbWait;

    private List<BluetoothDevice> list = new ArrayList<>();

    private Adapter adapter;
    private DeveiceFoundReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        initActionBar();
        receiver = new DeveiceFoundReceiver();
        listen();
        initView();
    }

    private void listen()
    {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(receiver, intentFilter);
    }

    private BluetoothAdapter bAdapter;

    private void initView()
    {
        bAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bAdapter != null)
        {
            if (bAdapter.isEnabled())
            {
                pbWait.setVisibility(View.VISIBLE);
                tvState.setText("正在搜索设备...");
                btn.setText("关闭蓝牙");
                list.addAll(bAdapter.getBondedDevices());
                adapter = new Adapter();
                lv.setAdapter(adapter);
                bAdapter.startDiscovery();
            } else
            {
                btn.setText("打开蓝牙");
            }
        } else
        {
            btn.setText("设备不支持");
        }
        btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (bAdapter != null)
                {
                    if (bAdapter.isEnabled())
                    {
                        bAdapter.disable();
                        btn.setText("打开蓝牙");
                        list.clear();
                        listen();
                        adapter.notifyDataSetChanged();
                    } else
                    {
                        //打开蓝牙
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        //可被发现
                        enableBtIntent.setAction(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                        //5分钟超时, 不能超出此值
                        enableBtIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                        startActivityForResult(enableBtIntent, 1);
                        registerReceiver(receiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
                    }
                } else
                {
                    Toast.makeText(BluetoothActivity.this, "设备不支持蓝牙", Toast.LENGTH_SHORT).show();
                }
            }

        });
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
            {
                BluetoothDevice device = list.get(position);
                if (device.getBondState() == BluetoothDevice.BOND_BONDED)
                {
                    Method removeBondMethod = null;
                    try
                    {
                        removeBondMethod = device.getClass().getMethod("removeBond");
                        removeBondMethod.invoke(device);
                        adapter.notifyDataSetChanged();
                    } catch (NoSuchMethodException e)
                    {
                        e.printStackTrace();
                    } catch (IllegalAccessException e)
                    {
                        e.printStackTrace();
                    } catch (InvocationTargetException e)
                    {
                        e.printStackTrace();
                    }

                }
                return true;
            }
        });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                if(bAdapter.isDiscovering())
                {
                    bAdapter.cancelDiscovery();
                }
                BluetoothDevice device = list.get(position);
                if (device.getBondState() == BluetoothDevice.BOND_NONE)
                {
                    try
                    {
                        Method createBondMethod = BluetoothDevice.class.getMethod("createBond");
                        createBondMethod.invoke(device);
                        adapter.notifyDataSetChanged();
                    } catch (NoSuchMethodException e)
                    {
                        e.printStackTrace();
                    } catch (IllegalAccessException e)
                    {
                        e.printStackTrace();
                    } catch (InvocationTargetException e)
                    {
                        e.printStackTrace();
                    }
                } else
                {
                    Intent intent = new Intent(BluetoothActivity.this, ChatActivity.class);
                    intent.putExtra("item", device);

                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        if (requestCode == 1)
        {
            if (300 == resultCode)
            {
                pbWait.setVisibility(View.VISIBLE);
                tvState.setText("正在搜索设备...");
                btn.setText("关闭蓝牙");
                Toast.makeText(BluetoothActivity.this, "打开成功", Toast.LENGTH_SHORT).show();
                list.addAll(bAdapter.getBondedDevices());
                adapter = new Adapter();
                lv.setAdapter(adapter);
                bAdapter.startDiscovery();
            } else
            {
                btn.setText("打开蓝牙");
                Toast.makeText(BluetoothActivity.this, "操作取消", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class DeveiceFoundReceiver extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action))
            {

                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                list.add(device);
                if (adapter == null)
                {
                    adapter = new Adapter();
                    lv.setAdapter(adapter);
                } else
                {
                    adapter.notifyDataSetChanged();
                }
            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action))
            {
                adapter.notifyDataSetChanged();
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
            {
                pbWait.setVisibility(View.GONE);
                tvState.setTextColor(Color.GREEN);
                tvState.setText("搜索完成!");
                bAdapter.cancelDiscovery();
            }
        }
    }

    public class Adapter extends BaseAdapter
    {

        @Override
        public int getCount()
        {
            return list == null ? 0 : list.size();
        }

        @Override
        public Object getItem(int position)
        {
            return position;
        }

        @Override
        public long getItemId(int position)
        {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            if (convertView == null)
            {
                convertView = LayoutInflater.from(BluetoothActivity.this).inflate(R.layout.item_bluedevice, parent, false);
                ViewHolder holder = new ViewHolder();
                holder.tvName = (TextView) convertView.findViewById(R.id.tv_device_name);
                holder.tvAddress = (TextView) convertView.findViewById(R.id.tv_device_mac);
                holder.tvBound = (TextView) convertView.findViewById(R.id.tv_device_bound);
                holder.ivType = (ImageView) convertView.findViewById(R.id.iv_type);
                convertView.setTag(holder);
            }

            ViewHolder holder = (ViewHolder) convertView.getTag();
            BluetoothDevice device = list.get(position);
            holder.tvName.setText("NAME："+device.getName());
            holder.tvAddress.setText("MAC："+device.getAddress());
            int type = device.getBluetoothClass().getMajorDeviceClass();
            if (type == BluetoothClass.Device.Major.COMPUTER)
            {
                holder.ivType.setImageResource(R.drawable.pc);
            } else if (type == BluetoothClass.Device.Major.PHONE)
            {
                holder.ivType.setImageResource(R.drawable.phone);
            } else
            {
                holder.ivType.setImageResource(R.drawable.pad);
            }
            int boundState = device.getBondState();
            switch (boundState)
            {
                case BluetoothDevice.BOND_BONDED:
                    holder.tvBound.setTextColor(Color.GREEN);
                    holder.tvBound.setText("已配对");
                    break;
                case BluetoothDevice.BOND_BONDING:
                    holder.tvBound.setTextColor(Color.GRAY);
                    holder.tvBound.setText("正在配对");
                    break;
                case BluetoothDevice.BOND_NONE:
                    holder.tvBound.setTextColor(Color.BLACK);
                    holder.tvBound.setText("未配对");
                    break;
            }
            return convertView;
        }

        class ViewHolder
        {
            TextView tvName, tvAddress, tvBound;
            ImageView ivType;
        }
    }

    public class PairingReceiver extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent)
        {
            if (intent.getAction().equals("android.bluetooth.device.action.PAIRING_REQUEST"))
            {
                BluetoothDevice btDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                try
                {
                    setPin(btDevice.getClass(), btDevice, "12345"); // 手机和蓝牙采集器配对
                    createBond(btDevice.getClass(), btDevice);
                    cancelPairingUserInput(btDevice.getClass(), btDevice);
                }
                catch (Exception e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    static public boolean setPin(Class btClass, BluetoothDevice btDevice, String str) throws Exception
    {
        try
        {
            Method removeBondMethod = btClass.getDeclaredMethod("setPin",new Class[]{byte[].class});
            removeBondMethod.invoke(btDevice,new Object[]{str.getBytes()});
        }
        catch (SecurityException e)
        {
            e.printStackTrace();
        }
        catch (IllegalArgumentException e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return true;
    }

    static public boolean createBond(Class btClass, BluetoothDevice btDevice) throws Exception
    {
        Method createBondMethod = btClass.getMethod("createBond");
        Boolean returnValue = (Boolean) createBondMethod.invoke(btDevice);
        return returnValue.booleanValue();
    }

    // 取消用户输入
    static public boolean cancelPairingUserInput(Class btClass,BluetoothDevice device) throws Exception
    {
        Method createBondMethod = btClass.getMethod("cancelPairingUserInput");
        Boolean returnValue = (Boolean) createBondMethod.invoke(device);
        return returnValue.booleanValue();
    }

    private void initActionBar() {
        actionBar.setActionBarBackgroundColor(getResources().getColor(R.color.BlueGrade4));
        actionBar.setVisibility(View.VISIBLE);
        actionBar.setTitleText("蓝牙", Color.WHITE);
        actionBar.showLeftBack(R.drawable.back);
    }
}
