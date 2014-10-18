package com.letv.watchball.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

public class LetvPlayGestureLayout extends RelativeLayout implements OnGestureListener {

	/**
	 * 无事件
	 * */
	public static final int NONE = 0x00;
	/**
	 * 双指下划
	 * */
	public static final int DOUBLE_FINGERS_DOWN = 0x10;
	/**
	 * 双指上划
	 * */
	public static final int DOUBLE_FINGERS_UP = 0x11;
	/**
	 * 横划完成
	 * */
	public static final int LANDSCAPE_SCROLL_FINISH = 0x12;

	/**
	 * 手势回调
	 * */
	private LetvPlayGestureCallBack letvPlayGestureCallBack;

	/**
	 * 当前手势事件
	 * */
	public int event;

	/**
	 * 手势探测器
	 * */
	private GestureDetector mGestureDetector = null;

	/**
	 * Y轴变化值，在一次事件完成后，会被归0
	 * */
	private float offsetY = 0;

	/**
	 * X轴变化值，在一次事件完成后，会被归0
	 * */
	private float offsetX = 0;

	/**
	 * 竖划斜率限制
	 * */
	private float portraitLimitSlope = 4; // 上下滑动手势的限制斜率

	/**
	 * 横划斜率限制
	 * */
	private float landscapeLimitSlope = 1f / 4f; // 上下滑动手势的限制斜率

	/**
	 * 方向锁
	 * */
	private int directionalLock = 0; // 0 无方向，1纵向，2横向

	/**
	 * 双指上划伐值
	 * */
	private float doubleFingersUpCuttingValue = 0.3f;

	/**
	 * 双指下划伐值
	 * */
	private float doubleFingersDownCuttingValue = 0.3f;

	/**
	 * 屏幕两边上下滑动区域伐值
	 * */
	private float bothSidesCuttingValue = 0.2f;

	/**
	 * 右半屏长度
	 * */
	private float rightProgerss = 0;

	/**
	 * 左半屏长度
	 * */
	private float leftProgress = 0;

	/**
	 * 横划进度记录
	 * */
	private float landscapeProgress = 0;

	public LetvPlayGestureLayout(Context activity, AttributeSet attrs) {
		super(activity, attrs);
		init();
	}

	public LetvPlayGestureLayout(Context activity) {
		super(activity);
		init();
	}

	public LetvPlayGestureLayout(Context activity, AttributeSet attrs, int defStyle) {
		super(activity, attrs, defStyle);
		init();
	}

	/**
	 * 手势类内部初始化
	 * */
	protected void init() {
		mGestureDetector = new GestureDetector(this);
		mGestureDetector.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {

			/**
			 * 单击
			 * */
			@Override
			public boolean onSingleTapConfirmed(MotionEvent e) {
				if (letvPlayGestureCallBack != null) {
					letvPlayGestureCallBack.onSingleTapUp();
				}
				return true;
			}

			/**
			 * 双击
			 * */
			@Override
			public boolean onDoubleTapEvent(MotionEvent e) {
				return false;
			}

			/**
			 * 双击
			 * */
			@Override
			public boolean onDoubleTap(MotionEvent e) {
				return true;
			}
		});
	}

	/**
	 * 初始化页面空间，请再控件初始化后在调用
	 * */
	public void initializeData(float rightProgerss, float leftProgress) {
		this.rightProgerss = rightProgerss;
		this.leftProgress = leftProgress;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (MotionEvent.ACTION_UP == event.getAction()) {
			// 释放方向锁
			switch (this.event) {
			case DOUBLE_FINGERS_UP:
				if (letvPlayGestureCallBack != null) {
					letvPlayGestureCallBack.onDoubleFingersUp();
				}
				break;
			case DOUBLE_FINGERS_DOWN:
				if (letvPlayGestureCallBack != null) {
					letvPlayGestureCallBack.onDoubleFingersDown();
				}
				break;
			case LANDSCAPE_SCROLL_FINISH:
				if (letvPlayGestureCallBack != null) {
					letvPlayGestureCallBack.onLandscapeScrollFinish(landscapeProgress);
				}
				break;
			default:
				break;
			}

			this.directionalLock = 0;
			this.offsetY = 0;
			this.offsetX = 0;
			this.landscapeProgress = 0;
			this.event = NONE;

			if (letvPlayGestureCallBack != null) {
				letvPlayGestureCallBack.onTouchEventUp();
			}
		}
		return mGestureDetector.onTouchEvent(event);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		leftProgress = 0 ;
		rightProgerss = 0 ;
		if (letvPlayGestureCallBack != null) {
			letvPlayGestureCallBack.onDown();
		}
		return true;
	}

	@Override
	public void onShowPress(MotionEvent e) {

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	/**
	 * 完成手势走的调节声音和调节亮度的功能
	 * */
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		int s = e2.getPointerCount();
		if (s == 2) {// 双指滑动
			offsetY += distanceY;
			if (offsetY > 0) {
				if (offsetY > doubleFingersUpCuttingValue * getHeight()) {// 向下
					event = DOUBLE_FINGERS_UP;
				} else {// 距离不够，或者划回来就取消事件
					event = NONE;
				}
			} else {
				if (offsetY < -doubleFingersDownCuttingValue * getHeight()) {// 向上
					event = DOUBLE_FINGERS_DOWN;
				} else {// 距离不够，或者划回来就取消事件
					event = NONE;
				}
			}
		} else if (s == 1) {// 单指滑动
			if (Math.abs(distanceY) > portraitLimitSlope * Math.abs(distanceX) && directionalLock != 2) {// 斜率判断，竖划
				directionalLock = 1;
				if (e1.getX() > (1 - bothSidesCuttingValue) * getWidth()) {// 右半屏幕上下滑动
					rightProgerss += distanceY / getHeight();
//					if (rightProgerss > 1) {
//						rightProgerss = 1;
//					}

//					if (rightProgerss < 0) {
//						rightProgerss = 0;
//					}
					if (letvPlayGestureCallBack != null) {
						letvPlayGestureCallBack.onRightScroll(rightProgerss);
					}
				} else if (e1.getX() < bothSidesCuttingValue * getWidth()) {// 左半屏幕上下滑动
					leftProgress += distanceY / getHeight();
//					if (leftProgress > 1) {
//						leftProgress = 1;
//					}

//					if (leftProgress < 0) {
//						leftProgress = 0;
//					}
					if (letvPlayGestureCallBack != null) {
						letvPlayGestureCallBack.onLeftScroll(leftProgress);
					}
				}
			} else if (Math.abs(distanceY) < landscapeLimitSlope * Math.abs(distanceX) && directionalLock != 1) {// 斜率判断，横划
				directionalLock = 2;
				offsetX -= distanceX;

				landscapeProgress = offsetX / getWidth();

				if (letvPlayGestureCallBack != null) {
					letvPlayGestureCallBack.onLandscapeScroll(landscapeProgress);
				}
				event = LANDSCAPE_SCROLL_FINISH;
			}
		}

		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		if (letvPlayGestureCallBack != null) {
			letvPlayGestureCallBack.onLongPress();
		}
	}

	public LetvPlayGestureCallBack getLetvPlayGestureCallBack() {
		return letvPlayGestureCallBack;
	}

	public void setLetvPlayGestureCallBack(LetvPlayGestureCallBack letvPlayGestureCallBack) {
		this.letvPlayGestureCallBack = letvPlayGestureCallBack;
	}

	public interface LetvPlayGestureCallBack {
		/**
		 * 双指下划
		 * */
		public void onDown();
		/**
		 * 双指下划
		 * */
		public void onDoubleFingersDown();

		/**
		 * 双指上划
		 * */
		public void onDoubleFingersUp();

		/**
		 * 单击
		 * */
		public void onSingleTapUp();

		/**
		 * 双击
		 * */
		public void onDoubleTap();

		/**
		 * 右边上下滑动 变化总量
		 * */
		public void onRightScroll(float incremental);

		/**
		 * 左边上下滑动 变化总量
		 * */
		public void onLeftScroll(float incremental);

		/**
		 * 横向滑动 变化总量
		 * */
		public void onLandscapeScroll(float incremental);

		/**
		 * 横向滑动 变化总量
		 * */
		public void onLandscapeScrollFinish(float incremental);

		/**
		 * up事件，完成所有操作了
		 * */
		public void onTouchEventUp();

		/**
		 * 长按
		 * */
		public void onLongPress();
	}
}
