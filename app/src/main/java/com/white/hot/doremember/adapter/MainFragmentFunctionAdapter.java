package com.white.hot.doremember.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.white.hot.doremember.R;
import com.white.hot.doremember.holder.BaseViewHolder;

import org.xutils.view.annotation.ViewInject;

import java.util.List;

import butterknife.Bind;

/**
 * Created by Administrator on 2016/9/24.
 */
public class MainFragmentFunctionAdapter extends DefaultAdapter<String> {
    public MainFragmentFunctionAdapter(Context context, List<String> datas) {
        super(context, datas);
    }

    @Override
    protected BaseViewHolder<String> getHolderInstance() {
        return new ViewHolder();
    }

    public class ViewHolder extends BaseViewHolder<String> {

        @ViewInject(R.id.item_function)
        TextView itemFunction;

        @Override
        protected View getViewLayout() {
            return View.inflate(context, R.layout.item_main_grid, null);
        }

        @Override
        protected void refreshView(int position) {
            itemFunction.setText(data);
        }
    }
}
