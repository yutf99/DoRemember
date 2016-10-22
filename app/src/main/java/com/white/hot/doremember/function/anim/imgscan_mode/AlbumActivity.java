package com.white.hot.doremember.function.anim.imgscan_mode;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.white.hot.doremember.R;
import com.white.hot.doremember.constant.ActivityCode;
import com.white.hot.doremember.function.anim.imgscan_mode.album.entity.AlbumFolderInfo;
import com.white.hot.doremember.function.anim.imgscan_mode.album.entity.AlbumView;
import com.white.hot.doremember.function.anim.imgscan_mode.album.entity.AlbumViewData;
import com.white.hot.doremember.function.anim.imgscan_mode.album.entity.ImageChooseView;
import com.white.hot.doremember.function.anim.imgscan_mode.album.entity.ImageInfo;
import com.white.hot.doremember.function.anim.imgscan_mode.album.presenter.ImageScannerPresenter;
import com.white.hot.doremember.function.anim.imgscan_mode.album.presenter.ImageScannerPresenterImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 系统相册页面
 *
 * created by ytf on 2016/7/7
 *
 */
public class AlbumActivity extends AppCompatActivity implements View.OnClickListener, ImageChooseView, AlbumView {

    private final static String FRAGMENT_BACK_STACK = "FragmentBackStack";

    /**
     * 相册列表页面
     */
    private AlbumFolderFragment mAlbumFolderFragment;
    /**
     * 相册详情页面
     */
    private HashMap<AlbumFolderInfo, AlbumDetailFragment> mAlbumDetailFragmentMap = new HashMap<>();
    /**
     * 被选中的图片文件列表
     */
    private ArrayList<String> mSelectedImageFileList = new ArrayList<>();

    private ImageScannerPresenter mImageScannerPresenter;
    /**
     * 相册目录信息列表
     */
    private List<AlbumFolderInfo> mAlbumFolderInfoList;
    /**
     * 显示图片目录的名称，选中图片的按钮
     */
    private TextView mTitleView, mSelectedView;

    private FrameLayout navibar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置沉浸式样式
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_album);

        adjustTitleBar();

        receiveIntentData();

        initView();

    }

    private boolean isIntentFromPostcard = false;
//    private boolean flagUserHead = false;
    private int leftCount = 6;


    private void receiveIntentData(){
        Intent intent = getIntent();
        //判断是否是上传明信片发起的意图
        isIntentFromPostcard = intent.getBooleanExtra("isIntentFromPostcard", false);
    }

    private void initView(){

        mTitleView = (TextView) findViewById(R.id.tv_dir_title);
        mSelectedView = (TextView) findViewById(R.id.tv_selected_ok);
        mSelectedView.setOnClickListener(this);
        findViewById(R.id.iv_back).setOnClickListener(this);
        navibar = (FrameLayout) findViewById(R.id.fl);
        mImageScannerPresenter = new ImageScannerPresenterImpl(this);
        mImageScannerPresenter.startScanImage(getApplicationContext(), getSupportLoaderManager());
    }

    private void adjustTitleBar() {

        int identifyId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if(identifyId > 0){
            int resultHeight = getResources().getDimensionPixelSize(identifyId);
            findViewById(R.id.fl).setPadding(0, resultHeight, 0, 0);
        }
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.iv_back) {
            onBackPressed();
        } else if (viewId == R.id.tv_selected_ok) {
            Intent intent = new Intent();
            intent.putExtra("path", mSelectedImageFileList);
            setResult(ActivityCode.RES_ANIM_IMG_CHOOSE, intent);
            finish();
        }
    }

    /**
     * 刷新目录名称
     *
     * @param albumFolderName
     */
    private void refreshFolderName(String albumFolderName) {
        if (!TextUtils.isEmpty(albumFolderName)) {
            mTitleView.setText(albumFolderName);
        }
    }

    /**
     * 切换到相册列表
     */
    private void switchAlbumFolderList() {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, mAlbumFolderFragment);
        fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                int backStackCount = fragmentManager.getBackStackEntryCount();
                if (backStackCount == 0) {
                    AlbumFolderInfo albumFolderInfo = mAlbumFolderInfoList.get(0);
                    String folderName = albumFolderInfo.getFolderName();
                    refreshFolderName(folderName);
                }
            }
        });
        //fragmentTransaction.commit(); //会产生 java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState
        fragmentTransaction.commitAllowingStateLoss();//http://stackoverflow.com/questions/25486656/java-lang-illegalstateexceptioncan-not-perform-this-action-after-onsaveinstance
    }

    //判断是否处于图片浏览状态
    private boolean isBroswerImage;
    //图片浏览详情页的fragment
    private AlbumDetailFragment albumDetailFragment;
    //当前选中的图片文件夹
    private AlbumFolderInfo albumFolderInfo;

    /***
     * 进入图片文件夹
     * @param albumFolderInfo 指定图片目录的信息
     */
    @Override
    public void switchAlbumFolder(AlbumFolderInfo albumFolderInfo) {
        if (albumFolderInfo != null) {
            this.albumFolderInfo = albumFolderInfo;
            isBroswerImage = true;
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            albumDetailFragment = mAlbumDetailFragmentMap.get(albumFolderInfo);
            if (albumDetailFragment == null) {
                List<ImageInfo> imageInfoList = albumFolderInfo.getImageInfoList();
                albumDetailFragment = AlbumDetailFragment.newInstance(imageInfoList);
                mAlbumDetailFragmentMap.put(albumFolderInfo, albumDetailFragment);
            }
            fragmentTransaction.replace(R.id.fragment_container, albumDetailFragment);
            fragmentTransaction.addToBackStack(FRAGMENT_BACK_STACK);
            fragmentTransaction.commit();

            refreshFolderName(albumFolderInfo.getFolderName());
        }
    }

    /**
     * 刷新选中按钮的状态
     */

    private void refreshSelectedViewState() {
        if (mSelectedImageFileList.size() == 0) {
            mSelectedView.setVisibility(View.GONE);
        } else {
            String selectedStringFormat = getString(R.string.selected_ok);
            int selectedSize = mSelectedImageFileList.size();
            String selectedString = String.format(selectedStringFormat, selectedSize, albumFolderInfo.getImageInfoList().size());
            mSelectedView.setText(selectedString);
            mSelectedView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void refreshAlbumData(AlbumViewData albumData) {
        if (albumData != null) {
            mAlbumFolderInfoList = albumData.getAlbumFolderInfoList();
            mAlbumFolderFragment = AlbumFolderFragment.newInstance(mAlbumFolderInfoList);
            switchAlbumFolderList();
            findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);//显示相册列表区域
        } else {
            findViewById(R.id.fragment_container).setVisibility(View.GONE);//隐藏显示相册列表的区域
            findViewById(R.id.tv_no_image).setVisibility(View.VISIBLE);//显示没有相片的提示
        }
    }

    @Override
    public void refreshSelectedCounter(ImageInfo imageInfo) {
        if(leftCount == 0){
            Toast.makeText(this, "最多只能选择6张照片!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (imageInfo != null) {
            boolean isSelected = imageInfo.isSelected();
            String imagePath = imageInfo.getImageFile().getAbsolutePath();
            if (isSelected) {//选中
                if (!mSelectedImageFileList.contains(imagePath)) {
                    mSelectedImageFileList.add(imagePath);
                    leftCount--;
                }
            } else {//取消选中
                if (mSelectedImageFileList.contains(imagePath)) {
                    mSelectedImageFileList.remove(imagePath);
                    if(mSelectedImageFileList.size() == 0){
                        albumDetailFragment.unselectedAll();
                    }
                    leftCount++;
                }
            }
            refreshSelectedViewState();
        }
    }

    @Override
    public void onBackPressed() {
        if(isBroswerImage){
            if(albumDetailFragment.selectedState){
                mSelectedImageFileList.clear();
                leftCount = 6;
                refreshSelectedViewState();
                albumDetailFragment.unselectedAll();
            }else{
                super.onBackPressed();
            }
        }else{
            super.onBackPressed();
        }
    }
}
