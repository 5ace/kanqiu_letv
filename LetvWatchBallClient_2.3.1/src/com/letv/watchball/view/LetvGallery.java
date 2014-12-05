package com.letv.watchball.view;

import java.util.ArrayList;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.ListView;

public class LetvGallery extends Gallery {

	private static final int MSG_GALLERY_IAMGE_MOVE = 1;

	private boolean isRight = true;

	private int delayMillis = 5000;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_GALLERY_IAMGE_MOVE:
				if (isRight) {
					move2Right();
				} else {
					move2Left();
				}
				sendEmptyMessageDelayed(MSG_GALLERY_IAMGE_MOVE, delayMillis);
				break;
			default:
				break;
			}
		}
	};

	private float gTouchStartX;
	private float gTouchStartY;
	public static boolean isFling = false;// 当为false时表示Gallery只可以一次滑动一张

	private ArrayList<OnItemSelectedListener> listeners = new ArrayList<AdapterView.OnItemSelectedListener>();

	public LetvGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOnItemSelectedListener(itemSelectedListener);
	}

	public void startMove(boolean isRight, int delayMillis) {
		this.isRight = isRight;
		this.delayMillis = delayMillis;
		mHandler.removeMessages(MSG_GALLERY_IAMGE_MOVE);
		mHandler.sendEmptyMessageDelayed(MSG_GALLERY_IAMGE_MOVE,
				this.delayMillis);
	}

	public void stopMove() {
		mHandler.removeMessages(MSG_GALLERY_IAMGE_MOVE);
	}

	private void move2Right() {
		try {
			onScroll(null, null, 1, 0); // 防止加了spacing之后onKeyDown无效
			onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT, null);
		} catch (Exception e) {

		}
	}

	private void move2Left() {
		try {
			onScroll(null, null, -1, 0); // 防止加了spacing之后onKeyDown无效
			onKeyDown(KeyEvent.KEYCODE_DPAD_LEFT, null);
		} catch (Exception e) {

		}
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		int event;
		int f = isScrollingLeft(e1, e2);
		if (f == -1) {
			return super.onFling(e1, e2, velocityX, velocityY);
		}
		if (isFling) {
			return super.onFling(e1, e2, velocityX, velocityY);
		} else {
			if (f == 0) {
				event = KeyEvent.KEYCODE_DPAD_LEFT;
			} else {
				event = KeyEvent.KEYCODE_DPAD_RIGHT;
			}
			onKeyDown(event, null);
			return false;
		}
	}

	private int isScrollingLeft(MotionEvent e1, MotionEvent e2) {
		if (e1 == null || e2 == null) {
			return -1;
		}
		return e2.getX() > e1.getX() ? 0 : 1;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// 屏蔽viewpager的页面切换
		if (null != pager) {
			pager.requestDisallowInterceptTouchEvent(true);
		}
		if (null != listView) {
			listView.requestDisallowInterceptTouchEvent(true);
		}
		int action = ev.getAction();
		switch (action) {

		case MotionEvent.ACTION_DOWN:
			gTouchStartX = ev.getX();
			gTouchStartY = ev.getY();
			super.onTouchEvent(ev);
			break;

		case MotionEvent.ACTION_MOVE:
			final float touchDistancesX = Math.abs(ev.getX() - gTouchStartX);
			final float touchDistancesY = Math.abs(ev.getY() - gTouchStartY);
			if (touchDistancesY * 2 >= touchDistancesX) {
				return false;
			} else {
				return true;
			}

		case MotionEvent.ACTION_CANCEL:
			break;
		case MotionEvent.ACTION_UP:
			break;

		default:
			break;
		}
		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// 屏蔽viewpager的页面切换
		if (null != pager) {
			pager.requestDisallowInterceptTouchEvent(true);
		}
		if (null != listView) {
			listView.requestDisallowInterceptTouchEvent(true);
		}

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			stopMove();
			break;
		case MotionEvent.ACTION_MOVE:

			break;
		case MotionEvent.ACTION_UP:
			startMove(isRight, delayMillis);
			break;
		}
		return super.onTouchEvent(event);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// 屏蔽viewpager的页面切换
		if (null != pager) {
			pager.requestDisallowInterceptTouchEvent(true);
		}
		if (null != listView) {
			listView.requestDisallowInterceptTouchEvent(true);
		}
		return super.dispatchTouchEvent(ev);
	}

	private ViewPager pager;
	private ListView listView;

	public void setViewPager(ViewPager viewPager) {
		this.pager = viewPager;
	}

	public void setListView(ListView listView) {
		this.listView = listView;
	}

	/**
	 * 添加监听
	 * */
	public void addSelectedListener(OnItemSelectedListener itemSelectedListener) {
		listeners.add(itemSelectedListener);
	}

	/**
	 * 移除监听
	 * */
	public void removeSelectedListener(
			OnItemSelectedListener itemSelectedListener) {
		listeners.remove(itemSelectedListener);
	}

	/**
	 * 清除回调
	 * */
	public void clearSelectedListener() {
		listeners.clear();
	}

	/**
	 * 扩展选中监听
	 * */
	private OnItemSelectedListener itemSelectedListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			if (listeners.size() > 0) {
				for (OnItemSelectedListener listener : listeners) {
					if (listener != null) {
						listener.onItemSelected(arg0, arg1, arg2, arg3);
					}
				}
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {

		}
	};
}
