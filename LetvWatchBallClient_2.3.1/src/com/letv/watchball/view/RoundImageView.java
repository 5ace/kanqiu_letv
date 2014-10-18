package com.letv.watchball.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.letv.cache.view.LetvImageView;
import com.letv.watchball.utils.LogInfo;

/**
 * 圆形控件
 * @author ljnalex
 *
 */
public class RoundImageView extends LetvImageView {
	
	private Paint paint;
	
	private Bitmap bitmapSrc;
	
	public RoundImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}
	
	private void init(Context context, AttributeSet attrs) {
		paint = new Paint();
		paint.setFilterBitmap(false);
		paint.setAntiAlias(true);
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		
		Drawable drawable = getDrawable();
		if (drawable != null) {
			bitmapSrc = ((BitmapDrawable)drawable).getBitmap();
		}
		
		if(bitmapSrc != null) {
			bitmapSrc = loadBitmap(bitmapSrc, getWidth());
			canvas.drawBitmap(getCircleBitmap(bitmapSrc), 0, 0, paint);
		} else {
			super.onDraw(canvas);
		}
	}

	/**
	 * 创建圆形位图
	 * @param bitmap
	 * @return
	 */
	public Bitmap getCircleBitmap(Bitmap bitmap) {
		// 根据原始图片的宽、高，创建一个新的画布
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getWidth(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		// 根据原来图片大小画一个矩形
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		
		// 画笔
		final Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(0xff424242);

		// 画出一个圆
		int radius = bitmap.getWidth() >> 1;
		canvas.drawCircle(radius, radius, radius, paint);
		
		// 取两层绘制交集,显示上层
		// PorterDuff.Mode的原理，参考：http://blog.csdn.net/starfeng11/article/details/7000284
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}
	
	/**
	 * 图片裁剪
	 * @param src
	 * @param w
	 * @return
	 */
	private Bitmap loadBitmap(Bitmap src, int w){
	    // 获得原始图片的宽高
	    int width = src.getWidth();
	    int height = src.getHeight();
	    
	    // 计算缩放比例
	    float scaleWidth = ((float) w) / width;
	    float scaleHeight = ((float) w) / height;
	    
	    // 取得想要缩放的matrix参数
	    Matrix matrix = new Matrix();
	    matrix.postScale(scaleWidth, scaleHeight);

	    // 得到新的图片
	    return Bitmap.createBitmap(src, 0, 0, width, height, matrix, true);
   }
}
