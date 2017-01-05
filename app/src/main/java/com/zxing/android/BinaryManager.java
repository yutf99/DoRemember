package com.zxing.android;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Binarizer;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.zxing.android.decoding.BitmapLuminanceSource;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * Created by ytf on 2017/1/4.
 */
public class BinaryManager
{

    private static BinaryManager instance;
    private Vector<BarcodeFormat> decodeFormats;
    private Map<DecodeHintType, Object> hints;
    private MultiFormatReader reader;

    private BinaryManager()
    {
        reader = new MultiFormatReader();
        decodeFormats = new Vector<BarcodeFormat>();
        hints = new HashMap<DecodeHintType, Object>();
        hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
        if (decodeFormats == null || decodeFormats.isEmpty())
        {
            decodeFormats = new Vector<BarcodeFormat>();

            // 这里设置可扫描的类型，我这里选择了都支持
            decodeFormats.add(BarcodeFormat.UPC_A);
            decodeFormats.add(BarcodeFormat.UPC_E);
            decodeFormats.add(BarcodeFormat.EAN_13);
            decodeFormats.add(BarcodeFormat.EAN_8);
            decodeFormats.add(BarcodeFormat.RSS_14);

            decodeFormats.add(BarcodeFormat.CODE_39);
            decodeFormats.add(BarcodeFormat.CODE_93);
            decodeFormats.add(BarcodeFormat.CODE_128);
            decodeFormats.add(BarcodeFormat.ITF);

            decodeFormats.add(BarcodeFormat.QR_CODE);
            decodeFormats.add(BarcodeFormat.DATA_MATRIX);
        }
        hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);
        reader.setHints(hints);
    }

    public static BinaryManager getInstance()
    {
        if(instance == null)
        {
            synchronized (BinaryManager.class)
            {
                if(instance == null)
                {
                    instance = new BinaryManager();
                }
            }
        }
        return instance;
    }


    /***
     * 生成一个二维码，默认为200x200
     * @param str
     * @return
     */
    public Bitmap encode(String str)
    {
        return encode(BarcodeFormat.QR_CODE, str, 200,200);
    }

    /***
     * 生成一个条形码或者二维码
     * @param format 二维码格式
     * @param str  存储的数据
     * @param bmpWidth 图形的宽度
     * @param bmpHeight 图形的高度
     * @return
     */
    public Bitmap encode(BarcodeFormat format, String str, int bmpWidth, int bmpHeight)
    {
        Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        BitMatrix bitMatrix = null;// 生成矩阵
        try
        {
            bitMatrix = new MultiFormatWriter().encode(str, format, bmpWidth, bmpHeight, hints);
            int w = bitMatrix.getWidth();
            int h = bitMatrix.getHeight();
            int[] pixels = new int[bmpWidth * bmpHeight];
            for (int y = 0; y < bmpHeight; y++)
            {
                for (int x = 0; x < bmpWidth; x++)
                {
                    if (bitMatrix.get(x, y))
                    {
                        pixels[y * bmpWidth + x] = 0xff000000;
                    }else
                    {
                        //此处如果不把它填充为白色，当拿到此图对象去解析的时候就会得到全是0 的位图数据，导致无法解析
                        pixels[y * bmpWidth + x] = 0xffffffff;
                    }
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(bmpWidth, bmpHeight, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, w, 0, 0, w, h);
            return bitmap;
        } catch (WriterException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /***
     * 解析二维码
     *
     * @param bmp
     * @return 返回一个Result
     */
    public Result decodeBitmap(Bitmap bmp)
    {
        try
        {
            Binarizer binarizer = new HybridBinarizer(new BitmapLuminanceSource(bmp));
            BinaryBitmap binaryBitmap = new BinaryBitmap(binarizer);
            return reader.decodeWithState(binaryBitmap);
        } catch (NotFoundException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /***
     * 从文件解析
     *
     * @param absPath
     * @return
     */
    public Result decodeFile(String absPath)
    {
        if (TextUtils.isEmpty(absPath))
        {
            return null;
        }
        File f = new File(absPath);
        if (!f.exists())
        {
            return null;
        }
        Bitmap bmp = BitmapFactory.decodeFile(absPath);
        return decodeBitmap(bmp);
    }
}
