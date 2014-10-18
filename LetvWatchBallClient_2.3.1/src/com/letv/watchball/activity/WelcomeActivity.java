
package com.letv.watchball.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.letv.ads.AdsManager;
import com.letv.ads.view.ImageAdView;
import com.letv.datastatistics.DataStatistics;
import com.letv.datastatistics.entity.ApiInfo;
import com.letv.datastatistics.entity.DataStatusInfo;
import com.letv.http.bean.LetvBaseBean;
import com.letv.http.bean.LetvDataHull;
import com.letv.watchball.LetvApplication;
import com.letv.watchball.R;
import com.letv.watchball.async.LetvAsyncTask;
import com.letv.watchball.async.LetvHttpAsyncTask;
import com.letv.watchball.bean.PushAdImage;
import com.letv.watchball.bean.WorldCupEntity;
import com.letv.watchball.db.DBManager;
import com.letv.watchball.db.PreferencesManager;
import com.letv.watchball.http.api.LetvHttpApi;
import com.letv.watchball.parser.PushAdImageParse;
import com.letv.watchball.parser.WorldCupParser;
import com.letv.watchball.utils.LetvConfiguration;
import com.letv.watchball.utils.LetvUtil;

public class WelcomeActivity extends LetvBaseActivity{
	
	
	private ImageView welcome;
	private final static int HAS_PUSH_AD_IMAGE = 1;
	private final static int GO_MAIN = 2;
	
	public Handler mHandler = new Handler(){
		
		public void handleMessage(Message msg) {
			switch(msg.what){
				case  HAS_PUSH_AD_IMAGE:
					showPushAdImg((PushAdImage)msg.obj);
					break;
				case GO_MAIN:
					goMain();
					// 接口初始化状态、客户端设备信息上报、升级信息、广告控制、精品推荐控制
					doRequestDataStatusInfo();
					break;
			}
		};
	};
      private ImageAdView adView;
      private boolean adIsSuccess;
      private boolean isChoice;

      @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);
		welcome = (ImageView)this.findViewById(R.id.welcome);
            adView = (ImageAdView) findViewById(R.id.ad_view);
            adView.setCompleteListener(new ImageAdView.AdLoadCompleteListener() {

                  @Override
                  public boolean onComplete() {
                	  Log.d("ads","loadcomplete");
                        adIsSuccess = true;
                        welcome.setVisibility(View.GONE);
                        adView.setVisibility(View.VISIBLE);
                        mHandler.sendEmptyMessageDelayed(GO_MAIN,3000);
                        return !isChoice;
                  }
            });


            initAds();
            //获取开机广告图
//		doRequestWelcomeAd();
            mHandler.postDelayed(new Runnable() {
                  @Override
                  public void run() {
                	  Log.d("ads" ,"isshowad=" + PreferencesManager.getInstance().isShowAd());
                        if(PreferencesManager.getInstance().isShowAd()){
                              adView.setAdType(1);
                              adView.setAutoLoad(true);
                        } else {
                              adView.setAdType(1);
                              adView.setAutoLoad(false);
                        }
                  }
            },1000);
            mHandler.postDelayed(new Runnable() {
                  @Override
                  public void run() {
                        if (!adIsSuccess){
                              mHandler.sendEmptyMessage(GO_MAIN);
                              adIsSuccess = true;
                        }
                  }
            },2000);
            //获取世界杯开关
            requestWorldCup();
//		// 接口初始化状态、客户端设备信息上报、升级信息、广告控制、精品推荐控制
//		doRequestDataStatusInfo();

	}

      /**
       * 初始化广告
       */
      private void initAds() {
            // 初始化广告包
            AdsManager.getInstance().initAdData(getApplicationContext(), "androidPhone", "android", LetvUtil.getClientVersionName(),
                    LetvUtil.getPcode(), LetvConfiguration.isDebug());
            AdsManager.getInstance().setVipCallBack(new AdsManager.VipCallBack() {
                  @Override
                  public boolean isVip() {
//                        boolean isVip = false;
//                        long cancelTime = PreferencesManager.getInstance().getVipCancelTime();
                        return false;
//                                isVip && (cancelTime > TimestampBean.getTm().getCurServerTime());
                  }
            });

            AdsManager.getInstance().setInit(true);
      }

	public void goMain(){
		mHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				Intent mIntent=new Intent(WelcomeActivity.this, MainActivity.class);
				mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(mIntent);
				finish();
			}
		}, 800);
	}
	
	public void showPushAdImg(PushAdImage pushAdImage){
//		view = LayoutInflater.from(getBaseContext()).inflate(R.layout.push_ad_image, null);
//		LetvImageView push_ad_img = (LetvImageView)view.findViewById(R.id.push_ad_img);
//		LetvCacheMannager.getInstance().loadImage(pushAdImage.getPic1(), push_ad_img);
//		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
//		AlphaAnimation outAnimation = new AlphaAnimation(1.0f, 0.0f);
//		AlphaAnimation inAnimation = new AlphaAnimation(0.0f, 1.0f);
//		outAnimation.setDuration(500);
//		inAnimation.setDuration(500);
//		welcome.setAnimation(outAnimation);
//		welcome.startAnimation(outAnimation);
//		welcome.setVisibility(View.GONE);
//		push_ad_img.setAnimation(inAnimation);
//		push_ad_img.startAnimation(inAnimation);
//		push_ad_img.setVisibility(View.VISIBLE);
		mHandler.sendEmptyMessageDelayed(GO_MAIN, 2000);
		
	}
	
	public void doRequestWelcomeAd(){
		 PushAdImage pushAdImage =  DBManager.getInstance().getPushAdImageTrace().getPushAdImage();
		 if(null != pushAdImage && !TextUtils.isEmpty(pushAdImage.getPic1())){
			Message msg = mHandler.obtainMessage();
			msg.what = HAS_PUSH_AD_IMAGE;
			msg.obj = pushAdImage;
			mHandler.sendMessageDelayed(msg, 2000);
			new RequestWelcomeAd(this).start();
			return;
		 }else{
			 new RequestWelcomeAd(this).start();
			 mHandler.sendEmptyMessage(GO_MAIN);
		 }
	}

      public void doRequestDataStatusInfo() {

            DataStatistics.getInstance().setDebug(LetvUtil.isDebug());
            new RequestDataStatusInfo(this).start();
      }

    private class RequestWelcomeAd extends LetvHttpAsyncTask<PushAdImage>{

    	public RequestWelcomeAd(Context context){
    		 super(context, false);
    	}
    	
		@Override
		public LetvDataHull<PushAdImage> doInBackground() {
			
			LetvDataHull<PushAdImage> dataHull = LetvHttpApi.requestGetPushAd(0,new PushAdImageParse());
			
			return dataHull;
		}

		@Override
		public void onPostExecute(int updateId, PushAdImage result) {
                  boolean playPlatform = result.getPlayPlatform();
                  if(playPlatform){
				DBManager.getInstance().getPushAdImageTrace().clearAll();
				DBManager.getInstance().getPushAdImageTrace().savePushAdImage(result);
			} else {
                        DBManager.getInstance().getPushAdImageTrace().clearAll();
                  }

                  Log.d("smydebug", "PlayPlatform" + result.getPlayPlatform());

		}
    	
    }
      
      private class RequestDataStatusInfo extends LetvHttpAsyncTask<LetvBaseBean> {

            public RequestDataStatusInfo(Context context) {
                  super(context, false);
            }

            @Override
            public LetvDataHull<LetvBaseBean> doInBackground() {
                  try {
                        DataStatusInfo result = DataStatistics.getInstance().getDataStatusInfo(LetvApplication.getInstance(), LetvUtil.getPcode());
                        if (result != null) {

                              LetvApplication.getInstance().setDataStatusInfo(result);

                              if (result.getApiInfo() != null && ApiInfo.APISTATUS_TEST.equals(result.getApiInfo().getApistatus())) {
                            	    LetvApplication.getInstance().setUseTest(true);
                                    LetvHttpApi.setTest(true);
                              } else {
                                    LetvApplication.getInstance().setUseTest(false);
                                    LetvHttpApi.setTest(false);
                              }

                        }
                       
                  } catch (Exception e) {
                        e.printStackTrace();
                  }
                  
                  return null;
            }

            @Override
            public void onPostExecute(int updateId, LetvBaseBean result) {
            }
      }
      
      @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	
    	if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
    		return true;
    	}
    	  
    	return super.onKeyDown(keyCode, event);
    }


      private WorldCupTask worldCupTask;
      private void requestWorldCup() {
            if(LetvUtil.hasNet()){
                  if(worldCupTask != null){
                        if (worldCupTask.isCancelled()){
                              worldCupTask.cancel();
                              worldCupTask = new WorldCupTask();
                              worldCupTask.execute();
                        }
                  } else {
                        worldCupTask = new WorldCupTask();
                        worldCupTask.execute();
                  }
            }
      }



      private class WorldCupTask extends LetvAsyncTask<Void,LetvDataHull<WorldCupEntity>> {
            @Override
            protected LetvDataHull<WorldCupEntity> doInBackground() {
                  return LetvHttpApi.requestShowWorldCup(0, new WorldCupParser());
            }

            @Override
            protected void onPostExecute(LetvDataHull<WorldCupEntity> result) {
                  if (result != null && result.getDataEntity() != null){

                        Log.d("ads",result.getDataEntity().isShowAD() + "    " + result.getDataEntity().isShowUTP() +"  "+ result.getDataEntity().isShowWorldCup());
                        
                        PreferencesManager.getInstance().setShowWorldCup(result.getDataEntity().isShowWorldCup());
                        PreferencesManager.getInstance().setShowAd(result.getDataEntity().isShowAD());
                        AdsManager.getInstance().setShowAd(result.getDataEntity().isShowAD());
                        PreferencesManager.getInstance().setUtp(result.getDataEntity().isShowUTP());
                  }
            }
      }

}

