package com.letv.watchball.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;
import cn.com.iresearch.mvideotracker.IRVideo;

import com.letv.ads.AdsManager;
import com.letv.android.lcm.LetvPushManager;
import com.letv.android.slidingmenu.lib.app.SlidingFragmentActivity;
import com.letv.cache.LetvCacheMannager;
import com.letv.datastatistics.DataStatistics;
import com.letv.datastatistics.entity.DataStatusInfo;
import com.letv.datastatistics.entity.UpgradeInfo;
import com.letv.http.bean.LetvDataHull;
import com.letv.pp.service.LeService;
import com.letv.watchball.LetvApplication;
import com.letv.watchball.R;
import com.letv.watchball.async.LetvAsyncTask;
import com.letv.watchball.async.RequestInfoTask;
import com.letv.watchball.async.UpdateDownloadAsyncTask;
import com.letv.watchball.async.UpdateDownloadAsyncTask.UpdataAppException;
import com.letv.watchball.bean.IP;
import com.letv.watchball.bean.TimestampBean;
import com.letv.watchball.bean.WorldCupEntity;
import com.letv.watchball.db.PreferencesManager;
import com.letv.watchball.fragment.WebFragmentBackListener;
import com.letv.watchball.http.api.LetvHttpApi;
import com.letv.watchball.manager.FragmentManager;
import com.letv.watchball.parser.WorldCupParser;
import com.letv.watchball.share.AccessTokenKeeper;
import com.letv.watchball.utils.LetvConfiguration;
import com.letv.watchball.utils.LetvUtil;
import com.letv.watchball.utils.UIs;
import com.letv.watchball.view.NewFeaturesDialog;
import com.umeng.analytics.MobclickAgent;
import com.letv.ads.view.ImageAdView;
import com.letv.ads.view.ImageAdView.AdLoadCompleteListener;

import java.io.IOException;

public class MainActivity extends SlidingFragmentActivity implements Icloseable {
	/**
	 * Substitute you own sender ID here. you got from the push server, Most
	 * applications use a single sender ID. You may use multiple senders if
	 * different servers may send messages to the app.
	 */
	private static final String SENDER_ID = "WpAq";
	/**
	 * Substitute you own app ID here, you got from the push server, it identify
	 * the application like package name.
	 */
	private static final String APP_ID = "JbMz";

	private static final String SHARED_PREFERENCE_FILE = "device_token";
	private static final String UNREGISTER_SUCCESS = "unregister success";
	private LetvPushManager mLpm;
	public static final String PROPERTY_REG_ID = "registration_id";
	Context mApplicationContext;
	String regid;
	// ------------------------------------------------------------
	private FragmentManager manager;
	private static MainActivity instance;
	private boolean isForceClose = false;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			isForceClose = true;
		}
	};
	private WebFragmentBackListener mWebFragmentBackListener;
	private LeService p2pService = null;// p2p 的service每次开机后，开启即可

	public void setWebFragmentBackListener(WebFragmentBackListener listener) {
		mWebFragmentBackListener = listener;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		mApplicationContext = getApplicationContext();

		instance = this;

		manager = new FragmentManager();
		LetvApplication.getInstance().setManager(manager);
		manager.onCreate(MainActivity.this);
		// 注册友盟统计的crash事件统计
		MobclickAgent.onError(this);

		CloseableManager.getInstance().add(this);

		try {
			// 环境日志在用户的每次开机上报
			IP ip = LetvApplication.getInstance().getIp();
			DataStatistics.getInstance().sendEnvInfo(this, "0", "0",
					ip == null ? "" : ip.getClient_ip(), LetvUtil.getSource(),
					false);
			// 开机时初始化艾瑞视频统计，获取设备UID，注册艾瑞分配的UAID
			IRVideo.getInstance(MainActivity.this).getUid();
			IRVideo.getInstance(MainActivity.this).init("letv-140001");
		} catch (Exception e) {
			e.printStackTrace();
		}

		DataStatistics.getInstance().sendLoginInfo(this, "0", "0",
				LetvUtil.getUID(), "-", "-",
				System.currentTimeMillis() / 1000 + "", LetvUtil.getPcode(), 0);

		// 获取数据，检查更新
		// 关闭新手引导，2.2产品需求
		boolean isShow = false;
		// PreferencesManager.getInstance().isShowNewFeaturesDialog();
		if (isShow) {
			new NewFeaturesDialog(this,
					new NewFeaturesDialog.NewFeaturesDialogListener() {

						@Override
						public void onStart() {
							UIs.fullScreen(MainActivity.this);
							PreferencesManager.getInstance()
									.notShowNewFeaturesDialog();
						}

						@Override
						public void onCancel() {
							UIs.notFullScreen(MainActivity.this);
							new Handler().postDelayed(new Runnable() {

								@Override
								public void run() {
									LetvClientRequestInfoTask();
									checkUpdateVersionInfo();
								}
							}, 2000);
						}
					}, false).show();
		}
		if (!isShow) {
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					LetvClientRequestInfoTask();
					checkUpdateVersionInfo();
				}
			}, 2000);
		}

		setUtp();

		if (!AdsManager.getInstance().isInit()) {
			initAds();
		}
	}

	/**
	 * 初始化广告
	 */
	private void initAds() {
		// 初始化广告包
		AdsManager.getInstance().initAdData(getApplicationContext(),
				"androidPhone", "android", LetvUtil.getClientVersionName(),
				LetvUtil.getPcode(), LetvConfiguration.isDebug());
		AdsManager.getInstance().setVipCallBack(new AdsManager.VipCallBack() {
			@Override
			public boolean isVip() {
				// boolean isVip = false;
				// long cancelTime =
				// PreferencesManager.getInstance().getVipCancelTime();
				return false;
				// isVip && (cancelTime >
				// TimestampBean.getTm().getCurServerTime());
			}
		});

		AdsManager.getInstance().setInit(true);
	}

	@Override
	public int getRequestedOrientation() {
		return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
	}

	public static void launch(Context context) {

		Intent i = new Intent();
		i.setClass(context, MainActivity.class);

		if (!(context instanceof FragmentActivity)) {
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		}

		context.startActivity(i);
		// 用户登录数据统计

	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);

		if (isForceClose) {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					finish();
				}
			}, 200);
		}

	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mWebFragmentBackListener != null) {
				if (mWebFragmentBackListener.keyBackPress())
					return true;
			}
			if (!getSlidingMenu().isMenuShowing() && manager.closeNewsFilter()) {
				new AlertDialog.Builder(this)
						.setTitle(R.string.dialog_eixt_title)
						.setMessage(R.string.dialog_eixt_msg)
						.setPositiveButton(R.string.dialog_eixt_yes,
								new OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										finish();
									}
								})
						.setNegativeButton(R.string.dialog_eixt_no, null)
						.show();
				return true;
			}
		}
		return super.onKeyUp(keyCode, event);
	}

	public static MainActivity getInstance() {
		return instance;
	}

	/**
	 * 客户端提示语服务端化、上报出错统计
	 */
	private void LetvClientRequestInfoTask() {
		new RequestInfoTask(MainActivity.this).start();
	}

	private boolean isForceUpdate = false;

	/**
	 * 检查升级
	 */
	private void checkUpdateVersionInfo() {
		DataStatusInfo mDataStatusInfo = LetvApplication.getInstance()
				.getDataStatusInfo();

		if (mDataStatusInfo != null) {
			UpgradeInfo mUpgradeInfo = mDataStatusInfo.getUpgradeInfo();

			if (mUpgradeInfo != null
					&& UpgradeInfo.UPGRADE_YES
							.equals(mUpgradeInfo.getUpgrade())) {

				PreferencesManager.getInstance().setIsNeedUpdate(true);

				isForceUpdate = UpgradeInfo.UPTYPE_FORCE.equals(mUpgradeInfo
						.getUptype());
				showUpdateDialog(mUpgradeInfo, isForceUpdate);
			} else {
				PreferencesManager.getInstance().setIsNeedUpdate(false);
			}
			// showUpdateDialog(mUpgradeInfo,
			// mUpgradeInfo.getUptype().equals(UpgradeInfo.UPTYPE_FORCE));
		}
	}

	/**
	 * 显示升级提示框
	 * 
	 * @param result
	 * @param isForceUpdate
	 */
	private void showUpdateDialog(final UpgradeInfo result,
			boolean isForceUpdate) {
		Log.d("updateInfo", "------------>" + result.toString());
		if (isForceUpdate) {
			LetvApplication.getInstance().setForceUpdating(true);
			UIs.call(MainActivity.this, result.getTitle(), result.getMsg(),
					R.string.update_update, R.string.update_exit,
					new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							try {
								new UpdateDownloadAsyncTask(MainActivity.this,
										result.getUrl(), result.getV(), result
												.getUptype(), mHandler)
										.execute();
							} catch (UpdataAppException e) {
								e.printStackTrace();
							}
						}
					}, new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							MainActivity.this.finish();
							// Process.killProcess(Process.myPid());
							LetvCacheMannager.getInstance().destroy();
							LetvApplication.getInstance().setForceUpdating(
									false);
						}
					}, false);
		} else {
			UIs.call(MainActivity.this, result.getTitle(), result.getMsg(),
					R.string.update_update, R.string.update_later,
					new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							try {
								new UpdateDownloadAsyncTask(MainActivity.this,
										result.getUrl(), result.getV(), result
												.getUptype()).execute();
							} catch (UpdataAppException e) {
								e.printStackTrace();
							}
						}
					}, new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					}, false);
		}
	}

	/**
	 * utp开关设置
	 */
	private void setUtp() {
		if ("no".equalsIgnoreCase(LetvApplication.getInstance()
				.getVideoFormat())) {
			PreferencesManager.getInstance().setUtp(false);
		}
		if (PreferencesManager.getInstance().getUtp()) {
			// 如果p2p开关开启，则打开p2p//0642版本，控制是否在3g下上传数据
			p2pService = new LeService(false, true);
			try {
				p2pService
						.startService(
								this,
								6990,
								"cache.max_size=20M&downloader.pre_download_size=10M&enable_android_log=false&app_id=70");
				LetvApplication.getInstance().setP2pService(p2pService);
			} catch (IOException e) {
				e.printStackTrace();
			}
			// P2P._getinstace().setP2PMode(true);
			// P2P._getinstace().setISP2P(true);
		} else {
			// P2P._getinstace().setP2PMode(false);
			// P2P._getinstace().setISP2P(false);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onDestroy() {
		AccessTokenKeeper.clear(this);
		// 退出时清除艾瑞缓存
		IRVideo.getInstance(MainActivity.this).clearVideoPlayInfo();
		// manager.onDestroy();
		super.onDestroy();
		if (null != p2pService) {
			p2pService.stopService();
		}
		// manager.onDestroy();
		instance = null;
	}

	@Override
	public void finish() {

		close();
		// manager.onDestroy();
		// ActivityManager activityMgr=
		// (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE );
		// activityMgr.restartPackage(getPackageName());
		System.exit(0);
		super.finish();

	}

	@Override
	public void close() {
		CloseableManager.getInstance().close(this);
	}

}
