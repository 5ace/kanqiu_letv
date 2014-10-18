package com.letv.watchball.view;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class MyProgressBar extends View{

	private float x = 0;
	private float y = 0;
	private static final float offset = 0.75f; 
	private ProgressBarCallBack progressBarCallBack;

	public MyProgressBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	
	public MyProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	public MyProgressBar(Context context) {
		super(context);
	}

    @Override
    protected void onDraw(Canvas canvas) {

          // TODO Auto-generated method stub
          super.onDraw(canvas);

          // 首先定义一个paint
          Paint paint = new Paint();

          paint.setColor(Color.WHITE);
          
          paint.setStyle(Paint.Style.STROKE);  //设置空心
          
          /**
           * 画最外层的大圆环
  		   */
  		  int centre = getWidth()/2; //获取圆心的x坐标
  		  int radius = (int) (centre - 7); //圆环的半
          
          paint.setStrokeWidth(5); //设置圆环的宽度
  	
          paint.setAntiAlias(true);  //消除锯齿 

          RectF rf1 = new RectF(centre - radius, centre - radius, centre
  				+ radius, centre + radius);
          canvas.drawArc(rf1, x-90, y, false, paint);
          super.onDraw(canvas);
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
    	super.onVisibilityChanged(changedView, visibility);
    	if(visibility == View.VISIBLE){
    		progressBarCallBack.start();
    	}else{
    		progressBarCallBack.stop();
    	}
    	
    }
    
    public void updatePosition(){
    	if(y<0)
    		return;
    	y += offset;
    	if(y>=360)
    		y = 0;
    	postInvalidate();
    }
    
    public int getPositon(){
    	return (int)y;

    }
    
    public void resetProgressBar(){
    	y = 0;
    	updatePosition();
    }
    
    public interface ProgressBarCallBack{
    	void start();
    	void stop();
    }
    
    public void setProgressBarCallBack(ProgressBarCallBack progressBarCallBack){
    	this.progressBarCallBack = progressBarCallBack;
    }
}
