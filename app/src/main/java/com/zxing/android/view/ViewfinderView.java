/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zxing.android.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;

import com.google.zxing.ResultPoint;
import com.zxing.android.camera.CameraManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * This view is overlaid on top of the camera preview. It adds the viewfinder
 * rectangle and partial transparency outside it, as well as the laser scanner
 * animation and result points.
 * 
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class ViewfinderView extends View {
	/**
	 * 刷新界面的时间
	 */
	private static final long ANIMATION_DELAY = 30L;
	private static final int OPAQUE = 0xFF;
	private static final int MAX_RESULT_POINTS = 5;
	/**
	 * 四个绿色边角对应的长度
	 */
	private int ScreenRate;

	/**
	 * 四个绿色边角对应的宽度
	 */
	private static final int CORNER_WIDTH = 10;
	/**
	 * 扫描框中的中间线的宽度
	 */
	private static final int MIDDLE_LINE_WIDTH = 6;

	/**
	 * 扫描框中的中间线的与扫描框左右的间隙
	 */
	private static final int MIDDLE_LINE_PADDING = 5;

	/**
	 * 中间那条线每次刷新移动的距离
	 */
	private static final int SPEEN_DISTANCE = 5;

	/**
	 * 手机的屏幕密度
	 */
	private static float density;
	/**
	 * 字体大小
	 */
	private static final int TEXT_SIZE = 16;
	/**
	 * 字体距离扫描框下面的距离
	 */
	private static final int TEXT_PADDING_TOP = 30;

	/**
	 * 画笔对象的引用
	 */
	private Paint paint;

	/**
	 * 中间滑动线的最顶端位置
	 */
	private int slideTop;

	/**
	 * 中间滑动线的最底端位置
	 */
	private int slideBottom;

	/**
	 * 将扫描的二维码拍下来，这里没有这个功能，暂时不考虑
	 */
	private Bitmap resultBitmap;
	private final int maskColor;
	private final int resultColor;

	private final int resultPointColor;
	private Collection<ResultPoint> possibleResultPoints;
	private Collection<ResultPoint> lastPossibleResultPoints;
	private CameraManager cameraManager;
	boolean isFirst;

	public ViewfinderView(Context context) {
		this(context, null);
	}

	// This constructor is used when the class is built from an XML resource.
	public ViewfinderView(Context context, AttributeSet attrs) {
		super(context, attrs);

		// Initialize these once for performance rather than calling them every
		// time in onDraw().
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		maskColor = 0x60000000;
		resultColor = 0xb0000000;
		resultPointColor = 0xc0ffff00;
		possibleResultPoints = new ArrayList<ResultPoint>(5);
		lastPossibleResultPoints = null;

		density = context.getResources().getDisplayMetrics().density;
		// 将像素转换成dp
		ScreenRate = (int) (20 * density);
	}

	public void setCameraManager(CameraManager cameraManager) {
		this.cameraManager = cameraManager;
	}

	@Override
	public void onDraw(Canvas canvas) {
		// 中间的扫描框，你要修改扫描框的大小，去CameraManager里面修改
		if(cameraManager == null)
			return;
		Rect frame = cameraManager.getFramingRect();
		if (frame == null) {
			return;
		}

		// 获取屏幕的宽和高
		int width = canvas.getWidth();
		int height = canvas.getHeight();

		// 初始化中间线滑动的最上边和最下边
		if (!isFirst) {
			isFirst = true;
			slideTop = frame.top;
			slideBottom = frame.bottom;

			rectShadow = new RectF(frame.left, frame.top - (frame.bottom-frame.top)/2, frame.right, frame.top);
			//渐变下面的线
			gradientLinePaint.setColor(0xff0ec3ff);
			gradientLinePaint.setStyle(Paint.Style.STROKE);
			gradientLinePaint.setStrokeWidth(2);
		}


		paint.setColor(resultBitmap != null ? resultColor : maskColor);

		// 画出扫描框外面的阴影部分，共四个部分，扫描框的上面到屏幕上面，扫描框的下面到屏幕下面
		// 扫描框的左边面到屏幕左边，扫描框的右边到屏幕右边
		canvas.drawRect(0, 0, width, frame.top, paint);
		canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
		canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, paint);
		canvas.drawRect(0, frame.bottom + 1, width, height, paint);

		if (resultBitmap != null) {
			// Draw the opaque result bitmap over the scanning rectangle
			paint.setAlpha(OPAQUE);
			canvas.drawBitmap(resultBitmap, frame.left, frame.top, paint);
		} else {

			// 画线框
			paint.setColor(0x55ffffff);
			paint.setStrokeWidth(1);
			paint.setStyle(Paint.Style.STROKE);
			canvas.drawRect(frame.left, frame.top, frame.right + 1, frame.bottom - 1, paint);

			// 这里画取景框四个角落的夹角
			paint.setStyle(Paint.Style.FILL);
			paint.setColor(0xff0ec3ff);
			paint.setAntiAlias(true);
			canvas.drawRect(frame.left - CORNER_WIDTH + 2, frame.top - CORNER_WIDTH + 2, frame.left + ScreenRate
					- CORNER_WIDTH + 2, frame.top + 2, paint);
			canvas.drawRect(frame.left - CORNER_WIDTH + 2, frame.top - CORNER_WIDTH + 2, frame.left + 2, frame.top
					+ ScreenRate - CORNER_WIDTH + 2, paint);
			canvas.drawRect(frame.right - ScreenRate + CORNER_WIDTH - 2, frame.top - CORNER_WIDTH + 2, frame.right
					+ CORNER_WIDTH - 2, frame.top + 2, paint);
			canvas.drawRect(frame.right - 2, frame.top - CORNER_WIDTH + 2, frame.right + CORNER_WIDTH - 2, frame.top
					+ ScreenRate - CORNER_WIDTH + 2, paint);

			canvas.drawRect(frame.left - CORNER_WIDTH + 2, frame.bottom - 2,
					frame.left + ScreenRate - CORNER_WIDTH + 2, frame.bottom + CORNER_WIDTH - 2, paint);
			canvas.drawRect(frame.left - CORNER_WIDTH + 2, frame.bottom - ScreenRate + CORNER_WIDTH - 2,
					frame.left + 2, frame.bottom + CORNER_WIDTH - 2, paint);
			canvas.drawRect(frame.right - ScreenRate + CORNER_WIDTH - 2, frame.bottom - 2, frame.right + CORNER_WIDTH
					- 2, frame.bottom + CORNER_WIDTH - 2, paint);
			canvas.drawRect(frame.right - 2, frame.bottom - ScreenRate + CORNER_WIDTH - 2, frame.right + CORNER_WIDTH
					- 2, frame.bottom + CORNER_WIDTH - 2, paint);

			// 绘制中间扫描特效（渐变）,每次刷新界面，中间的线往下移动SPEEN_DISTANCE
			rectShadow.top += SPEEN_DISTANCE;
			if(rectShadow.top >= frame.bottom)
			{
				rectShadow.left = frame.left;
				rectShadow.top = frame.top - (frame.bottom-frame.top)/2;
				rectShadow.right = frame.right;
				rectShadow.bottom = frame.top;
			}else
			{
				rectShadow.bottom += SPEEN_DISTANCE;
			}

			linearGradient = new LinearGradient(rectShadow.right / 2,rectShadow.bottom,
					rectShadow.right / 2,rectShadow.top, 0x661976D2, 0x00bbbbbb, Shader.TileMode.CLAMP);
			gradientPaint.setShader(linearGradient);
			gradientPaint.setStyle(Paint.Style.FILL);
			int sc = canvas.saveLayer(frame.left, frame.top, frame.right, frame.bottom, null,
					Canvas.MATRIX_SAVE_FLAG | Canvas.CLIP_SAVE_FLAG
							| Canvas.HAS_ALPHA_LAYER_SAVE_FLAG
							| Canvas.FULL_COLOR_LAYER_SAVE_FLAG
							| Canvas.CLIP_TO_LAYER_SAVE_FLAG);

			gradientPaint.setXfermode(pd);
			gradientLinePaint.setXfermode(pd);
			canvas.drawRect(rectShadow, gradientPaint);
			canvas.drawLine(rectShadow.left, rectShadow.bottom, rectShadow.right, rectShadow.bottom, gradientLinePaint);
			gradientPaint.setXfermode(null);
			gradientLinePaint.setXfermode(null);
			canvas.restoreToCount(sc);

//			Rect lineRect = new Rect();
//			lineRect.left = frame.left;
//			lineRect.right = frame.right;
//			lineRect.top = slideTop;
//			lineRect.bottom = slideTop + 18;
//			canvas.drawBitmap(((BitmapDrawable) (getResources().getDrawable(R.drawable.qr_scan_line))).getBitmap(),
//					null, lineRect, paint);


			Collection<ResultPoint> currentPossible = possibleResultPoints;
			Collection<ResultPoint> currentLast = lastPossibleResultPoints;
			if (currentPossible.isEmpty()) {
				lastPossibleResultPoints = null;
			} else {
				possibleResultPoints = new HashSet<ResultPoint>(5);
				lastPossibleResultPoints = currentPossible;
				paint.setAlpha(OPAQUE);
				paint.setColor(resultPointColor);
				for (ResultPoint point : currentPossible) {
					canvas.drawCircle(frame.left + point.getX(), frame.top + point.getY(), 6.0f, paint);
				}
			}
			if (currentLast != null) {
				paint.setAlpha(OPAQUE / 2);
				paint.setColor(resultPointColor);
				for (ResultPoint point : currentLast) {
					canvas.drawCircle(frame.left + point.getX(), frame.top + point.getY(), 3.0f, paint);
				}
			}

			// 只刷新扫描框的内容，其他地方不刷新
			postInvalidateDelayed(ANIMATION_DELAY, frame.left, frame.top, frame.right, frame.bottom);

		}
	}

	private LinearGradient linearGradient;
	//蓝色渐变
	private Paint gradientPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	//与蓝色渐变相连的实线画笔
	private Paint gradientLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	//渐变所占的矩形范围
	private RectF rectShadow;
	//用于图形叠加
	private PorterDuffXfermode pd = new PorterDuffXfermode(PorterDuff.Mode.DST_OVER);

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
	}

	public void drawViewfinder() {
		Bitmap resultBitmap = this.resultBitmap;
		this.resultBitmap = null;
		if (resultBitmap != null) {
			resultBitmap.recycle();
		}
		invalidate();
	}

	/**
	 * Draw a bitmap with the result points highlighted instead of the live
	 * scanning display.
	 * 
	 * @param barcode
	 *            An image of the decoded barcode.
	 */
	public void drawResultBitmap(Bitmap barcode) {
		resultBitmap = barcode;
		invalidate();
	}

	public void addPossibleResultPoint(ResultPoint point) {
		possibleResultPoints.add(point);
	}
}
