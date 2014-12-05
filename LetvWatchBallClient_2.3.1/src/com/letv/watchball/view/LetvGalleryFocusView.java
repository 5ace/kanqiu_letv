package com.letv.watchball.view;

import java.util.List;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.letv.android.slidingmenu.lib.CustomViewAbove;
import com.letv.watchball.R;
import com.letv.watchball.utils.UIs;

public class LetvGalleryFocusView extends RelativeLayout {
	/**
	 * 焦点图控件
	 * */
	private LetvGallery gallery;
	/**
	 * 焦点图控件
	 * */
	private CircleGalleryIndicator galleryIndicator;
	private List<?> list;
	private Context context;

	private ViewPager pager;
	private ListView listView;

	public interface ChannelFocusViewListener {
		public void showFocusView();//
	}

	public LetvGalleryFocusView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		init();
	}

	public LetvGalleryFocusView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init();
	}

	public LetvGalleryFocusView(Context context) {
		super(context);
		this.context = context;
		init();
	}

	/**
	 * 当前View是否可见
	 * 
	 * @return
	 */
	public boolean isThisViewVisible() {
		if (list != null && list.size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @param list
	 * @param adapter
	 */
	public void setFocusInitData(List<?> list, BaseAdapter adapter) {
		if (list != null && list.size() > 0 && adapter != null) {
			try {
				this.list = list;
				int size = list.size();
				gallery.setAdapter(adapter);
				galleryIndicator.setViewFlow(gallery);
				galleryIndicator.setTotle(size);
				gallery.startMove(true, 5000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 设置更新数据
	 * 
	 * @param list
	 */
	public void setFocusInitData(List<?> list) {
		if (list != null && list.size() > 0) {
			try {
				this.list = list;
				int size = list.size();
				galleryIndicator.setTotle(size);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 为了拦截viewPager左右滑动事件，设置当前viewPager
	 * 
	 * @param viewPager
	 */
	public void setViewPager(ViewPager viewPager) {
		this.pager = viewPager;
		gallery.setViewPager(viewPager);
	}

	/**
	 * 为了拦截listView滑动事件，设置当前listView
	 * 
	 * @param listView
	 */
	public void setListView(ListView listView) {
		this.listView = listView;
		// gallery.setListView(listView);
	}

	/**
	 * 释放资源
	 */
	public void destroy() {
		galleryIndicator = null;
		if (gallery != null) {
			gallery = null;
		}
		if (list != null) {
			list.clear();
			list = null;
		}
	}

	/**
	 * 开始移动
	 */
	public void startMove() {
		if (gallery != null) {
			gallery.startMove(true, 5000);
		}
	}

	/**
	 * 暂停移动
	 */
	public void stopRemove() {
		if (gallery != null) {
			gallery.stopMove();
		}

	}

	// =======================================================================================
	// =======================================================================================
	protected void init() {
		inflate(context, R.layout.home_tope_gallery_layout, this);
		findView();
	}

	public void setFocusHeight(int height) {
		LetvGallery gallery = (LetvGallery) findViewById(R.id.top_gallery);
		ViewGroup group = (ViewGroup) gallery.getParent();
		// View frameLayout = findViewById(R.id.channel_framelayout);
		FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.MATCH_PARENT,
				FrameLayout.LayoutParams.MATCH_PARENT);
		RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT, UIs.dipToPx(height));
		group.setLayoutParams(rlp);
		gallery.setLayoutParams(flp);
	}

	// =======================================================================================
	// =======================================================================================
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// 屏蔽viewpager、listView页面切换
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (null != pager) {
				pager.requestDisallowInterceptTouchEvent(true);
			}
			// if (null != listView) {
			// listView.requestDisallowInterceptTouchEvent(true);
			// }
			break;
		case MotionEvent.ACTION_UP:
			if (null != pager) {
				pager.requestDisallowInterceptTouchEvent(false);
			}
			// if (null != listView) {
			// listView.requestDisallowInterceptTouchEvent(false);
			// }
			break;
		case MotionEvent.ACTION_CANCEL:
			if (null != pager) {
				pager.requestDisallowInterceptTouchEvent(false);
			}
			// if (null != listView) {
			// listView.requestDisallowInterceptTouchEvent(false);
			// }
			break;
		case MotionEvent.ACTION_MOVE:
			if (null != pager) {
				pager.requestDisallowInterceptTouchEvent(true);
			}
			// if (null != listView) {
			// listView.requestDisallowInterceptTouchEvent(true);
			// }
			break;
		}

		return super.onInterceptTouchEvent(ev);// gallery.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// 屏蔽viewpager、listView页面切换
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			CustomViewAbove.mEnabled = false;// 控制焦点图滑动不移动大空间
			if (null != pager) {
				pager.requestDisallowInterceptTouchEvent(true);
			}
			// if (null != listView) {
			// listView.requestDisallowInterceptTouchEvent(true);
			// }
			break;
		case MotionEvent.ACTION_UP:
			CustomViewAbove.mEnabled = true;// 控制焦点图滑动不移动大空间
			if (null != pager) {
				pager.requestDisallowInterceptTouchEvent(false);
			}
			// if (null != listView) {
			// listView.requestDisallowInterceptTouchEvent(false);
			// }
			break;
		case MotionEvent.ACTION_CANCEL:
			CustomViewAbove.mEnabled = true;// 控制焦点图滑动不移动大空间
			if (null != pager) {
				pager.requestDisallowInterceptTouchEvent(false);
			}
			// if (null != listView) {
			// listView.requestDisallowInterceptTouchEvent(false);
			// }
			break;
		case MotionEvent.ACTION_MOVE:
			CustomViewAbove.mEnabled = false;// 控制焦点图滑动不移动大空间
			if (null != pager) {
				pager.requestDisallowInterceptTouchEvent(true);
			}
			// if (null != listView) {
			// listView.requestDisallowInterceptTouchEvent(true);
			// }
			break;
		}
		return super.onTouchEvent(ev);// gallery.onTouchEvent(ev);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// 屏蔽viewpager、listView页面切换
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			CustomViewAbove.mEnabled = false;// 控制焦点图滑动不移动大空间
			if (null != pager) {
				pager.requestDisallowInterceptTouchEvent(true);
			}
			// if (null != listView) {
			// listView.requestDisallowInterceptTouchEvent(true);
			// }
			break;
		case MotionEvent.ACTION_UP:
			CustomViewAbove.mEnabled = true;// 控制焦点图滑动不移动大空间
			if (null != pager) {
				pager.requestDisallowInterceptTouchEvent(false);
			}
			// if (null != listView) {
			// listView.requestDisallowInterceptTouchEvent(false);
			// }
			break;
		case MotionEvent.ACTION_CANCEL:
			CustomViewAbove.mEnabled = true;// 控制焦点图滑动不移动大空间
			if (null != pager) {
				pager.requestDisallowInterceptTouchEvent(false);
			}
			// if (null != listView) {
			// listView.requestDisallowInterceptTouchEvent(false);
			// }
			break;
		case MotionEvent.ACTION_MOVE:
			CustomViewAbove.mEnabled = false;// 控制焦点图滑动不移动大空间
			if (null != pager) {
				pager.requestDisallowInterceptTouchEvent(true);
			}
			// if (null != listView) {
			// listView.requestDisallowInterceptTouchEvent(true);
			// }
			break;
		}
		return super.dispatchTouchEvent(ev);// gallery.dispatchTouchEvent(ev);
	}

	protected void findView() {
		gallery = (LetvGallery) findViewById(R.id.top_gallery);
		galleryIndicator = (CircleGalleryIndicator) findViewById(R.id.top_gallery_indicator);
	}

}
