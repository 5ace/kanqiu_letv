package com.letv.watchball.ui;

import java.util.Calendar;

import android.os.BatteryManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.letv.watchball.R;
import com.letv.watchball.db.PreferencesManager;
import com.letv.watchball.utils.LetvUtil;
import com.letv.watchball.utils.NetWorkTypeUtils;
import com.letv.watchball.utils.UIs;

public class PlayLiveFullController extends BaseLivePlayController {

	private PlayLiveController mController;

	/**
	 * 全屏控制器
	 * */
	private View fullPlayControllerLayoutt;

	/**
	 * 全屏返回键
	 * */
	private View fullPlayControllerBack;
	/**
	 * 全屏标题
	 * */
	private TextView fullPlayControllerTitle;

	/**
	 * 全屏网络状态
	 * */
	private ImageView fullPlayControllerNet;

	/**
	 * 全屏电池状态
	 * */
	private ImageView fullPlayControllerBattery;

	/**
	 * 全屏时间
	 * */
	private TextView fullPlayControllerTime;

	/**
	 * 全屏播放
	 * */
	public ImageView fullPlayControllerPlay;

	/**
	 * 全屏声图标
	 * */
	private ImageView fullPlayControllerSoundIcon;

	/**
	 * 全屏声音进度条
	 * */
	private SeekBar fullPlayControllerSoundSeekbar;

	/**
	 * 全屏声音布局
	 * */
	private View fullPlayControllerSoundLayout;

	// /**
	// * 全屏直播节目引导按钮
	// */
	// private ImageView fullLiveProgramIntroBar;

	private View toPip;

	/**
	 * 傻逼半屏按钮
	 * */
	private View halfView;
	private View fullLayout;
	private View fullPlayControllerLowOrHigh;
	private TextView fullPlayControllerLowText;
	private TextView fullPlayControllerHighText;
	private TextView fullPlayControllerHd;

	public PlayLiveFullController(PlayLiveController mController, View root) {
		this.mController = mController;

		findFullView(root);
	}

	private void findFullView(View root) {

		fullPlayControllerLayoutt = root
				.findViewById(R.id.live_full_controller);
		fullPlayControllerBack = root.findViewById(R.id.full_back);
		fullPlayControllerBack.setOnClickListener(fullClick);
		fullPlayControllerTitle = (TextView) root.findViewById(R.id.full_title);
		fullPlayControllerNet = (ImageView) root.findViewById(R.id.full_net);
		fullPlayControllerBattery = (ImageView) root
				.findViewById(R.id.full_battery);
		fullPlayControllerTime = (TextView) root.findViewById(R.id.full_time);

		fullLayout = root.findViewById(R.id.full_layout);
		fullPlayControllerLowOrHigh = root.findViewById(R.id.full_loworhigh);
		fullPlayControllerLowText = (TextView) root
				.findViewById(R.id.full_low_text);
		fullPlayControllerHighText = (TextView) root
				.findViewById(R.id.full_high_text);
		fullPlayControllerHd = (TextView) root.findViewById(R.id.full_hd);

		fullPlayControllerPlay = (ImageView) root.findViewById(R.id.full_play);
		fullPlayControllerSoundIcon = (ImageView) root
				.findViewById(R.id.full_sound_icon);
		fullPlayControllerSoundSeekbar = (SeekBar) root
				.findViewById(R.id.full_sound_seekbar);
		fullPlayControllerSoundSeekbar
				.setOnSeekBarChangeListener(volumeSeekBarChangeListener);
		fullPlayControllerSoundLayout = root
				.findViewById(R.id.full_sound_layout);

		// fullLiveProgramIntroBar = (ImageView)
		// root.findViewById(R.id.full_liveprogram_bar);
		// fullLiveProgramIntroBar.setOnClickListener(videoBarClick);
		toPip = root.findViewById(R.id.play_pip);
		toPip.setOnClickListener(pipClick);
		halfView = root.findViewById(R.id.full_half_icon);
		initState();
	}

	private void initState() {
		halfView.setVisibility(View.VISIBLE);
		halfView.setOnClickListener(halfIconClick);
		fullPlayControllerSoundLayout.measure(0, 0);
		fullPlayControllerSoundIcon.setOnClickListener(vloumeIconClick);

		fullLayout.setOnTouchListener(shieldTouchListener);
		fullPlayControllerLowOrHigh.measure(0, 0);
		fullLayout.measure(0, 0);
		fullPlayControllerSoundLayout.measure(0, 0);

		fullPlayControllerHd.setText(mController.isHd ? PreferencesManager
				.getInstance().getPlayNormal_zh() : PreferencesManager
				.getInstance().getPlayLow_zh());
		fullPlayControllerHd.setVisibility(View.VISIBLE);

	}

	/**
	 * 屏蔽事件不向下传
	 * */
	private View.OnTouchListener shieldTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			return true;
		}
	};

	/**
	 * 初始化高清标清选项功能
	 * */
	void initHighOrLow() {
		boolean hasHigh = mController.hasHd;
		boolean hasStandard = mController.hasStandard;
		boolean isHd = PreferencesManager.getInstance().isPlayHd();
		String hdName = PreferencesManager.getInstance().getPlayNormal_zh();
		String lowName = PreferencesManager.getInstance().getPlayLow_zh();
		fullPlayControllerHd.setText(isHd ? hdName : lowName);

		if (hasHigh && hasStandard) {
			fullPlayControllerHd.setEnabled(true);

			fullPlayControllerHd.setOnClickListener(lowOrHighClick);
			fullPlayControllerHighText.setText(hdName);
			fullPlayControllerLowText.setText(lowName);
			if (isHd) {
				// fullPlayControllerLowOrHigh.setBackgroundResource(R.drawable.player_high_bg);
				fullPlayControllerHighText.setTextColor(mController
						.getActivity().getResources()
						.getColor(R.color.letv_color_ff00a0e9));
				fullPlayControllerLowText.setTextColor(mController
						.getActivity().getResources()
						.getColor(R.color.letv_color_ffadadad));
			} else {
				// fullPlayControllerLowOrHigh.setBackgroundResource(R.drawable.player_low_bg);
				fullPlayControllerLowText.setTextColor(mController
						.getActivity().getResources()
						.getColor(R.color.letv_color_ff00a0e9));
				fullPlayControllerHighText.setTextColor(mController
						.getActivity().getResources()
						.getColor(R.color.letv_color_ffadadad));
			}
			fullPlayControllerLowText.setOnClickListener(play_low_listener);
			fullPlayControllerHighText.setOnClickListener(play_high_listener);
		} else {
			fullPlayControllerHd.setEnabled(false);
		}

		if (mController.getLaunchMode() != PlayController.PLAY_DEFAULT) {
			fullPlayControllerHd.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 标清按钮点击
	 * */
	private View.OnClickListener play_low_listener = new View.OnClickListener() {
		public void onClick(View v) {
			if (null != fullLayout) {
				fullLayout.setVisibility(View.GONE);
			}
			if (mController.isHd) {
				mController.isHd = false;
				PreferencesManager.getInstance().setIsPlayHd(false);
				// fullPlayControllerLowOrHigh.setBackgroundResource(R.drawable.player_low_bg);
				fullPlayControllerLowText.setTextColor(mController
						.getActivity().getResources()
						.getColor(R.color.letv_color_ff00a0e9));
				fullPlayControllerHighText.setTextColor(mController
						.getActivity().getResources()
						.getColor(R.color.letv_color_ffadadad));
				fullPlayControllerHd.setText(fullPlayControllerLowText
						.getText());
				if (!mController.game.pay.equalsIgnoreCase("1"))
					mController.playUrl(mController.game.live_350.streamId,
							mController.game.live_350.liveUrl);
				else
					mController.playLivePayUrl(
							mController.game.live_350.streamId,
							mController.game.live_350.liveUrl, 350);

			}
		}
	};

	/**
	 * 高清按钮点击
	 * */
	private View.OnClickListener play_high_listener = new View.OnClickListener() {
		public void onClick(View v) {
			if (null != fullLayout) {
				fullLayout.setVisibility(View.GONE);
			}
			if (!mController.isHd) {
				mController.isHd = true;
				PreferencesManager.getInstance().setIsPlayHd(true);
				// fullPlayControllerLowOrHigh.setBackgroundResource(R.drawable.player_high_bg);
				fullPlayControllerHighText.setTextColor(mController
						.getActivity().getResources()
						.getColor(R.color.letv_color_ff00a0e9));
				fullPlayControllerLowText.setTextColor(mController
						.getActivity().getResources()
						.getColor(R.color.letv_color_ffadadad));
				fullPlayControllerHd.setText(fullPlayControllerHighText
						.getText());
				if (!mController.game.pay.equalsIgnoreCase("1"))
					mController.playUrl(mController.game.live_800.streamId,
							mController.game.live_800.liveUrl);
				else
					mController.playLivePayUrl(
							mController.game.live_800.streamId,
							mController.game.live_800.liveUrl, 800);

			}
		}
	};

	/**
	 * 清晰度选择按钮监听
	 * */
	private OnClickListener lowOrHighClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			startHandlerHide();
			if (fullLayout.getVisibility() == View.VISIBLE) {
				fullLayout.setVisibility(View.GONE);
			} else {
				int[] locations = new int[2];
				fullPlayControllerHd.getLocationOnScreen(locations);
				int x = locations[0];// 获取组件当前位置的横坐标
				int y = locations[1];// 获取组件当前位置的纵坐标
				RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) fullLayout
						.getLayoutParams();
				params.leftMargin = x;
				params.topMargin = y - fullLayout.getMeasuredHeight()
						- UIs.dipToPx(5);

				fullLayout.setLayoutParams(params);
				fullLayout.requestLayout();
				fullLayout.setVisibility(View.VISIBLE);

			}
		}
	};

	/**
	 * 点击半屏的监听
	 * */
	private OnClickListener halfIconClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (callBack != null)
				callBack.half();
		}
	};

	public void hideFullLiveBar() {
		// if(fullLiveProgramIntroBar != null){
		// fullLiveProgramIntroBar.setVisibility(View.GONE);
		// }
	}

	public void onProgramChanged() {
	}

	@Override
	public void show() {
		setShow(true);
		// if(!mController.isLock()){
		fullPlayControllerLayoutt.setVisibility(View.VISIBLE);
		startHandlerHide();
		// }
	}

	@Override
	public void hide() {
		setShow(false);
		fullPlayControllerLayoutt.setVisibility(View.GONE);
		stopHandlerHide();
	}

	public void hideNoState() {
		fullPlayControllerLayoutt.setVisibility(View.GONE);
		stopHandlerHide();
		if (fullPlayControllerSoundLayout.getVisibility() == View.VISIBLE) {
			fullPlayControllerSoundLayout.setVisibility(View.GONE);
		}
	}

	@Override
	public boolean clickShowAndHide() {
		if (isShow()) {
			if (fullPlayControllerLayoutt.getVisibility() == View.VISIBLE) {
				fullPlayControllerLayoutt.setVisibility(View.GONE);
				stopHandlerHide();
			} else {
				fullPlayControllerLayoutt.setVisibility(View.VISIBLE);
				startHandlerHide();
				return true;
			}
		}

		return false;
	}

	@Override
	public void clickShowAndHide(boolean isShow) {
		if (isShow()) {
			if (isShow) {
				fullPlayControllerLayoutt.setVisibility(View.VISIBLE);
				startHandlerHide();
			} else {
				fullPlayControllerLayoutt.setVisibility(View.GONE);
				stopHandlerHide();
			}
		}
	}

	@Override
	public void star() {
		fullPlayControllerPlay
				.setImageResource(R.drawable.play_controller_pause_btn);
		fullPlayControllerPlay.setOnClickListener(pauseClick);
	}

	@Override
	public void pause() {
		fullPlayControllerPlay
				.setImageResource(R.drawable.play_controller_play_btn);
		fullPlayControllerPlay.setOnClickListener(playClick);
	}

	/**
	 * 将控制器切换到不可操作状态
	 * */
	@Override
	public void Inoperable() {
		fullPlayControllerPlay.setOnClickListener(null);
		fullPlayControllerPlay
				.setImageResource(R.drawable.play_controller_play_btn_selected);
	}

	@Override
	public void format() {
		Inoperable();
		clearVideos();
		setPipEnable(true);
	}

	@Override
	public void onTimeChange() {
		if (fullPlayControllerTime.getVisibility() != View.VISIBLE) {
			fullPlayControllerTime.setVisibility(View.VISIBLE);
		}
		Calendar mCalendar = Calendar.getInstance();
		mCalendar.setTimeInMillis(System.currentTimeMillis());
		int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
		int minite = mCalendar.get(Calendar.MINUTE);
		fullPlayControllerTime.setText(LetvUtil.getStringTwo(String
				.valueOf(hour))
				+ ":"
				+ LetvUtil.getStringTwo(String.valueOf(minite)));
	}

	@Override
	public void onNetChange() {
		if (fullPlayControllerNet.getVisibility() != View.VISIBLE) {
			fullPlayControllerNet.setVisibility(View.VISIBLE);
		}
		switch (NetWorkTypeUtils.getNetType()) {
		case NetWorkTypeUtils.NETTYPE_NO:
			fullPlayControllerNet.setImageResource(R.drawable.net_no);
			break;
		case NetWorkTypeUtils.NETTYPE_WIFI:
			fullPlayControllerNet.setImageResource(R.drawable.net_wifi);
			break;
		case NetWorkTypeUtils.NETTYPE_2G:
			fullPlayControllerNet.setImageResource(R.drawable.net_2g);
			break;
		case NetWorkTypeUtils.NETTYPE_3G:
			fullPlayControllerNet.setImageResource(R.drawable.net_3g);
			break;
		default:
			break;
		}
	}

	@Override
	public void onBatteryChange(int curStatus, int curPower) {
		if (fullPlayControllerBattery.getVisibility() != View.VISIBLE) {
			fullPlayControllerBattery.setVisibility(View.VISIBLE);
		}
		boolean isCharging = false;

		switch (curStatus) {
		case BatteryManager.BATTERY_STATUS_CHARGING:// 充电状态
			isCharging = true;
			break;
		case BatteryManager.BATTERY_STATUS_DISCHARGING:// 放电状态
			break;
		case BatteryManager.BATTERY_STATUS_NOT_CHARGING:// 未充电
			break;
		case BatteryManager.BATTERY_STATUS_FULL:// 充满电
			break;
		case BatteryManager.BATTERY_STATUS_UNKNOWN:// 未知状态
			break;
		default:
			break;
		}

		if (isCharging) {
			fullPlayControllerBattery
					.setImageResource(R.drawable.battery_charge);
		} else {
			if (curPower >= 80) {
				fullPlayControllerBattery.setImageResource(R.drawable.battery5);
			} else if (curPower >= 60) {
				fullPlayControllerBattery.setImageResource(R.drawable.battery4);
			} else if (curPower >= 40) {
				fullPlayControllerBattery.setImageResource(R.drawable.battery3);
			} else if (curPower >= 20) {
				fullPlayControllerBattery.setImageResource(R.drawable.battery2);
			} else if (curPower >= 0) {
				fullPlayControllerBattery.setImageResource(R.drawable.battery1);
			}
		}
	}

	@Override
	public void onVolumeChange(int max, int progress) {
		fullPlayControllerSoundSeekbar.setMax(max);
		fullPlayControllerSoundSeekbar.setProgress(progress);
		changeSoundState(progress, max);

	}

	@Override
	public void onVolumeChange(int max, int progress, boolean isUp) {
		fullPlayControllerSoundSeekbar.setMax(max);
		int pTmp = fullPlayControllerSoundSeekbar.getProgress();
		if (isUp) {
			if (pTmp == progress) {
				progress += 1;
			} else if (pTmp > progress) {
				progress = pTmp + 1;
			}
		} else {
			if (pTmp == progress) {
				progress -= 1;
			} else if (pTmp < progress) {
				progress = pTmp + 1;
			}
		}
		fullPlayControllerSoundSeekbar.setProgress(progress);
		changeSoundState(progress, max);
	}

	@Override
	public void setTitle(String title) {
		fullPlayControllerTitle.setText(title);
	}

	/**
	 * 点击暂停的监听
	 * */
	private OnClickListener pauseClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			startHandlerHide();
			if (callBack != null)
				callBack.pause();
		}
	};

	/**
	 * 点击播放的监听
	 * */
	private OnClickListener playClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			startHandlerHide();
			if (callBack != null)
				callBack.star();
		}
	};

	/**
	 * 点击全屏的监听
	 * */
	private OnClickListener fullClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			startHandlerHide();
			if (callBack != null)
				callBack.half();
		}
	};

	/**
	 * 点击全屏的监听
	 * */
	private OnClickListener pipClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			startHandlerHide();
			if (callBack != null)
				callBack.toPip();
		}
	};
	private Handler handler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			hideNoState();
			return false;
		}
	});

	private void startHandlerHide() {
		if (isShow()) {
			handler.removeMessages(1);
			handler.sendEmptyMessageDelayed(1, 3000);
		}
	}

	/**
	 * 移除自动隐藏
	 * */
	private void stopHandlerHide() {
		handler.removeMessages(1);
	}

	/**
	 * 清除视频列表数据
	 * */
	public void clearVideos() {

	}

	/**
	 * 改变声音按钮状态
	 * */
	public void changeSoundState(int value, int maxValue) {
		if (fullPlayControllerSoundIcon != null) {
			if (value >= maxValue / 3 * 2) {
				fullPlayControllerSoundIcon
						.setImageResource(R.drawable.sound_three);
			} else if (value >= maxValue / 3) {
				fullPlayControllerSoundIcon
						.setImageResource(R.drawable.sound_two);
			} else if (value > 0) {
				fullPlayControllerSoundIcon
						.setImageResource(R.drawable.sound_one);
			} else {
				fullPlayControllerSoundIcon
						.setImageResource(R.drawable.sound_zero);
			}
		}
	}

	/**
	 * 声音进度条变化的监听
	 * */
	private OnSeekBarChangeListener volumeSeekBarChangeListener = new OnSeekBarChangeListener() {

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			startHandlerHide();
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			stopHandlerHide();
		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			startHandlerHide();
			int max = seekBar.getMax();
			int volumeMax = mController.setSoundVolume(progress, false);
			changeSoundState(progress, max);
			if (max != volumeMax) {
				if (progress <= max) {
					seekBar.setMax(volumeMax);
				} else {
					seekBar.setProgress(max);
				}

			}
		}
	};

	/**
	 * 点击声音的监听
	 * */
	private OnClickListener vloumeIconClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (fullPlayControllerSoundLayout.getVisibility() != View.VISIBLE) {

				int[] locations = new int[2];
				fullPlayControllerSoundIcon.getLocationOnScreen(locations);
				int x = locations[0];// 获取组件当前位置的横坐标
				int y = locations[1];// 获取组件当前位置的纵坐标
				RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) fullPlayControllerSoundLayout
						.getLayoutParams();
				params.leftMargin = x;
				params.topMargin = y
						- fullPlayControllerSoundLayout.getMeasuredHeight();
				// params.addRule(RelativeLayout.ALIGN_LEFT,
				// R.id.full_sound_icon);
				fullPlayControllerSoundLayout.setLayoutParams(params);
				fullPlayControllerSoundLayout.requestLayout();
				fullPlayControllerSoundLayout.setVisibility(View.VISIBLE);
			} else {
				fullPlayControllerSoundLayout.setVisibility(View.INVISIBLE);
			}
		}
	};

	public void setPipEnable(boolean enable) {
		if (toPip != null) {
			toPip.setEnabled(enable);
		}
	}
}
