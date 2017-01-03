package com.white.hot.doremember.function.explosion;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.white.hot.doremember.R;
import com.white.hot.doremember.base.BaseActivity;

import org.xutils.view.annotation.ViewInject;

public class ExplosionActivity extends BaseActivity
{

    @ViewInject(R.id.iv_explosion)
    private ImageView ivExlposion;

    private ExplosionField ef;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explosion);
        ivExlposion = (ImageView) findViewById(R.id.iv_explosion);
        initActionBar();
        ef = ExplosionField.attach2Window(this);
        ef.expandExplosionBound(10,5);
    }

    public void click(View v)
    {
//        ivExlposion.getDrawable();
        ef.explode(ivExlposion);
        v.setOnClickListener(null);
    }

    private void initActionBar() {
        actionBar.setActionBarBackgroundColor(getResources().getColor(R.color.BlueGrade4));
        actionBar.setVisibility(View.VISIBLE);
        actionBar.setTitleText("View爆炸", Color.WHITE);
    }
}
