package com.white.hot.doremember.function.anim.imgscan_mode.album.presenter;

import android.content.Context;
import android.support.v4.app.LoaderManager;

import com.white.hot.doremember.function.anim.imgscan_mode.album.entity.*;
import com.white.hot.doremember.function.anim.imgscan_mode.album.scanner.ImageScannerModel;
import com.white.hot.doremember.function.anim.imgscan_mode.album.scanner.ImageScannerModelImpl;

public class ImageScannerPresenterImpl implements ImageScannerPresenter {

    private ImageScannerModel mScannerModel;
    private AlbumView mAlbumView;

    public ImageScannerPresenterImpl(AlbumView albumView) {
        mScannerModel = new ImageScannerModelImpl();
        mAlbumView = albumView;
    }

    @Override
    public void startScanImage(final Context context, LoaderManager loaderManager) {
        mScannerModel.startScanImage(context, loaderManager, new ImageScannerModel.OnScanImageFinish() {


            @Override
            public void onFinish(com.white.hot.doremember.function.anim.imgscan_mode.album.entity.ImageScanResult imageScanResult) {
                if (mAlbumView != null) {
                    AlbumViewData albumData = mScannerModel.archiveAlbumInfo(context, imageScanResult);
                    mAlbumView.refreshAlbumData(albumData);
                }
            }

        });
    }
}
