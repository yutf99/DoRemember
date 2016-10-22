package com.white.hot.doremember.function.pdf;

import android.graphics.Color;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.white.hot.doremember.R;
import com.white.hot.doremember.base.BaseActivity;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.io.File;

@ContentView(R.layout.activity_pdf)
public class PDFActivity extends BaseActivity {

//    private PDFView pdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);
        initActionBar();
        init();
    }

    private void init() {
        File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "/test.pdf");
//        pdfView.fromFile(f)
////                .pages(0, 1,2,3)
//                .defaultPage(1)
//                .enableSwipe(true)
////                .onDraw(onDrawListener)
////                .onLoad(onLoadCompleteListener)
////                .onPageChange(onPageChangeListener)
//                .load();
    }

    private void initActionBar() {
        actionBar.setActionBarBackgroundColor(getResources().getColor(R.color.CyanGrade3));
        actionBar.setVisibility(View.VISIBLE);
        actionBar.setTitleText("Pdf", Color.WHITE);
        actionBar.showLeftBack(R.drawable.back);
    }
}
