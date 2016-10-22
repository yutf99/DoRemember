package com.white.hot.doremember.function.anim.imgscan_mode;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;

import com.white.hot.doremember.R;
import com.white.hot.doremember.constant.ActivityCode;
import com.white.hot.doremember.function.anim.imgscan_mode.album.entity.ImageChooseView;
import com.white.hot.doremember.function.anim.imgscan_mode.album.entity.ImageInfo;
import com.white.hot.doremember.function.anim.imgscan_mode.album.imageloader.ImageLoaderFactory;
import com.white.hot.doremember.function.anim.imgscan_mode.album.imageloader.ImageLoaderWrapper;
import com.white.hot.doremember.function.anim.imgscan_mode.widget.PreviewDialog;

import java.io.Serializable;
import java.util.List;

/**
 * 相册详情页面
 *
 */
public class AlbumDetailFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";

    /**
     * 图片选择View层交互接口
     */
    private ImageChooseView mImageChooseView;
    /**
     * 相册信息列表
     */
    private List<ImageInfo> mImageInfoList;
    /**
     * 相册视图控件
     */
    private GridView mAlbumGridView;
    private BaseAdapter mAlbumGridViewAdapter;

    /**
     * @param imageInfoList 相册列表
     * @return
     */
    public static AlbumDetailFragment newInstance(List<ImageInfo> imageInfoList) {
        AlbumDetailFragment fragment = new AlbumDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, (Serializable) imageInfoList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mImageInfoList = (List<ImageInfo>) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    //状态，长按任意一个图片进入选择模式
    public boolean selectedState;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_album_detail, container, false);
        mAlbumGridView = (GridView) rootView.findViewById(R.id.gv_album);
        ImageLoaderWrapper loaderWrapper = ImageLoaderFactory.getLoader();
        mAlbumGridViewAdapter = new AlbumGridAdapter(mImageInfoList, loaderWrapper, mImageChooseView);
        mAlbumGridView.setAdapter(mAlbumGridViewAdapter);
        mAlbumGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //长按一次进入选择模式后，再按无效
                if (!selectedState) {
                    refreshSelectedImage(mImageInfoList.get(position));
                }
                selectedState = true;
                return true;
            }
        });

        mAlbumGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!selectedState) {
                    new PreviewDialog(getActivity(), mImageInfoList, position).show();
                } else {
                    refreshSelectedImage(mImageInfoList.get(position));
                }
            }
        });
        return rootView;
    }

    //由activity调用，因为选中列表在activity中创建维护，此处调用时为了把list中imageinfo选中状态清空，然后刷新列表
    public void unselectedAll(){
        for(ImageInfo i : mImageInfoList){
            i.setIsSelected(false);
        }
        selectedState = false;
        mAlbumGridViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ImageChooseView) {
            mImageChooseView = (ImageChooseView) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mImageChooseView = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ActivityCode.SELECT_REQUEST) {

            if (resultCode == ActivityCode.SELECT_NO_RESULT) {
                return;
            }

        }
    }

    /**
     * 刷新新选中图片的数据
     */
    private void refreshSelectedImage(ImageInfo newSelectedImage) {
        newSelectedImage.setIsSelected(!newSelectedImage.isSelected());//遍历更新选中的状态
        if (mImageChooseView != null) {
            ImageInfo destImageInfo = new ImageInfo(newSelectedImage);
            mImageChooseView.refreshSelectedCounter(destImageInfo);
        }
        mAlbumGridViewAdapter.notifyDataSetChanged();
    }
}
