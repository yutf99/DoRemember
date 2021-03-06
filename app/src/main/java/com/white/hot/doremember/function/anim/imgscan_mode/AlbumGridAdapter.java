package com.white.hot.doremember.function.anim.imgscan_mode;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.white.hot.doremember.R;
import com.white.hot.doremember.function.anim.imgscan_mode.album.entity.ImageChooseView;
import com.white.hot.doremember.function.anim.imgscan_mode.album.entity.ImageInfo;
import com.white.hot.doremember.function.anim.imgscan_mode.album.imageloader.ImageLoaderWrapper;
import com.white.hot.doremember.utils.UIHelper;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.List;


/**
 * 相册视图适配器
 * <p>
 * Created by Clock on 2016/1/16.
 */
public class AlbumGridAdapter extends BaseAdapter {

    private List<ImageInfo> mImageInfoList;
    private ImageLoaderWrapper mImageLoaderWrapper;
    private View.OnClickListener mImageItemClickListener;
    private CompoundButton.OnCheckedChangeListener mImageOnSelectedListener;
    private ImageChooseView mImageChooseView;

    public AlbumGridAdapter(List<ImageInfo> imageInfoList, ImageLoaderWrapper imageLoaderWrapper,
                            ImageChooseView imageChooseView) {
        this.mImageInfoList = imageInfoList;
        this.mImageLoaderWrapper = imageLoaderWrapper;
        this.mImageChooseView = imageChooseView;
    }

    @Override
    public int getCount() {
        if (mImageInfoList == null) {
            return 0;
        }
        return mImageInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return mImageInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AlbumViewHolder holder = null;
        if (convertView == null) {
            holder = new AlbumViewHolder();
            convertView = View.inflate(parent.getContext(), R.layout.album_grid_item, null);

            int gridItemSpacing = (int) UIHelper.dp2px(parent.getContext(), 2);
            int gridEdgeLength = (UIHelper.getScreenWidth((Activity) parent.getContext()) - gridItemSpacing * 2) / 3;
            AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(gridEdgeLength, gridEdgeLength);
            convertView.setLayoutParams(layoutParams);
            holder.albumItem = (ImageView) convertView.findViewById(R.id.iv_album_item);
            holder.imageSelectedCheckBox = (CheckBox) convertView.findViewById(R.id.ckb_image_select);
            convertView.setTag(holder);
        } else {
            holder = (AlbumViewHolder) convertView.getTag();
            resetConvertView(holder);
        }

        ImageInfo imageInfo = mImageInfoList.get(position);
        ImageLoaderWrapper.DisplayOption displayOption = new ImageLoaderWrapper.DisplayOption();
        displayOption.loadingResId = R.drawable.default_bg;
        displayOption.loadErrorResId = R.drawable.default_bg;
        mImageLoaderWrapper.displayImage(holder.albumItem, imageInfo.getImageFile(), displayOption);
        holder.imageSelectedCheckBox.setChecked(imageInfo.isSelected());
//        if (mImageOnSelectedListener == null) {
//            mImageOnSelectedListener = new CompoundButton.OnCheckedChangeListener() {
//
//                @Override
//                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//
//                    ImageInfo imageInfo = (ImageInfo) buttonView.getTag();
//                    imageInfo.setIsSelected(isChecked);
//
//                    if (mImageChooseView != null) {
//                        mImageChooseView.refreshSelectedCounter(imageInfo);
//                    }
//                }
//            };
//        }
//        holder.imageSelectedCheckBox.setTag(imageInfo);
//        holder.imageSelectedCheckBox.setOnCheckedChangeListener(mImageOnSelectedListener);//监听图片是否被选中的状态

//        if (mImageItemClickListener == null) {
//            mImageItemClickListener = new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    ImageInfo imageInfo = (ImageInfo) v.getTag();
//                    if (mOnClickPreviewImageListener != null) {
//                        mOnClickPreviewImageListener.onClickPreview(imageInfo);
//                    }
//                }
//            };
//        }

        holder.albumItem.setTag(imageInfo);
//        holder.albumItem.setOnClickListener(mImageItemClickListener);

        return convertView;
    }

    /**
     * 重置缓存视图的初始状态
     *
     * @param viewHolder
     */
    private void resetConvertView(AlbumViewHolder viewHolder) {
        viewHolder.imageSelectedCheckBox.setOnCheckedChangeListener(null);//先取消选择状态的监听
        viewHolder.imageSelectedCheckBox.setChecked(false);
    }

    private static class AlbumViewHolder {
        /**
         * 显示图片的位置
         */
        ImageView albumItem;
        /**
         * 图片选择按钮
         */
        CheckBox imageSelectedCheckBox;
    }
}
