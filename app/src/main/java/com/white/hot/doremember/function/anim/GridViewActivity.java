package com.white.hot.doremember.function.anim;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.GridView;
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

@ContentView(R.layout.activity_grid_view)
public class GridViewActivity extends BaseActivity
{

    @ViewInject(R.id.gv)
    private GridView gv;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        initActionBar();
    }

    private void initActionBar() {
        actionBar.setActionBarBackgroundColor(getResources().getColor(R.color.BlueGrade4));
        actionBar.setVisibility(View.VISIBLE);
        actionBar.setTitleText("动画", Color.WHITE);
        actionBar.showLeftBack(R.drawable.back);
    }

    @Event(R.id.btn)
    private void click(View v)
    {
        List<String> l = new ArrayList();
        for(int i=0; i<8;i++)
        {
            l.add("ssad");
        }
        Adapter adapter = new Adapter(this, l);
        gv.setAdapter(adapter);
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.anim_gridview);
        CustomLayoutAnimationController controller = new CustomLayoutAnimationController(anim);
        controller.setOrder(LayoutAnimationController.ORDER_NORMAL);
        controller.setDelay(0.3f);
        gv.setLayoutAnimation(controller);
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
                return View.inflate(GridViewActivity.this, R.layout.item_anim_gridview, null);
            }

            @Override
            protected void refreshView(int position)
            {
                tv.setText(data);
            }
        }
    }

    class CustomLayoutAnimationController extends LayoutAnimationController
    {

        // 7 just lucky number
        public static final int ORDER_CUSTOM  = 7;

        private Callback onIndexListener;

        public void setOnIndexListener(Callback onIndexListener) {
            this.onIndexListener = onIndexListener;
        }

        public CustomLayoutAnimationController(Animation anim) {
            super(anim);
        }

        public CustomLayoutAnimationController(Animation anim, float delay) {
            super(anim, delay);
        }

        public CustomLayoutAnimationController(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        /**
         * override method for custom play child view animation order
         */
        protected int getTransformedIndex(AnimationParameters params) {
            if(getOrder() == ORDER_CUSTOM  && onIndexListener != null) {
                return onIndexListener.onIndex(this, params.count, params.index);
            } else {
                return super.getTransformedIndex(params);
            }
        }

        /**
         * callback for get play animation order
         *
         */

    }

    public interface Callback{

        public int onIndex(CustomLayoutAnimationController controller, int count, int index);
    }
}
