package com.zlf.zlfviewutils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 自定义雪花
 */
public class SnowFlakeView extends View {
	/**
	 * 画笔
	 */
	private Paint mPaint;
	/**
	 * 雪花图片
	 */
	private Bitmap mBitmap;
	/**
	 * 雪花数量
	 */
	private static final int COUNT = 50;
	/**
	 * 雪花集合
	 */
	private List<SnowFlake> snowFlakes;
	private Handler mHandler = new Handler();
	/**
	 * 重绘的任务
	 */
	private Runnable runnable = new Runnable() {
		public void run() {
			invalidate();
			mHandler.postDelayed(runnable, 50);
		}
	};

	public SnowFlakeView(Context context) {
		this(context, null);
	}

	public SnowFlakeView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SnowFlakeView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	/**
	 * 初始化
	 */
	private void init() {
		setBackgroundColor(Color.BLACK);
		mPaint = new Paint();
		mPaint.setAntiAlias(true);// 抗锯齿
		mPaint.setDither(true);// 防抖动
		mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.snow);
		snowFlakes = new ArrayList<SnowFlake>();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		snowFlakes.clear();
		for (int i = 0; i < COUNT; i++) {
			snowFlakes.add(new SnowFlake(mBitmap.getHeight(), w, h));
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		for (SnowFlake snowFlake : snowFlakes) {
			// 当雪花的top超过自定义控件的高度时，重写设置雪花的参数
			if (snowFlake.y > getHeight()) {
				snowFlake.reset(mBitmap.getHeight(), getWidth());
			}
			// 只有当雪花的底部大于0时才画出雪花
			if (snowFlake.y - mBitmap.getHeight() * snowFlake.scale > 0) {
				mPaint.setAlpha(snowFlake.alpha);
				canvas.drawBitmap(getScaleBitmap(mBitmap, snowFlake.scale),
						snowFlake.x, snowFlake.y, mPaint);
			}
			// 雪花降落
			snowFlake.y += snowFlake.dy;
		}
		// post重绘任务
		mHandler.post(runnable);
	}

	/**
	 * 得到缩小的图片
	 * 
	 * @param bitmap
	 *            原图片
	 * @param scale
	 *            x,y方向的缩小比例
	 * @return
	 */
	private Bitmap getScaleBitmap(Bitmap bitmap, float scale) {
		Bitmap scaleBitmap = Bitmap.createBitmap(
				(int) (bitmap.getWidth() * scale),
				(int) (bitmap.getHeight() * scale), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(scaleBitmap);
		Matrix matrix = new Matrix();
		matrix.setScale(scale, scale);
		canvas.drawBitmap(bitmap, matrix, null);
		return scaleBitmap;
	}

	/**
	 * 雪花类
	 */
	private static class SnowFlake {
		int alpha;// 透明度
		int x;// x轴位置
		int y;// y轴位置
		int dy;// y轴下降像素
		float scale;// 缩小比例
		static Random random = new Random();

		public SnowFlake(int bitmapHeight, int width, int height) {
			init(bitmapHeight, width, height, true);
		}

		public void reset(int bitmapHeight, int width) {
			init(bitmapHeight, width, 0, false);
		}

		private void init(int bitmapHeight, int width, int height,
				boolean isFirst) {
			alpha = random.nextInt(156) + 100;// [100,255)
			x = random.nextInt(width);
			if (isFirst) {
				y = -random.nextInt(height);
			} else {
				y = -(int) (bitmapHeight * scale);// 使得重置时雪花的底部位置为0
			}
			dy = random.nextInt(4) * 3 + 5;
			scale = random.nextFloat() / 2 + 0.2f;// [0.2-0.7)
		}
	}
}
