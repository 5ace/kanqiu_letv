package com.letv.watchball.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView;

import android.widget.Toast;
import com.letv.datastatistics.DataStatistics;
import com.letv.datastatistics.entity.ApiInfo;
import com.letv.datastatistics.entity.DataStatusInfo;
import com.letv.datastatistics.entity.RecommendInfo;
import com.letv.datastatistics.entity.UpgradeInfo;
import com.letv.http.bean.LetvBaseBean;
import com.letv.http.bean.LetvDataHull;
import com.letv.watchball.LetvApplication;
import com.letv.watchball.R;
import com.letv.watchball.async.LetvHttpAsyncTask;
import com.letv.watchball.async.UpdateDownloadAsyncTask;
import com.letv.watchball.bean.IP;
import com.letv.watchball.db.PreferencesManager;
import com.letv.watchball.http.api.LetvHttpApi;
import com.letv.watchball.push.LetvWbPushService;
import com.letv.watchball.ui.impl.FeedBackActivity;
import com.letv.watchball.utils.LetvUtil;
import com.letv.watchball.utils.UIs;
import com.letv.watchball.view.SwitchView;
import com.umeng.fb.UMFeedbackService;

public class SettingsActivity extends LetvBaseActivity implements
		OnClickListener, Icloseable {
	private SwitchView mSwitchView;
	private static final int REQUEST_CODE_GAME_START_REMIND = 0x1001;
	private static final int REQUEST_CODE_PLAY_HD = 0x1002;
	private SwitchView mSleepSwitchView;
	private SwitchView mPushserviceSwitchView;
	private static boolean upDateIsRunning = false;

	public static boolean isUpDateIsRunning() {
		return upDateIsRunning;
	}

	public static void setUpDateIsRunning(boolean upDateIsRunning) {
		SettingsActivity.upDateIsRunning = upDateIsRunning;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		CloseableManager.getInstance().add(this);
		// mGameStartSet = (RelativeLayout)
		// findViewById(R.id.seting_item_game_start_ctrl);
		mSwitchView = (SwitchView) findViewById(R.id.seting_item_game_result_switchview);
		mSleepSwitchView = (SwitchView) findViewById(R.id.seting_item_sleep_switchview);
		mPushserviceSwitchView = (SwitchView) findViewById(R.id.seting_item_pushservice_switchview);

		findViewById(R.id.setting_item_update).setOnClickListener(this);
		findViewById(R.id.setting_item_about).setOnClickListener(this);
		findViewById(R.id.setting_item_proposal).setOnClickListener(this);
		findViewById(R.id.seting_item_game_start_ctrl).setOnClickListener(this);
		findViewById(R.id.head_back).setOnClickListener(this);
		findViewById(R.id.settins_item_hd).setOnClickListener(this);
		// SwitchView 0为选中，1为不选

		mSwitchView.setSelection(PreferencesManager.getInstance()
				.isGameResultRemind() ? 0 : 1);
		mSwitchView.setListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == 0) {
					/**
					 * 打开赛果轮询及开赛提醒
					 */
					PreferencesManager.getInstance().setGameResultRemind(true);
					LetvWbPushService.schedule(SettingsActivity.this);
					PreferencesManager.getInstance().setGameStartRemind(10);
				} else {
					/**
					 * 关闭赛果轮询及开赛提醒
					 */
					PreferencesManager.getInstance().setGameResultRemind(false);
					LetvWbPushService.unschedule(SettingsActivity.this);
					PreferencesManager.getInstance().setGameStartRemind(-1);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
		mSleepSwitchView.setSelection(PreferencesManager.getInstance()
				.isSleepRemind() ? 0 : 1);
		mSleepSwitchView.setListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == 0) {
					// 开启免打扰
					PreferencesManager.getInstance().setSleepRemind(true);
				} else {
					// 关闭免打扰
					PreferencesManager.getInstance().setSleepRemind(false);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		mPushserviceSwitchView.setSelection(PreferencesManager.getInstance()
				.isPushservice() ? 0 : 1);
		mPushserviceSwitchView.setListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == 0) {
					// 开启推送
					PreferencesManager.getInstance().setPushservice(true);
				} else {
					// 关闭推送
					PreferencesManager.getInstance().setPushservice(false);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		setPlayHd();
		setGameStartRemind();
		View newVersion = findViewById(R.id.new_version);
		if (LetvUtil.hasNet()) {
			DataStatusInfo mDataStatusInfo = LetvApplication.getInstance()
					.getDataStatusInfo();

			if (mDataStatusInfo != null) {
				UpgradeInfo mUpgradeInfo = mDataStatusInfo.getUpgradeInfo();

				if (mUpgradeInfo != null
						&& UpgradeInfo.UPGRADE_YES.equals(mUpgradeInfo
								.getUpgrade())) {
					PreferencesManager.getInstance().setIsNeedUpdate(true);
					// 在升级按钮后提示有新版本
					if (newVersion.getVisibility() != View.VISIBLE)
						;
					newVersion.setVisibility(View.VISIBLE);
				} else {
					PreferencesManager.getInstance().setIsNeedUpdate(false);
					if (newVersion.getVisibility() != View.GONE)
						;
					newVersion.setVisibility(View.GONE);
				}
			} else {
				new RequestDataStatusInfo(this).start();
			}
		} else {
			UIs.notifyLongNormal(SettingsActivity.this, R.string.toast_net_null);

		}
	}

	/**
	 * 设置开赛提醒 显示
	 */
	private void setGameStartRemind() {
		int remindAhead = PreferencesManager.getInstance().getGameStartRemind();
		int[] gameRemindArrInt = getResources().getIntArray(
				R.array.game_remind_arr_int);
		int index = 0;
		for (int i = 0; i < gameRemindArrInt.length; i++) {
			if (remindAhead == gameRemindArrInt[i]) {
				index = i;
				break;
			}
		}
		((TextView) findViewById(R.id.seting_item_game_start_tv))
				.setText(getResources().getStringArray(
						R.array.game_remind_arr_str)[index]);
		if (index == gameRemindArrInt.length - 1) {
			((TextView) findViewById(R.id.seting_item_game_start_tv))
					.setText(getResources().getStringArray(
							R.array.game_remind_arr_str)[index].subSequence(0,
							2));
		}
	}

	/**
	 * 设置--清晰度设置
	 */
	private void setPlayHd() {
		boolean playHd = (PreferencesManager.getInstance().isPlayHd() != 0);
		String[] playHdArr = getResources().getStringArray(R.array.playHd);
		((TextView) findViewById(R.id.settings_play_hd))
				.setText(playHdArr[(playHd ? 0 : 1)]);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.settins_item_hd:
			// 清晰度设置
			startActivityForResult(new Intent(SettingsActivity.this,
					SettingsHdActivity.class), REQUEST_CODE_PLAY_HD);
			break;
		case R.id.seting_item_game_start_ctrl:
			// 开赛提醒设置
			startActivityForResult(new Intent(SettingsActivity.this,
					SettingsGameRemindActivity.class),
					REQUEST_CODE_GAME_START_REMIND);
			break;
		case R.id.setting_item_update:
			// 软件更新

			if (upDateIsRunning) {
				UIs.showToast("正在更新，请稍后");
			} else {
				if (LetvUtil.checkClickEvent()) {
					checkUpdateVersionInfo();
				}
			}

			break;
		case R.id.setting_item_about:
			// 关于
			startActivity(new Intent(SettingsActivity.this,
					SettingsAboutActivity.class));
			break;
		case R.id.setting_item_proposal:
			// 意见反馈
			// UMFeedbackService.openUmengFeedbackSDK(this);
			FeedBackActivity.launch(this);
			break;
		case R.id.head_back:
			finish();
			break;
		default:
			break;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE_GAME_START_REMIND) {
			setGameStartRemind();
		} else if (requestCode == REQUEST_CODE_PLAY_HD) {
			setPlayHd();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 检查升级
	 * */
	private void checkUpdateVersionInfo() {

		if (LetvUtil.hasNet()) {
			DataStatusInfo mDataStatusInfo = LetvApplication.getInstance()
					.getDataStatusInfo();

			if (mDataStatusInfo != null) {
				UpgradeInfo mUpgradeInfo = mDataStatusInfo.getUpgradeInfo();

				if (mUpgradeInfo != null
						&& UpgradeInfo.UPGRADE_YES.equals(mUpgradeInfo
								.getUpgrade())) {
					PreferencesManager.getInstance().setIsNeedUpdate(true);
					showUpdateDialog(mUpgradeInfo);
				} else {
					PreferencesManager.getInstance().setIsNeedUpdate(false);
					UIs.callDialogMsgPositiveButton(SettingsActivity.this, -1,
							R.string.dialog_messge_update_is_newest, null);
				}
			} else {
				new RequestDataStatusInfo(this).start();
			}
		} else {
			UIs.notifyLongNormal(SettingsActivity.this, R.string.toast_net_null);

		}
	}

	private void showUpdateDialog(final UpgradeInfo result) {

		String uptype = result.getUptype();
		if (uptype.equals(UpgradeInfo.UPTYPE_FORCE)) {
			UIs.call(this, result.getTitle(), result.getMsg(),
					R.string.update_update, R.string.update_exit,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();

							try {
								new UpdateDownloadAsyncTask(
										SettingsActivity.this, result)
										.execute();
							} catch (com.letv.watchball.async.UpdateDownloadAsyncTask.UpdataAppException e) {
								e.printStackTrace();
							}
						}
					}, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							// 退出应用
							CloseableManager.getInstance().closeAll();
						}
					}, false);
		} else {
			UIs.call(this, result.getTitle(), result.getMsg(),
					R.string.update_update, R.string.update_later,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();

							try {
								new UpdateDownloadAsyncTask(
										SettingsActivity.this, result)
										.execute();
							} catch (com.letv.watchball.async.UpdateDownloadAsyncTask.UpdataAppException e) {
								e.printStackTrace();
							}
						}
					}, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					}, false);
		}

		// UIs.callDialogMsgPositiveNegtivButton(SettingsActivity.this, -1,
		// String.format(getResources().getString(R.string.update_dialog_has_new_version),
		// result.getV()),
		//
		// new DialogInterface.OnClickListener() {
		// @Override
		// public void onClick(DialogInterface dialog, int which) {
		// dialog.dismiss();
		//
		// DownloadAsyncTask asyncTask;
		// try {
		// if (DownloadAsyncTask.state == DownloadAsyncTask.WAIT) {
		// asyncTask = new DownloadAsyncTask(SettingsActivity.this,
		// result.getUrl(), result.getV());
		// asyncTask.execute();
		// } else {
		// UIs.notifyShortNormal(SettingsActivity.this,
		// R.string.already_updata);
		// }
		// } catch (UpdataAppException e) {
		// e.printStackTrace();
		// }
		// }
		// }, R.string.update_update, new DialogInterface.OnClickListener() {
		// @Override
		// public void onClick(DialogInterface dialog, int which) {
		// }
		// }, R.string.update_exit);

	}

	@Override
	public void close() {

	}

	private class RequestDataStatusInfo extends LetvHttpAsyncTask<LetvBaseBean> {

		public RequestDataStatusInfo(Context context) {
			super(context, true);
		}

		@Override
		public LetvDataHull<LetvBaseBean> doInBackground() {
			try {
				DataStatusInfo result = DataStatistics.getInstance()
						.getDataStatusInfo(SettingsActivity.this,
								LetvUtil.getPcode());
				if (result != null) {

					LetvApplication.getInstance().setDataStatusInfo(result);

					if (result.getApiInfo() != null
							&& ApiInfo.APISTATUS_TEST.equals(result
									.getApiInfo().getApistatus())) {
						LetvApplication.getInstance().setUseTest(true);
						LetvHttpApi.setTest(true);
					} else {
						LetvApplication.getInstance().setUseTest(false);
						LetvHttpApi.setTest(false);
					}

					// if(result.getAdsInfo() != null &&
					// AdsInfo.ADS_OPEN.equals(result.getAdsInfo().getValue()))
					// {
					// LetvWatchBallApplication.getInstance().setShowAdvertisement(true);
					// AdsManager.getInstance().setShowAd(true);
					// } else {
					// LetvWatchBallApplication.getInstance().setShowAdvertisement(false);
					// AdsManager.getInstance().setShowAd(false);
					// }

					if (result.getRecommendInfos() != null) {

						for (String key : result.getRecommendInfos().keySet()) {

							RecommendInfo mRecommendInfo = result
									.getRecommendInfos().get(key);

							if (RecommendInfo.RECOMMEND_KEY_CHANNEL.equals(key)) {
								if (mRecommendInfo.isOpen()) {
									if (mRecommendInfo.getNum() > 0) {
										LetvApplication.getInstance()
												.setShowChannelRecommend(2);
									} else {
										LetvApplication.getInstance()
												.setShowChannelRecommend(1);
									}

								} else {
									LetvApplication.getInstance()
											.setShowChannelRecommend(0);
								}
							} else if (RecommendInfo.RECOMMEND_KEY_LIVE
									.equals(key)) {
								if (mRecommendInfo.isOpen()) {
									if (mRecommendInfo.getNum() > 0) {
										LetvApplication.getInstance()
												.setShowLiveRecommend(2);
									} else {
										LetvApplication.getInstance()
												.setShowLiveRecommend(1);
									}
								} else {
									LetvApplication.getInstance()
											.setShowLiveRecommend(0);
								}
							} else if (RecommendInfo.RECOMMEND_KEY_DOWNLOAD
									.equals(key)) {
								if (mRecommendInfo.isOpen()) {
									if (mRecommendInfo.getNum() > 0) {
										LetvApplication.getInstance()
												.setShowDownloadRecommend(2);
									} else {
										LetvApplication.getInstance()
												.setShowDownloadRecommend(1);
										;
									}
								} else {
									LetvApplication.getInstance()
											.setShowDownloadRecommend(0);
								}
							} else if (RecommendInfo.RECOMMEND_KEY_SETTING
									.equals(key)) {
								if (mRecommendInfo.isOpen()) {
									if (mRecommendInfo.getNum() > 0) {
										LetvApplication.getInstance()
												.setShowSettingRecommend(2);
									} else {
										LetvApplication.getInstance()
												.setShowSettingRecommend(1);
									}
								} else {
									LetvApplication.getInstance()
											.setShowSettingRecommend(0);
								}
							}
						}
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

		public void netErr(int updateId, String errMsg) {
			DataStatusInfo mDataStatusInfo = LetvApplication.getInstance()
					.getDataStatusInfo();

			if (mDataStatusInfo != null) {
				UpgradeInfo mUpgradeInfo = mDataStatusInfo.getUpgradeInfo();

				if (mUpgradeInfo != null
						&& UpgradeInfo.UPGRADE_YES.equals(mUpgradeInfo
								.getUpgrade())) {
					PreferencesManager.getInstance().setIsNeedUpdate(true);
					showUpdateDialog(mUpgradeInfo);
				} else {
					PreferencesManager.getInstance().setIsNeedUpdate(false);
					// UIs.callDialogMsgPositiveButton(SettingsActivity.this,
					// DialogMsgConstantId.TWENTYTWO_ZERO_ONE_CONSTANT, null);
					UIs.callDialogMsgPositiveButton(SettingsActivity.this, -1,
							R.string.update_dialog_newest_already, null);
					// UIs.call(SettingsActivity.this, R.string.already_new,
					// null);
				}
			}
		};
	}

	@Override
	public void finish() {
		super.finish();
		CloseableManager.getInstance().close(this);
		// close();
	}
}
