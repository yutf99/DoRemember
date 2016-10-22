package com.white.hot.doremember.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.white.hot.doremember.holder.BaseViewHolder;
import com.zhy.autolayout.utils.AutoUtils;

import org.xutils.x;

import java.util.List;

/**
 * Created by ytf on 2016/08/10.
 * Description:
 * Modify by xx on 2016/08/10.
 * Modify detail:
 */
public abstract class DefaultAdapter<D> extends BaseAdapter {

    public List<D> datas;
    public Context context;

    public DefaultAdapter(Context context, List<D> datas){
        this.datas = datas;
        this.context = context;
    }

    @Override
    public int getCount() {
        return datas == null ? 0 : datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas == null ? null : datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        BaseViewHolder holder = null;
        if(convertView == null){
            holder = getHolderInstance();
            AutoUtils.auto(holder.getContentView());
            x.view().inject(holder, holder.getContentView());
        }else{
            holder = (BaseViewHolder) convertView.getTag();
        }
        //特殊的的地方在小说宫格显示，无数据的地方补全
        holder.setData(position < datas.size() ? datas.get(position) : null, position);

        return holder.getContentView();
    }

    protected abstract BaseViewHolder<D> getHolderInstance();
}
