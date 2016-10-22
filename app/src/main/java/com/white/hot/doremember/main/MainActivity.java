package com.white.hot.doremember.main;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.white.hot.doremember.R;
import com.white.hot.doremember.widget.BaseActionBar;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

@ContentView(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

    @ViewInject(R.id.navigation_view)
    private NavigationView navigationView;
    @ViewInject(R.id.main_layout)
    private DrawerLayout mainLayout;
    @ViewInject(R.id.vp_main)
    private ViewPager vpMain;
    @ViewInject(R.id.activity_base_actionbar)
    private BaseActionBar actionBar;

    private MainPageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWindowFeature();
        x.view().inject(this);
        initActionBar();
        initView();
        initListener();
    }

    private void initWindowFeature() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }else{
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    private void initListener() {
        mainLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
//                View mContent = mainLayout.getChildAt(0);
//                View mMenu = drawerView;
//                Log.e("s", "slideOffset="+slideOffset);
//                float scale = 1 - slideOffset;
//                float rightScale = 0.8f + scale * 0.2f;
//                float leftScale = 1 - 0.3f * scale;
//
//                mMenu.setScaleX(rightScale);
//                mMenu.setScaleY(leftScale);
//                mMenu.setAlpha(0.6f + 0.4f * (1 - scale));
//                mContent.setTranslationX(mMenu.getMeasuredWidth() * (1 - scale));
//                mContent.setPivotX(0);
//                mContent.setPivotY(mContent.getMeasuredHeight() / 2);
//                mContent.invalidate();
//                mContent.setScaleX(rightScale);
//                mContent.setScaleY(rightScale);

            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    private void initView() {
//        mainLayout.setScrimColor(0x00000000);
        adapter = new MainPageAdapter(getSupportFragmentManager());
        vpMain.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public class MainPageAdapter extends FragmentPagerAdapter {

        public MainPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public Fragment getItem(int position) {
            MainFragment frag = new MainFragment();
            ArrayList<String> itemName = new ArrayList<>();
            itemName.add("通知栏");
            itemName.add("音乐播放");
            itemName.add("PDF阅读");
            itemName.add("加密解密");
            itemName.add("动画");
            Bundle b = new Bundle();
            b.putStringArrayList("datas", itemName);
            frag.setArguments(b);
            return frag;
        }
    }

    private void initActionBar() {
        actionBar.setActionBarBackgroundColor(getResources().getColor(R.color.BlueGrade4));
        actionBar.setVisibility(View.VISIBLE);
        actionBar.setTitleText("功能", Color.WHITE);
        actionBar.setLeft(R.drawable.back);
    }
}
