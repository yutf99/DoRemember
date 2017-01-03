package com.white.hot.doremember.function.anim;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.white.hot.doremember.R;
import com.white.hot.doremember.widget.MoreTextView;

import java.util.List;

public class MessageAdapter extends BaseAdapter
{

    private Context ctx;
    private List<String> mList;
    private LayoutInflater inflater;

    /**
     * 默认最大行数
     */
    private int maxDescripLine = 2;
    /**
     * 信息内容（全局）
     */
    private TextView mContent;

    public MessageAdapter(Context ctx, List list)
    {
        this.ctx = ctx;
        this.mList = list;
        this.inflater = LayoutInflater.from(ctx);
    }

    @Override
    public int getCount()
    {
        if (mList == null)
        {
            return 0;
        }
        return mList.size();
    }

    @Override
    public Object getItem(int position)
    {
        if (mList == null)
        {
            return 0;
        }
        return mList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        if (mList == null)
        {
            return 0;
        }
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder;

        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.item_message_listview, null);
            holder = new ViewHolder();
            holder.tvContent = (MoreTextView) convertView.findViewById(R.id.cc);
            convertView.setTag(holder);
        }

        holder = (ViewHolder) convertView.getTag();
        holder.tvContent.setTvContentMaxLines(2);
        holder.tvContent.setContent(mList.get(position));
        return convertView;
    }

    public class ViewHolder
    {
        MoreTextView tvContent;
    }

    /**
     * 展开信息
     */
    private void getMoreText(TextView tvContent)
    {
        tvContent.setOnClickListener(new View.OnClickListener()
        {
            boolean isExpand;

            @Override
            public void onClick(View v)
            {
                mContent = (TextView) v.findViewById(R.id.tv_content_message);
                isExpand = !isExpand;
                mContent.clearAnimation();
                final int deltaValue;
                final int startValue = mContent.getHeight();
                int durationMillis = 350;
                if (isExpand)
                {
                    deltaValue = mContent.getLineHeight() * mContent.getLineCount() - startValue;
                    RotateAnimation animation = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    animation.setDuration(durationMillis);
                    animation.setFillAfter(true);
                    mContent.startAnimation(animation);
                } else
                {
                    deltaValue = mContent.getLineHeight() * maxDescripLine - startValue;
                    RotateAnimation animation = new RotateAnimation(180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    animation.setDuration(durationMillis);
                    animation.setFillAfter(true);
                    mContent.startAnimation(animation);
                }
                Animation animation = new Animation()
                {
                    protected void applyTransformation(float interpolatedTime, Transformation t)
                    {
                        mContent.setHeight((int) (startValue + deltaValue * interpolatedTime));
                    }

                };
                animation.setDuration(durationMillis);
                mContent.startAnimation(animation);
            }
        });
    }
}
