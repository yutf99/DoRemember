package com.white.hot.doremember.function.anim.imgscan_mode.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.white.hot.doremember.R;
import com.white.hot.doremember.function.anim.imgscan_mode.album.entity.ImageInfo;
import com.white.hot.doremember.utils.UIHelper;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by ${ytf} on 2016/10/16.
 * descption:
 */

public class PreviewDialog extends Dialog
{

    private Activity activity;
    private List<ImageInfo> imgs;
    private List<ImageView> views = new ArrayList<>();
    private Adapter adapter;
    private ViewPager pager;
    private int currentPosition;
    private PhotoViewAttacher mAttacher;

    public PreviewDialog(Context context, List<ImageInfo> list, int position)
    {
        super(context, R.style.PreviewDialogStle);
        activity = (Activity)context;

//        Window win = getWindow();
//        win.getDecorView().setPadding(0, 0, 0, 0);
//        WindowManager.LayoutParams lp = win.getAttributes();
//        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
//        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
//        win.setAttributes(lp);

        imgs = list;
        currentPosition = position;
        if(imgs != null && imgs.size() > 0)
        {
            ViewPager.LayoutParams params = new ViewPager.LayoutParams();
            params.width = ViewPager.LayoutParams.WRAP_CONTENT;
            params.height = ViewPager.LayoutParams.WRAP_CONTENT;
            for(int i=0;i < imgs.size(); i++)
            {
                ImageView iv = new ImageView(activity);
                iv.setLayoutParams(params);
                mAttacher = new PhotoViewAttacher();
                views.add(iv);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.preivew_dialog, null, false);
        AutoUtils.auto(contentView);
        setContentView(contentView);

        pager = (ViewPager) contentView.findViewById(R.id.vp_preview);
        adapter = new Adapter();
        pager.setAdapter(adapter);
        pager.setCurrentItem(currentPosition);
        super.onCreate(savedInstanceState);
    }


    class Adapter extends PagerAdapter
    {

        @Override
        public int getCount()
        {
            return views == null ? 0 : views.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object)
        {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position)
        {
            ImageView ziv = views.get(position);
            container.addView(ziv);
            UIHelper.showImg(ziv, "file://" + imgs.get(position).getImageFile().getAbsolutePath());
            return ziv;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object)
        {

            container.removeView((View) object);
        }
    }
}
