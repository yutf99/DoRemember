package com.white.hot.doremember.function.anim;

import android.animation.LayoutTransition;
import android.app.ListActivity;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.white.hot.doremember.R;
import com.white.hot.doremember.adapter.DefaultAdapter;
import com.white.hot.doremember.base.BaseActivity;
import com.white.hot.doremember.holder.BaseViewHolder;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

@ContentView(R.layout.activity_list_view)
public class ListViewActivity extends BaseActivity
{

    @ViewInject(R.id.lv)
    private ListView lv;
    private Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        initActionBar();
        List<String> l = new ArrayList();
        for(int i=0; i < 20;i++)
        {
            l.add("String");
        }

//        LayoutTransition layoutTransition = new LayoutTransition();
//        layoutTransition.setAnimator(LayoutTransition.APPEARING, layoutTransition.getAnimator(LayoutTransition.APPEARING));
//        layoutTransition.setAnimator(LayoutTransition.CHANGE_APPEARING, layoutTransition.getAnimator(LayoutTransition.CHANGE_APPEARING));
//        layoutTransition.setAnimator(LayoutTransition.CHANGE_DISAPPEARING, layoutTransition.getAnimator(LayoutTransition.CHANGE_DISAPPEARING));
//        layoutTransition.setAnimator(LayoutTransition.CHANGING, layoutTransition.getAnimator(LayoutTransition.CHANGING));
//        layoutTransition.setAnimator(LayoutTransition.DISAPPEARING, layoutTransition.getAnimator(LayoutTransition.DISAPPEARING));
//        lv.setLayoutTransition(layoutTransition);
        adapter = new Adapter(this, l);

    }

    @Event(R.id.btn)
    private void click(View v)
    {
        lv.setAdapter(adapter);
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.anim_listview);
        LayoutAnimationController loc = new LayoutAnimationController(anim);
        loc.setOrder(LayoutAnimationController.ORDER_NORMAL);
        loc.setDelay(0.3f);
        lv.setLayoutAnimation(loc);
    }

    private void initActionBar() {
        actionBar.setActionBarBackgroundColor(getResources().getColor(R.color.BlueGrade4));
        actionBar.setVisibility(View.VISIBLE);
        actionBar.setTitleText("动画", Color.WHITE);
        actionBar.showLeftBack(R.drawable.back);
    }

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

            @ViewInject(R.id.tv)
            private TextView tv;

            @Override
            protected View getViewLayout()
            {
                return View.inflate(ListViewActivity.this, R.layout.item_anim_listview, null);
            }

            @Override
            protected void refreshView(int position)
            {
                tv.setText(data);
            }
        }
    }

}
