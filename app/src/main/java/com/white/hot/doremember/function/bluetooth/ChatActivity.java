package com.white.hot.doremember.function.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.white.hot.doremember.R;
import com.white.hot.doremember.base.BaseActivity;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@ContentView(R.layout.activity_chat)
public class ChatActivity extends BaseActivity
{

    @ViewInject(R.id.btn_send)
    Button btnSend;
    @ViewInject(R.id.et_msg)
    EditText etMsg;
    @ViewInject(R.id.lv_chat)
    ListView lvChat;
    @ViewInject(R.id.fl_wait)
    private FrameLayout flWait;
//    @ViewInject(R.id.rb_server)
//    private RadioButton rbServer;
//    @ViewInject(R.id.rb_client)
//    private RadioButton rbClient;
//    @ViewInject(R.id.rg_choose)
//    private RadioGroup rgChoose;
//    @ViewInject(R.id.btn_listen)
//    private Button btnListen;

    private boolean isListening;
    private static UUID SERVER_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
//    private static UUID CLIENT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FC");
    private BluetoothDevice device;
    private ChatAdapter chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getIntentData();
        initActionBar();
        config();
    }

    private void getIntentData()
    {
        device = getIntent().getParcelableExtra("item");
    }

    private ParcelUuid[] uuids;
    private void config()
    {
//        if(device.fetchUuidsWithSdp())
//        {
//            uuids = device.getUuids();
//            if(uuids != null && uuids.length > 0)
//            {
//                SERVER_UUID = uuids[0].getUuid();
//                serverWorker.start();
//            }
//        }
        flWait.setVisibility(View.VISIBLE);
        serverWorker.start();
        clientWorker.start();
    }

    public static final int TYPE_OTHER = 0;
    public static final int TYPE_ME = 1;
    class ChatBean
    {
        public Date time;
        public String content;
        public int chatType;
    }

    private List<ChatBean> chatList = new ArrayList<>();

    class ChatAdapter extends BaseAdapter
    {
        private List<ChatBean> list;

        public ChatAdapter(List<ChatBean> list)
        {
            this.list = list;
        }

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
        public int getViewTypeCount()
        {
            return 2;
        }

        @Override
        public int getItemViewType(int position)
        {
            ChatBean c = list.get(position);
            if(c.chatType == TYPE_OTHER)
            {
                return TYPE_OTHER;
            }else
            {
                return TYPE_ME;
            }
        }

        private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            int viewtype = getItemViewType(position);
            ViewHolder holder = null;
            ChatBean bean = list.get(position);
            switch (viewtype)
            {
                case TYPE_OTHER:
                    if(convertView == null)
                    {
                        convertView = View.inflate(ChatActivity.this, R.layout.item_blue_other_layout, null);
                        holder = new ViewHolder();
                        holder.tvMsg = (TextView) convertView.findViewById(R.id.tv_msg);
                        holder.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
                        convertView.setTag(holder);
                    }else
                    {
                        holder = (ViewHolder) convertView.getTag();
                    }
                    break;
                case TYPE_ME:
                    if(convertView == null)
                    {
                        convertView = View.inflate(ChatActivity.this, R.layout.item_blue_me_layout, null);
                        holder = new ViewHolder();
                        holder.tvMsg = (TextView) convertView.findViewById(R.id.tv_msg);
                        holder.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
                        convertView.setTag(holder);
                    }else
                    {
                        holder = (ViewHolder) convertView.getTag();
                    }
                    break;
            }
            holder.tvMsg.setText(bean.content);
            holder.tvTime.setText(sdf.format(bean.time));
            return convertView;
        }

        public class ViewHolder
        {
            public TextView tvMsg, tvTime;
        }
    }

    public static final String PROTOCOL_SCHEME_RFCOMM = "btspp";

    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case 0:
                    flWait.setVisibility(View.GONE);
                    Toast.makeText(ChatActivity.this, "建立连接", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    ChatBean bean = new ChatBean();
                    bean.chatType = msg.arg1;
                    bean.content = (String) msg.obj;
                    bean.time = new Date();
                    chatList.add(bean);
                    if(chatAdapter == null)
                    {
                        chatAdapter = new ChatAdapter(chatList);
                        lvChat.setAdapter(chatAdapter);
                    }else
                    {
                        chatAdapter.notifyDataSetChanged();
                    }
                    break;
                case 2:
                    Toast.makeText(ChatActivity.this, "对方已掉线", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();

    private BluetoothServerSocket serverSocket;

    private Thread serverWorker = new Thread()
    {
        public void run()
        {
            listen();
        }
    };
    private BluetoothSocket socket;
    private Thread clientWorker = new Thread()
    {
        public void run()
        {
            connect();
        }
    };

    private boolean isExit;

    protected void listen()
    {
        try
        {
            serverSocket = bluetooth.listenUsingRfcommWithServiceRecord(PROTOCOL_SCHEME_RFCOMM, SERVER_UUID);
            mHandler.sendEmptyMessage(0);
            socket = serverSocket.accept();
            if (socket != null)
            {
                new ReadThread(socket).start();
            }
        } catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    // 读取数据
    private class ReadThread extends Thread
    {

        private BluetoothSocket socket;

        public ReadThread(BluetoothSocket socket)
        {
            this.socket = socket;
        }

        public void run()
        {
            InputStream is = null;
            try
            {
                is = socket.getInputStream();
                while (!isExit)
                {
                    int read;
                    byte[] buffer = new byte[1024];
                    if ((read = is.read(buffer)) > 0)
                    {
                        byte[] buf_data = new byte[read];
                        for (int i = 0; i < read; i++)
                        {
                            buf_data[i] = buffer[i];
                        }
                        String s = new String(buf_data, "UTF-8");
                        Message msg = new Message();
                        msg.obj = s;
                        msg.arg1 = TYPE_OTHER;
                        msg.what = 1;
                        mHandler.sendMessage(msg);
                    }
                }
            } catch (IOException e)
            {
                e.printStackTrace();
                try
                {
                    is.close();
                    mHandler.sendEmptyMessage(2);
                } catch (IOException e1)
                {
                    e1.printStackTrace();
                }
            }
        }
    }

    private BluetoothSocket clientSocket;
    private OutputStream clientOsw;
    private boolean isClientUp;
    private void connect()
    {
        try
        {
            clientSocket = device.createInsecureRfcommSocketToServiceRecord(SERVER_UUID);
            clientSocket.connect();
            isClientUp = true;
            mHandler.sendEmptyMessage(0);
//            sendMsg(clientSocket, device, "连接成功");
        } catch (IOException e)
        {
            e.printStackTrace();
            if(!isClientUp)
            {
                connect();
            }
        }
    }

    private void disconnect()
    {
        if (clientSocket != null && clientSocket.isConnected())
        {
            try
            {
                clientSocket.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void sendMsg(BluetoothSocket socket, BluetoothDevice device, String content)
    {
        try
        {
            clientOsw = socket.getOutputStream();
            clientOsw.write(content.getBytes());
            Message msg = mHandler.obtainMessage();
            msg.obj = content;
            msg.arg1 = TYPE_ME;
            msg.what = 1;
            mHandler.sendMessage(msg);
        } catch (IOException e)
        {
            e.printStackTrace();
            mHandler.sendEmptyMessage(2);
        }
    }

    @Override
    public void finish()
    {
        isExit = true;
        shutdownServer();
        disconnect();
        super.finish();
    }

    private void shutdownServer()
    {
        new Thread()
        {
            public void run()
            {

                if (serverSocket != null)
                {
                    serverWorker.interrupt();
                    try
                    {
                        serverSocket.close();
                    } catch (IOException ex)
                    {
                    }
                }
            }
        }.start();
    }

    @Event(value = {R.id.btn_send})
    private void click(View v)
    {
        switch (v.getId())
        {
            case R.id.btn_send:
                String content = etMsg.getText().toString();
                if (!TextUtils.isEmpty(content))
                {
                    sendMsg(clientSocket, device, content);
                } else
                {
                    Toast.makeText(ChatActivity.this, "请输入内容", Toast.LENGTH_SHORT).show();
                }
                break;
//            case R.id.btn_listen:
//                rbServer.setChecked(true);
//                break;
        }
    }

//    @Event(value = {R.id.rg_choose}, type = RadioGroup.OnCheckedChangeListener.class)
//    private void onCheckedChanged(RadioGroup group, int checkedId)
//    {
//        switch (checkedId)
//        {
//            case R.id.rb_server:
//                btnSend.setEnabled(false);
//                flWait.setVisibility(View.VISIBLE);
//                serverWorker.start();
//                break;
//            case R.id.rb_client:
//                btnSend.setEnabled(true);
//                flWait.setVisibility(View.VISIBLE);
//                clientWorker.start();
//                break;
//
//        }
//    }

    private void initActionBar()
    {
        actionBar.setActionBarBackgroundColor(getResources().getColor(R.color.BlueGrade4));
        actionBar.setVisibility(View.VISIBLE);
        actionBar.setTitleText(device.getName(), Color.WHITE);
        actionBar.showLeftBack(R.drawable.back);
    }
}
