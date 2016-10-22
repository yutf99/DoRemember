package com.white.hot.doremember.function.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.widget.Button;

import com.github.johnpersano.supertoasts.library.SuperToast;
import com.white.hot.doremember.R;
import com.white.hot.doremember.base.BaseActivity;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

@ContentView(R.layout.activity_notification)
public class NotificationActivity extends BaseActivity {

    @ViewInject(R.id.btn_noti_s1)
    Button btnNotiS1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initActionBar();
        mNotiManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    //通知管理器
    private NotificationManager mNotiManager;
    //构建包装意图
    private PendingIntent pendingIntent;
    //builder对象
    private NotificationCompat.Builder mBuilder;

    @Event({R.id.btn_noti_s1})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_noti_s1:
                new SuperToast(this).setText("简单通知").show();
                simpleNoti();
                break;
        }
    }

    private void simpleNoti() {
        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setAutoCancel(true);
//        mBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
        mBuilder.setVibrate(new long[]{0, 300, 500, 700});
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setContentTitle("简单通知");
        mBuilder.setTicker("测试通知来啦");
        mBuilder.setContentText("恭喜中奖300万");
        mNotiManager.notify(0, mBuilder.build());
        pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, NotificationActivity.class), PendingIntent.FLAG_NO_CREATE);
    }

    private void initActionBar() {
        actionBar.setActionBarBackgroundColor(getResources().getColor(R.color.CyanGrade3));
        actionBar.setVisibility(View.VISIBLE);
        actionBar.setTitleText("通知栏学习", Color.WHITE);
        actionBar.showLeftBack(R.drawable.back);
    }
}
