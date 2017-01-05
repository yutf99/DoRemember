package com.zxing.android.decoding;

import android.graphics.Bitmap;

import com.google.zxing.LuminanceSource;

public class BitmapLuminanceSource extends LuminanceSource
{

    private byte bitmapPixels[];

    public BitmapLuminanceSource(Bitmap bitmap)
    {
        super(bitmap.getWidth(), bitmap.getHeight());
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        // 首先，要取得该图片的像素数组内容
        int[] data = new int[width * height];

        bitmap.getPixels(data, 0, getWidth(), 0, 0, getWidth(), getHeight());

        this.bitmapPixels = new byte[data.length];

        // 将int数组转换为byte数组，也就是取像素值中蓝色值部分作为辨析内容
//        for (int y = 0; y < height; y++)
//        {
//            int offset = y * width;
//            for (int x = 0; x < width; x++)
//            {
//                int pixel = bitmapPixels[offset + x];
//                int r = (pixel >> 16) & 0xff;
//                int g = (pixel >> 8) & 0xff;
//                int b = pixel & 0xff;
//                if (r == g && g == b)
//                {
//                    // Image is already greyscale, so pick any channel.
//                    bitmapPixels[offset + x] = (byte) r;
//                } else
//                {
//                    // Calculate luminance cheaply, favoring green.
//                    bitmapPixels[offset + x] = (byte) ((r + g + g + b) >> 2);
//                }
//            }
//        }

//
        for (int i = 0; i < data.length; i++)
        {
            this.bitmapPixels[i] = (byte) data[i];
        }
    }

    @Override
    public byte[] getMatrix()
    {
        // 返回我们生成好的像素数据
        return bitmapPixels;
    }

    @Override
    public byte[] getRow(int y, byte[] row)
    {
        // 这里要得到指定行的像素数据
        System.arraycopy(bitmapPixels, y * getWidth(), row, 0, getWidth());
        return row;
    }
}