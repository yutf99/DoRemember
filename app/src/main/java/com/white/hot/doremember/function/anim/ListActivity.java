package com.white.hot.doremember.function.anim;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ListViewCompat;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.white.hot.doremember.R;
import com.white.hot.doremember.adapter.DefaultAdapter;
import com.white.hot.doremember.base.BaseActivity;
import com.white.hot.doremember.holder.BaseViewHolder;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;


@ContentView(R.layout.activity_list)
public class ListActivity extends BaseActivity
{


    @ViewInject(R.id.lv)
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        initActionBar();
        List<String> list = new ArrayList<>();
        for(int i=0; i< 100; i++)
        {
            list.add("学会坚强");
        }
        Adapter a = new Adapter(this, list);
        lv.setAdapter(a);
    }


    private void initActionBar() {
        actionBar.setActionBarBackgroundColor(getResources().getColor(R.color.BlueGrade4));
        actionBar.setVisibility(View.VISIBLE);
        actionBar.setTitleText("list", Color.WHITE);
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
            return new ViewHoder();
        }

        class ViewHoder extends BaseViewHolder<String>
        {
            @ViewInject(R.id.tv_content)
            private TextView tv;

            @Override
            protected View getViewLayout()
            {
                return View.inflate(ListActivity.this, R.layout.item_anim_list, null);
            }

            @Override
            protected void refreshView(int position)
            {
                tv.setText(data);
            }
        }
    }

}
