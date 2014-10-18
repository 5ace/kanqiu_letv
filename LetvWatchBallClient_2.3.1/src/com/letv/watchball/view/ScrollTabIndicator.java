package com.letv.watchball.view;

import java.util.ArrayList;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.letv.watchball.R;
import com.letv.watchball.adapter.BaseScrollingTabsAdapter;
import com.letv.watchball.utils.UIs;

/**
 * 自定义水平分页器控件
 * @author ljnalex
 *
 */
public class ScrollTabIndicator extends HorizontalScrollView implements 
	ViewPager.OnPageChangeListener{
	
	private ViewPager mPager = null;
	private final LinearLayout mContainer;
	// 水平分页器
	private final LinearLayout mTabIndicator;
	// 水平分页器下方游标
	private ImageView mCursor; 
	
	private BaseScrollingTabsAdapter mAdapter = null;
	private Context mContext;
	
	private final ArrayList<View> mTabs = new ArrayList<View>();
	
	private final int TAB_MAX_COUNT = 8;
	// ViewPager的页面个数
	private int mPagerCount = 0;
	// 记录当前选中的Tab
	private int currentPos = 0;
	// 屏幕宽度
	private int screenWidth = 0;
	// 游标位移
	private int offset = 0;
	// 是否滚动
	boolean isScroll = false;
	
	private final String TAG = "ScrollTabIndicator";
	
	private int type;//1是点播，2是直播
	private OnPageChangeListener listener ;
	
	public ScrollTabIndicator(Context context) {
		this(context, null);
	}
	
	public ScrollTabIndicator(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		this.mContext = context;
	}
	
	public ScrollTabIndicator(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs);
		this.mContext = context;
		this.setHorizontalScrollBarEnabled(false);
		this.setHorizontalFadingEdgeEnabled(false);
		int w = UIs.getScreenWidth();
		int h = UIs.getScreenHeight();
		screenWidth = Math.min(w, h);
		
		mContainer = new LinearLayout(context);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
		       ViewGroup.LayoutParams.MATCH_PARENT,
		       ViewGroup.LayoutParams.MATCH_PARENT);
		mContainer.setLayoutParams(params);
		mContainer.setOrientation(LinearLayout.VERTICAL);
		
		mTabIndicator = new LinearLayout(context);
		mTabIndicator.setLayoutParams(params);
		mTabIndicator.setOrientation(LinearLayout.HORIZONTAL);
		
		mCursor = new ImageView(context);
		
		this.addView(mContainer);
	}
	
	public void setAdapter(BaseScrollingTabsAdapter adapter) {
		this.mAdapter = adapter;
		
		if (mPager != null && mAdapter != null)
		    initTabs();
	}

	public void setViewPager(ViewPager pager,int type) {
		this.mPager = pager;
		this.mPagerCount = pager.getAdapter().getCount();
		mPager.setOnPageChangeListener(this);
		
		if (mPager != null && mAdapter != null)
		    initTabs();
		
		this.type=type;
	}
	
	private void initTabs() {
		mContainer.removeAllViews();
		mTabs.clear();
		
		if (mAdapter == null || mPagerCount == 0)
		    return;
		
//		final int width = mPagerCount > TAB_MAX_COUNT ? 
//				(screenWidth / TAB_MAX_COUNT) : (screenWidth / mPagerCount);
		final  int width;
		if(type==2){
			width =screenWidth / 3;
		}else{
			width =screenWidth / 4;
		}
		
		
			
		LinearLayout.LayoutParams params = 
				new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT);

		// 添加水平分页器
		for (int i = 0; i < mPager.getAdapter().getCount(); i++) {
		
		    final int index = i;
		
		    View tab = mAdapter.getView(i);
		    tab.setLayoutParams(params);
		    
		    mTabIndicator.addView(tab);
		
		    tab.setFocusable(true);
		    
		    mTabs.add(tab);
		
		    tab.setOnClickListener(new OnClickListener() {
		        @Override
		        public void onClick(View v) {
		            if (mPager.getCurrentItem() == index) {
		                selectTab(index);
		            } else {
		            	calculateOffset(index-2, width);
		                mPager.setCurrentItem(index, true);
		            }
		        }
		    });
		
		}
		mContainer.addView(mTabIndicator);
		
		// 添加水平分页器游标
		LinearLayout linear = new LinearLayout(mContext);
		linear.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 4));
		linear.setOrientation(LinearLayout.VERTICAL);
		mCursor.setLayoutParams(new LayoutParams(width, LayoutParams.FILL_PARENT));
		mCursor.setImageResource(R.color.letv_color_ff2c95d2);
		
		linear.addView(mCursor);
		mContainer.addView(linear);
		
		// 添加水平分页器和ViewPager之间的分割线
		View underLine = new View(mContext);
		underLine.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 2));
		underLine.setBackgroundColor(0xff2c95d2);
		mContainer.addView(underLine);
		
		calculateOffset(mPager.getCurrentItem(), width);
		currentPos = mPager.getCurrentItem();
		selectTab(currentPos);
		mAdapter.updateView(mTabIndicator.getChildAt(currentPos), null, currentPos, 0);
	}
	
	protected void calculateOffset(int index, int silderW) {
		offset += (index - currentPos) * silderW;
		mCursor.post(new Runnable() {
			
			@Override
			public void run() {
				LinearLayout.LayoutParams params = 
						(LinearLayout.LayoutParams) mCursor.getLayoutParams();
				
				params.leftMargin = offset;
				mCursor.requestLayout();
			}
		});
	}

	@Override
	public void onPageScrollStateChanged(int state) {
		if(listener != null)
			listener.onPageScrollStateChanged(state);
		if (state == ViewPager.SCROLL_STATE_DRAGGING) { //滑动开始
			isScroll = true;
		} else if(state == ViewPager.SCROLL_STATE_IDLE) {// 滑动停止	
//			((MyViewPager)mPager).clearDirection();
			isScroll = false;
		} else if(state == ViewPager.SCROLL_STATE_SETTLING) {
			
		}
	}
	
	@Override
	public void onPageScrolled(final int position, final float positionOffset, int positionOffsetPixels) {
		if(listener != null)
			listener.onPageScrolled(position, positionOffset, positionOffsetPixels);
		mCursor.post(new Runnable() {
			
			@Override
			public void run() {
				LinearLayout.LayoutParams params = 
						(LinearLayout.LayoutParams) mCursor.getLayoutParams();
				
				/**
				 * 一个很巧的算法,不需要根据viewpager的方向来判断滑块的位移是增加,还是减少
				 * 从该方法的返回的参数来看,变化过程如下:
				 * 1.position: 0 -> 1
				 * 	 positionOffset: 0 -> 0.99xxx -> 0
				 * 2.position: 1 -> 0
				 * 	 positionOffset: 0 -> 0.99xxx -> 0
				 * 从上面的变化过程来看,position+positionOffset就是当前页面滑动的距离,与方向无关
				 * 这个变化过程,可以看成三个pager不动,有一个大的滑块从第一个到第二个平滑的移动
				 */
				params.leftMargin = (int) ((position + positionOffset) * mCursor.getWidth());
				
				mCursor.requestLayout();
			}
		});
	}
	
	@Override
	public void onPageSelected(int position) {
		if(listener != null)
			listener.onPageSelected(position);
		selectTab(position);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		
		if (changed && mPager != null) {
			
			selectTab(mPager.getCurrentItem());
		}
	}

	public void selectTab(int position) {
		
		for (int i = 0, pos = 0; i < mTabIndicator.getChildCount(); i ++ , pos++) {
		    View tab = mTabIndicator.getChildAt(i);
		    tab.setSelected(pos == position);
		}
		
		View selectedTab = mTabIndicator.getChildAt(position);
		View prevTab = mTabIndicator.getChildAt(currentPos);
		if(position != currentPos) {
			mAdapter.updateView(selectedTab, prevTab, position, currentPos);
		}
		
		final int w = selectedTab.getMeasuredWidth();
		final int l = selectedTab.getLeft();
		
		final int x = l - this.getWidth() / 2 + w / 2;
		
		//smoothScrollTo(x, this.getScrollY());
		
		currentPos = position;
	}

	public OnPageChangeListener getListener() {
		return listener;
	}

	public void setListener(OnPageChangeListener listener) {
		this.listener = listener;
	}
}
