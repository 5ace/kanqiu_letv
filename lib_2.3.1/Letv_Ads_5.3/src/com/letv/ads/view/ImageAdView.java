package com.letv.ads.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.TextView;
import com.letv.adlib.managers.status.ad.AdStatusManager;
import com.letv.adlib.model.ad.common.CommonAdItem;
import com.letv.adlib.model.ad.types.AdClickShowType;
import com.letv.ads.AdsManager;
import com.letv.ads.http.LetvSimpleAsyncTask;
import com.letv.ads.util.AdViewClickListener;
import com.letv.ads.util.AdsUtils;
import com.letv.ads.util.Commons;
import com.letv.ads.util.LogInfo;
import com.letv.cache.LetvCacheMannager;
import com.letv.watchball.ads.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
/**
 * 广告图片控件，主要用于显示 开机广告，焦点图广告，banner广告，搜索大图广告
 * */
public class ImageAdView extends LinearLayout {

	/**
	 * 广告图片控件
	 * */
	private ImageView imageView;


	/**
	 * 关闭按钮
	 * */
	private View closeView;

	/**
	 * 是否能关闭
	 * */
	private boolean canClose = false;

	/**
	 * 广告类型（开机）
	 * */
	private int adType = 1;

	/**
	 * 是否自动加载，true自动加载内容，自动曝光，false不加载；如果不需代码配置的广告可直接在xml文件中添加这个属性，如果需要代码配置的配置完成后调用方法
	 * */
	private boolean autoLoad;
	
	/**
	 * 是否自动响应点击
	 * */
	private boolean autoClick;

	/**
	 * 保持尺寸，控制requestLayout是否响应
	 * */
	private boolean keepSize;

	/**
	 * 是否在加载中，简单控制重复请求
	 * */
	private boolean isLoading;

	/**
	 * 是否有点击事件，显示后或者有跳转时返回true，阶段完成gallery的onItemSelected事件
	 * */
	private boolean hasClick;

	/**
	 * 定投专辑id
	 * */
	private String pid;

	/**
	 * 定投专辑频道id
	 * */
	private String cid;

	/**
	 * 广告素材对象
	 * */
	private CommonAdItem mAdInfo;

	private String clickStatistics;

	private String showStatistics;

	/**
	 * 用户id
	 * */
	private String uid;
	
	/**
	 * 是否加载完成后自动显示
	 * */
	private boolean autoShow ;

	/**
	 * 需要跳转播放的点击，回调给主程序
	 * */
	private AdViewClickListener listener;
	
	/**
	 * 广告图片显示完成后的回调，为需要对完成后做操作的地方调用
	 * */
	private AdLoadCompleteListener completeListener ;
	
	/**
	 * 是否完成后自动调用曝光统计，为了控制重复曝光做的
	 * */
	private boolean isUpShowed = true ;
      private TextView timeText;
      private int time = 3;
      //倒计时handler
      private Handler mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                  if (time > 0){
                        timeText.setText(time+"");
                        time--;
                        mHandler.sendEmptyMessageDelayed(0,1000);
                  }
            }
      };

      public ImageAdView(Context context) {
		super(context);
		init(context);
		requestAd();
	}

	public ImageAdView(Context context, AttributeSet attrs) {
		super(context, attrs);
		getAttrs(context, attrs);
		init(context);
		requestAd();
	}

	@Override
	public void requestLayout() {
		if (!keepSize) {
			super.requestLayout();
		}
	}

	private void init(Context context) {
		hasClick = false;
		inflate(context, R.layout.ad_view, this);
		findView();
		if (keepSize) {
			setVisibility(View.INVISIBLE);
		} else {
			setVisibility(View.GONE);
		}
		closeView.setVisibility(canClose ? VISIBLE : GONE);
	}

	/**
	 * 初始化属性
	 * */
      private void getAttrs(Context context, AttributeSet attrs) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.adview);
            canClose = ta.getBoolean(R.styleable.adview_canClose, false);
            adType = ta.getInt(R.styleable.adview_adType, 0);
            autoLoad = ta.getBoolean(R.styleable.adview_autoLoad, false);
            keepSize = ta.getBoolean(R.styleable.adview_keepSize, true);
            autoClick = ta.getBoolean(R.styleable.adview_autoClick, false);
            autoShow = ta.getBoolean(R.styleable.adview_autoShow, true);
            pid = ta.getString(R.styleable.adview_pid);
            cid = ta.getString(R.styleable.adview_cid);
            clickStatistics = ta.getString(R.styleable.adview_clickstatistics);
            showStatistics = ta.getString(R.styleable.adview_showstatistics);
            uid = ta.getString(R.styleable.adview_uid);

		ta.recycle();
	}

	/**
	 * 初始化控件
	 * */
	private void findView() {
		imageView = (ImageView) findViewById(R.id.ad_image);
		closeView = findViewById(R.id.ad_close);
            timeText = (TextView) findViewById(R.id.ad_time_text);
            closeView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setVisibility(View.GONE);
			}
		});
	}

	/**
	 * 是否能关闭
	 * */
	public boolean isCanClose() {
		return canClose;
	}

	/**
	 * 设置能否被关闭
	 * */
	public void setCanClose(boolean canClose) {
		try {
			this.canClose = canClose;
		} finally {
			closeView.setVisibility(canClose ? VISIBLE : GONE);
		}
	}

	/**
	 * 得到广告类型
	 * */
	public int getAdType() {
		return adType;
	}

	/**
	 * 设置广告类型
	 * */
	public void setAdType(int adType) {
		hasClick = false;
		this.adType = adType;
	}

	/**
	 * 是否初始化的时候置顶加载
	 * */
	public boolean isAutoLoad() {
		return autoLoad;
	}

	/**
	 * 设置是否初始化的时候加载
	 * */
	public void setAutoLoad(boolean autoLoad) {
		try {
			this.autoLoad = autoLoad;
		} finally {
			requestAd();
		}
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public String getClickStatistics() {
		return clickStatistics;
	}

	public void setClickStatistics(String clickStatistics) {
		this.clickStatistics = clickStatistics;
	}

	public String getShowStatistics() {
		return showStatistics;
	}

	public void setShowStatistics(String showStatistics) {
		this.showStatistics = showStatistics;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}
	public void setPcode(String pcode){
		Commons.PCODE = pcode;
	}
	private void requestAd() {
            LogInfo.log("ads", "---------- 1111 start----" + isAutoLoad() +  "  " + adType +  "  " + isLoading);
            if (autoLoad && adType != 0 && !isLoading) {
			new RequestFrontAd(getContext()).start();
		}
	}

	/**
	 * 加载广告的线程
	 * */
	private class RequestFrontAd extends LetvSimpleAsyncTask<CommonAdItem> {

		public RequestFrontAd(Context context) {
			super(context);
			isLoading = true;
		}

		@Override
		public CommonAdItem doInBackground() {
                  LogInfo.log("ads", "----------start----");
                  if (adType == 0) {
                        return null;
                  }
                  if (isCancel()) {
                        return null;
                  }
			CommonAdItem adInfo = requestAdInfo();
			
			return adInfo;
		}

		@Override
		public void onPostExecute(CommonAdItem result) {
			fill(result);
			isLoading = false;
		}

		@Override
		public void onPreExecute() {

		}
	}

	/**
	 * 根据广告类型，请求不同接口
	 * */
	private CommonAdItem requestAdInfo() {

		CommonAdItem adInfo = null;

		switch (adType) {
		case 1:
			adInfo = AdsManager.getInstance().getBeginAdInfo();
			break;
//		case 2:
//			adInfo = AdsManager.getInstance().getFocusAdInfo();
//			break;
//		case 3:
//			adInfo = AdsManager.getInstance().getFocusAdInfo2();
//			break;
//		case 4:
//			adInfo = AdsManager.getInstance().getBannerAdInfo(cid, pid);
//			break;
//		case 5:
//			adInfo = AdsManager.getInstance().getFullBackAdInfo();
//			break;
		}
		return adInfo;
	}

	/**
	 * 填充物料，及绘制
	 * */
	private void fill(final CommonAdItem adInfo) {

		if (adInfo != null) {
			mAdInfo = adInfo;
			
			DisplayImageOptions options = new DisplayImageOptions.Builder()
			.cacheOnDisc()
			.displayer(new SimpleBitmapDisplayer())
			.bitmapConfig(Config.RGB_565)
			.build();
			LetvCacheMannager.getInstance().loadImage(adInfo.mediaFileUrl, imageView , options , new ImageLoadingListener() {
				
				@Override
				public void onLoadingStarted(String imageUri, View view) {
//                              ViewGroup group = (ViewGroup) view.getParent();
//                              group.getChildAt(0).setVisibility(GONE);
                        }
				
				@Override
				public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				}
				
				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					imageView.setVisibility(VISIBLE);
                              imageView.setImageBitmap(loadedImage);
                              timeText.setVisibility(VISIBLE);
                              mHandler.sendEmptyMessageDelayed(0,1000);
                              boolean isSu = true ;
					if(completeListener != null){
						isSu = completeListener.onComplete();
					}
					if(isSu){
						if(autoShow){
							setVisibility(VISIBLE);
						}
						if(isUpShowed){
							/**
							 * 曝光统计
							 * */
							AdStatusManager statusMgr = new AdStatusManager(adInfo);
							statusMgr.onAdPlayStart();
						}
						
						hasClick = true;
						if (autoClick) {
							setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									hasClick();
								}
							});
						}
					}
				}
				
				@Override
				public void onLoadingCancelled(String imageUri, View view) {
				}
			});
			
		}
	}

	/**
	 * 判断点击时间是否需要响应，没哟响应回调到主程序
	 * */
	public boolean hasClick() {
		if (getVisibility() != VISIBLE) {
			hasClick = false;
		}
		if (hasClick) {
			if (mAdInfo.clickShowType == AdClickShowType.EnterVideoPlayer) { // 视频广告，回调主程序
				if (listener != null) {
					listener.onClick(mAdInfo.clickShowType, mAdInfo.specialAdInfo.pid,  mAdInfo.specialAdInfo.vid);
					
					/**
					 * 点击统计
					 * */
					AdStatusManager statusMgr = new AdStatusManager(mAdInfo);
					statusMgr.onAdClicked();
				}
			} else if(mAdInfo.clickShowType == AdClickShowType.EnterLivePlayer){
				if (listener != null) {
					listener.onClick(mAdInfo.clickShowType, mAdInfo.specialAdInfo.streamCode,  mAdInfo.specialAdInfo.streamURL);
					
					/**
					 * 点击统计
					 * */
					AdStatusManager statusMgr = new AdStatusManager(mAdInfo);
					statusMgr.onAdClicked();
				}
			}else {// 图片广告，打开浏览器
				clickGotoWeb();
			}
		}
		return hasClick;
	}

	/**
	 * 点击广告跳转到系统浏览器,并请求点击曝光地址和第三方点击曝光地址
	 */
	private void clickGotoWeb() {
		if (AdsUtils.checkClickEvent()) {
			if(mAdInfo.clickShowType == AdClickShowType.ExternalWebBrowser || mAdInfo.clickShowType == AdClickShowType.InternalWebView){
				if (!TextUtils.isEmpty(mAdInfo.getClickUrl())) {
					AdsUtils.gotoWeb(getContext(), mAdInfo.getClickUrl() , mAdInfo.clickShowType);
				}
			}
			/**
			 * 点击统计
			 * */
			AdStatusManager statusMgr = new AdStatusManager(mAdInfo);
			statusMgr.onAdClicked();
		}
	}

	/**
	 * 得到广告点击回调
	 * */
	public AdViewClickListener getListener() {
		return listener;
	}

	/**
	 * 设置广告点击回调
	 * */
	public void setListener(AdViewClickListener listener) {
		this.listener = listener;
	}

	/**
	 * 得到广告点击回调
	 * */
	public CommonAdItem getAdInfo() {
		return mAdInfo;
	}

	/**
	 * 如果请求过程和显示过程分离的操作，可以直接传入广告物料进行显示和曝光（如：首页焦点图广告）
	 * */
	public void setAdInfo(CommonAdItem mAdInfo, String pcode , boolean isUpShow) {
		isUpShowed = isUpShow ;
		this.mAdInfo = mAdInfo;
		setPcode(pcode);
		if(mAdInfo != null){
			fill(mAdInfo);
		}
	}
	
	/**
	 * 得到完成监听
	 * */
	public AdLoadCompleteListener getCompleteListener() {
		return completeListener;
	}

	/**
	 * 设置完成监听
	 * */
	public void setCompleteListener(AdLoadCompleteListener completeListener) {
		this.completeListener = completeListener;
	}

	/**
	 * 完成监听回调
	 * */
	public interface AdLoadCompleteListener{
		public boolean onComplete();
	}
	
	/**
	 * 曝光统计
	 * */
	public void sendShowStart(){
		if(mAdInfo == null)
			return ;
		/**
		 * 曝光统计
		 * */
		AdStatusManager statusMgr = new AdStatusManager(mAdInfo);
		statusMgr.onAdPlayStart();
	}
	
	
	/**
	 * 曝光统计
	 * */
	public void sendShowStart(CommonAdItem adInfo){
		if(adInfo == null)
			return ;
		/**
		 * 曝光统计
		 * */
		AdStatusManager statusMgr = new AdStatusManager(adInfo);
		statusMgr.onAdPlayStart();
	}
	
	public ImageView getImageView() {
		return imageView;
	}

	public void setImageView(ImageView imageView) {
		this.imageView = imageView;
	}
}
