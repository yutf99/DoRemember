package com.white.hot.doremember.function.snapshot;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.white.hot.doremember.R;
import com.white.hot.doremember.adapter.DefaultAdapter;
import com.white.hot.doremember.base.BaseActivity;
import com.white.hot.doremember.holder.BaseViewHolder;
import com.white.hot.doremember.utils.FileUtils;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

@ContentView(R.layout.activity_screencature)
public class ScreenCaptureImageActivity extends BaseActivity
{

    @ViewInject(R.id.startButton)
    Button startButton;
    @ViewInject(R.id.stopButton)
    Button stopButton;
    @ViewInject(R.id.gv_screen_snap)
    GridView gvScreenSnap;
    @ViewInject(R.id.captrue)
    private Button btnCaptrue;

    private static final int REQUEST_CODE = 100;
    private static String STORE_DIRECTORY;
    private static int IMAGES_PRODUCED;
    private static final String SCREENCAP_NAME = "screencap";
    private static final int VIRTUAL_DISPLAY_FLAGS = DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY
            | DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC;
    private static MediaProjection sMediaProjection;


    private MediaProjectionManager mProjectionManager;
    private ImageReader mImageReader;
    private Handler mHandler;
    private Display mDisplay;
    private VirtualDisplay mVirtualDisplay;
    private int mDensity;
    private int mWidth;
    private int mHeight;
    private int mRotation;
    private OrientationChangeCallback mOrientationChangeCallback;

    //截完屏显示预览
    private List<String> imgPath = new ArrayList<>();
    private ImageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        initActionBar();
        btnCaptrue.setEnabled(false);
        mProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);

        new Thread()
        {
            @Override
            public void run()
            {
                Looper.prepare();
                mHandler = new Handler();
                Looper.loop();
            }
        }.start();
    }

    private void initActionBar() {
        actionBar.setActionBarBackgroundColor(getResources().getColor(R.color.BlueGrade4));
        actionBar.setVisibility(View.VISIBLE);
        actionBar.setTitleText("截屏", Color.WHITE);
        actionBar.showLeftBack(R.drawable.back);
    }

    @Event(value = {R.id.startButton, R.id.stopButton, R.id.captrue})
    private void click(View v)
    {
        switch (v.getId())
        {
            case R.id.startButton:
                startProjection();
                break;
            case R.id.stopButton:
                stopProjection();
                break;
            case R.id.captrue:
                captrueScreen();
                break;
        }
    }

    private void startProjection()
    {
        startActivityForResult(mProjectionManager.createScreenCaptureIntent(), REQUEST_CODE);
    }

    private void stopProjection()
    {
        mHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                if (sMediaProjection != null)
                {
                    sMediaProjection.stop();
                }
            }
        });
    }

    private class ImageAvailableListener implements ImageReader.OnImageAvailableListener
    {
        @Override
        public void onImageAvailable(ImageReader reader)
        {
            captrueScreen();
        }
    }

    private void captrueScreen()
    {
        Image image = null;
        FileOutputStream fos = null;
        Bitmap bitmap = null;

        try
        {
            image = mImageReader.acquireLatestImage();
            if (image != null)
            {
                Image.Plane[] planes = image.getPlanes();
                ByteBuffer buffer = planes[0].getBuffer();
                int pixelStride = planes[0].getPixelStride();
                int rowStride = planes[0].getRowStride();
                int rowPadding = rowStride - pixelStride * mWidth;

                bitmap = Bitmap.createBitmap(mWidth + rowPadding / pixelStride, mHeight, Bitmap.Config.ARGB_8888);
                bitmap.copyPixelsFromBuffer(buffer);

                String path = STORE_DIRECTORY + "/myscreen_" + IMAGES_PRODUCED + ".png";
                fos = new FileOutputStream(path);
                bitmap.compress(CompressFormat.JPEG, 100, fos);

                imgPath.add(path);
                if(adapter == null)
                {
                    adapter = new ImageAdapter(ScreenCaptureImageActivity.this,imgPath);
                    gvScreenSnap.setAdapter(adapter);
                }else
                {
                    adapter.notifyDataSetChanged();
                }

                IMAGES_PRODUCED++;
            }

        } catch (Exception e)
        {
            e.printStackTrace();
        } finally
        {
            if (fos != null)
            {
                try
                {
                    fos.close();
                } catch (IOException ioe)
                {
                    ioe.printStackTrace();
                }
            }

            if (bitmap != null)
            {
                bitmap.recycle();
            }

            if (image != null)
            {
                image.close();
            }
        }
    }

    class ImageAdapter extends DefaultAdapter<String>
    {

        public ImageAdapter(Context context, List<String> datas)
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

            @ViewInject(R.id.iv)
            private ImageView iv;

            @Override
            protected View getViewLayout()
            {
                return View.inflate(ScreenCaptureImageActivity.this, R.layout.item_snap_preview, null);
            }

            @Override
            protected void refreshView(int position)
            {
                displayImageLoaderImg(iv, data);
            }
        }
    }

    private class OrientationChangeCallback extends OrientationEventListener
    {
        public OrientationChangeCallback(Context context)
        {
            super(context);
        }

        @Override
        public void onOrientationChanged(int orientation)
        {
            synchronized (this)
            {
                final int rotation = mDisplay.getRotation();
                if (rotation != mRotation)
                {
                    mRotation = rotation;
                    try
                    {
                        // clean up
                        if (mVirtualDisplay != null) mVirtualDisplay.release();
                        if (mImageReader != null)
                            mImageReader.setOnImageAvailableListener(null, null);

                        // re-create virtual display depending on device width / height
                        createVirtualDisplay();
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private class MediaProjectionStopCallback extends MediaProjection.Callback
    {
        @Override
        public void onStop()
        {
            mHandler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    if (mVirtualDisplay != null) mVirtualDisplay.release();
                    if (mImageReader != null) mImageReader.setOnImageAvailableListener(null, null);
                    if (mOrientationChangeCallback != null) mOrientationChangeCallback.disable();
                    sMediaProjection.unregisterCallback(MediaProjectionStopCallback.this);
                }
            });
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_CODE)
        {
            sMediaProjection = mProjectionManager.getMediaProjection(resultCode, data);

            if (sMediaProjection != null)
            {
                STORE_DIRECTORY = FileUtils.createPathBasedOnApp("ScreenSnap");
                DisplayMetrics metrics = getResources().getDisplayMetrics();
                mDensity = metrics.densityDpi;
                mDisplay = getWindowManager().getDefaultDisplay();

                createVirtualDisplay();

                mOrientationChangeCallback = new OrientationChangeCallback(this);
                if (mOrientationChangeCallback.canDetectOrientation())
                {
                    mOrientationChangeCallback.enable();
                }

                sMediaProjection.registerCallback(new MediaProjectionStopCallback(), mHandler);
            }
        }
    }

    private void createVirtualDisplay()
    {
        Point size = new Point();
        mDisplay.getSize(size);
        mWidth = size.x;
        mHeight = size.y;

        mImageReader = ImageReader.newInstance(mWidth, mHeight, PixelFormat.RGBA_8888, 2);
        mVirtualDisplay = sMediaProjection.createVirtualDisplay(SCREENCAP_NAME, mWidth, mHeight, mDensity, VIRTUAL_DISPLAY_FLAGS, mImageReader.getSurface(), null, mHandler);
        btnCaptrue.setEnabled(true);
        //注册此接口会自动截
//        mImageReader.setOnImageAvailableListener(new ImageAvailableListener(), mHandler);
    }

    @Override
    public void finish()
    {
        stopProjection();
        if(!TextUtils.isEmpty(STORE_DIRECTORY))
        {
            FileUtils.deleteDirectory(STORE_DIRECTORY);
        }
        super.finish();
    }
}