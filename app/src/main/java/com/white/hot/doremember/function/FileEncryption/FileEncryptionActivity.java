package com.white.hot.doremember.function.FileEncryption;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.white.hot.doremember.R;
import com.white.hot.doremember.base.BaseActivity;
import com.white.hot.doremember.utils.FileUtils;
import com.white.hot.doremember.widget.JustifyTextView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import butterknife.Bind;

@ContentView(R.layout.activity_file_encryption)
public class FileEncryptionActivity extends BaseActivity {

    @ViewInject(R.id.btn_source)
    private Button btnSource;
    @ViewInject(R.id.btn_encrypt)
    private Button btnEncrypt;
    @ViewInject(R.id.btn_decrypt)
    private Button btnDecrypt;
    @ViewInject(R.id.j_tv)
    private JustifyTextView jTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initActionBar();
    }

    @Event(value={R.id.btn_source, R.id.btn_encrypt, R.id.btn_decrypt},type=View.OnClickListener.class)
    private void onClick(View v){
        switch (v.getId()){
            case R.id.btn_source:
                createFile();
                String text = readFile();
                jTv.setText(text);
                break;
            case R.id.btn_encrypt:
                File f = new File(Environment.getExternalStorageDirectory(), "/x.txt");
                if(!f.exists()){
                    createFile();
                }
                try {
                    File okFile = CipherUtil.encry("12345", f);
                    if(okFile != null && okFile.exists()){
                        jTv.setText("加密成功！加密文件路径为："+okFile.getAbsolutePath());
                    }else{
                        jTv.setText("加密失败！");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    finish();
                }
                break;
            case R.id.btn_decrypt:
                try {
                    File okFile = CipherUtil.decry(new File(Environment.getExternalStorageDirectory(), "/x.dat"), ".txt");
                    if(okFile != null && okFile.exists()){
                        jTv.setText("解密成功！文件内容是："+readFile());
                    }else{
                        jTv.setText("解密失败！");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    finish();
                }
                break;
        }
    }

    private String readFile() {
        File f = new File(Environment.getExternalStorageDirectory(), "/x.txt");

        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            fis = new FileInputStream(f);
            isr = new InputStreamReader(fis, "UTF-8");
            br = new BufferedReader(isr);
            StringBuffer sb = new StringBuffer();
            sb.append(br.readLine());
            return sb.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if(br != null){
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void createFile(){

        File f = FileUtils.createFile(Environment.getExternalStorageDirectory().getAbsolutePath(), "/x.txt");
        if(f.exists()){
            return ;
        }else{
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        StringBuffer sb = new StringBuffer();
        for(int i=0;i<200;i++){
            sb.append("平常心就好！");
        }
        FileOutputStream fos = null;
        OutputStreamWriter osw = null;
        try {
            fos = new FileOutputStream(f);
            osw = new OutputStreamWriter(fos, "UTF-8");
            osw.write(sb.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(osw != null){
                    osw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void initActionBar() {
        actionBar.setActionBarBackgroundColor(getResources().getColor(R.color.CyanGrade3));
        actionBar.setVisibility(View.VISIBLE);
        actionBar.setTitleText("加密解密", Color.WHITE);
        actionBar.showLeftBack(R.drawable.back);
    }
}
