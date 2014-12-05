package com.letv.watchball.ui;

import java.util.Calendar;

import android.os.BatteryManager;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.letv.watchball.R;
import com.letv.watchball.bean.AlbumNew;
import com.letv.watchball.bean.Video;
import com.letv.watchball.db.PreferencesManager;
import com.letv.watchball.utils.LetvUtil;
import com.letv.watchball.utils.NetWorkTypeUtils;
import com.letv.watchball.utils.UIs;

public class PlayAlbumFullController extends BasePlayController {

	PlayAlbumController playAlbumController;

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
	 * 全屏下边布局
	 * */
	private View fullPlayControllerBottom;

	/**
	 * 全屏高清标清切换
	 * */
	private View fullPlayControllerLowOrHigh;

	/**
	 * 全屏标清文案
	 * */
	private TextView fullPlayControllerLowText;

	/**
	 * 全屏高清文案
	 * */
	private TextView fullPlayControllerHighText;

	/**
	 * 全屏超清文案
	 */
	private TextView fullPlayController1080p;
	/**
	 * 全屏清晰度切换 布局
	 * */
	private View fullLayout;

	/**
	 * 全屏切换清晰度按钮
	 * */
	private TextView fullPlayControllerHd;

	/**
	 * 全屏快进
	 * */
	private View fullPlayControllerForward;

	/**
	 * 全屏快退
	 * */
	private View fullPlayControllerRewind;

	/**
	 * 全屏播放
	 * */
	public ImageView fullPlayControllerPlay;

	/**
	 * 全屏进度条
	 * */
	private SeekBar fullPlayControllerSeekbar;

	/**
	 * 全屏片头打点
	 * */
	private View fullPlayControllerBegin;

	/**
	 * 全屏片尾打点
	 * */
	private View fullPlayControllerEnd;

	/**
	 * 全屏声音布局
	 * */
	private View fullPlayControllerSoundLayout;

	/**
	 * 全屏声图标
	 * */
	private ImageView fullPlayControllerSoundIcon;

	/**
	 * 全屏声音进度条
	 * */
	private SeekBar fullPlayControllerSoundSeekbar;

	/**
	 * 全屏播放进度文字
	 * */
	private TextView fullPlayControllerProgressText;
	/**
	 * 切换至画中画
	 */
	private View toPip;

	/**
	 * 傻逼半屏按钮
	 * */
	private View halfView;

	private Handler handler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			hideNoState();
			return false;
		}
	});

	public PlayAlbumFullController(PlayAlbumController playAlbumController,
			View root) {
		this.playAlbumController = playAlbumController;
		findFullView(root);
	}

	/**
	 * 初始化全屏播放器控件
	 * */
	private void findFullView(View root) {
		fullPlayControllerLayoutt = root
				.findViewById(R.id.detailplay_full_controller);
		fullPlayControllerBack = root.findViewById(R.id.full_back);
		fullPlayControllerTitle = (TextView) root.findViewById(R.id.full_title);
		fullPlayControllerNet = (ImageView) root.findViewById(R.id.full_net);
		fullPlayControllerBattery = (ImageView) root
				.findViewById(R.id.full_battery);
		fullPlayControllerTime = (TextView) root.findViewById(R.id.full_time);
		fullPlayControllerLowOrHigh = root.findViewById(R.id.full_loworhigh);
		fullPlayControllerLowText = (TextView) root
				.findViewById(R.id.full_low_text);
		fullPlayControllerHighText = (TextView) root
				.findViewById(R.id.full_high_text);
		fullPlayController1080p = (TextView) root.findViewById(R.id.full_1080);
		fullPlayControllerHd = (TextView) root.findViewById(R.id.full_hd);
		fullLayout = root.findViewById(R.id.full_layout);
		fullPlayControllerForward = root.findViewById(R.id.full_forward);
		fullPlayControllerRewind = root.findViewById(R.id.full_rewind);
		fullPlayControllerPlay = (ImageView) root.findViewById(R.id.full_play);
		fullPlayControllerSeekbar = (SeekBar) root
				.findViewById(R.id.full_play_seekbar);
		fullPlayControllerBegin = root.findViewById(R.id.full_play_skip_begin);
		fullPlayControllerEnd = root.findViewById(R.id.full_play_skip_end);
		fullPlayControllerSoundLayout = root
				.findViewById(R.id.full_sound_layout);
		fullPlayControllerSoundIcon = (ImageView) root
				.findViewById(R.id.full_sound_icon);
		fullPlayControllerSoundSeekbar = (SeekBar) root
				.findViewById(R.id.full_sound_seekbar);
		fullPlayControllerProgressText = (TextView) root
				.findViewById(R.id.full_progress_text);
		fullPlayControllerBottom = root.findViewById(R.id.full_bottom);
		toPip = root.findViewById(R.id.play_pip);
		halfView = root.findViewById(R.id.full_half_icon);
		initState();
		onTimeChange();
	}

	private void initState() {
		switch (playAlbumController.getLaunchMode()) {
		case PlayController.PLAY_ALBUM:
			halfView.setVisibility(View.VISIBLE);
			halfView.setOnClickListener(halfIconClick);
			fullPlayControllerBack.setOnClickListener(fullClick);
			fullLayout.setOnTouchListener(shieldTouchListener);
			fullPlayControllerLowOrHigh.measure(0, 0);
			fullLayout.measure(0, 0);
			fullPlayControllerSoundLayout.measure(0, 0);
			switch (playAlbumController.isHd) {
			case 1:
				fullPlayControllerHd.setText(PreferencesManager.getInstance()
						.getPlayNormal_zh());
				break;
			case 0:
				fullPlayControllerHd.setText(PreferencesManager.getInstance()
						.getPlayLow_zh());
				break;
			case 2:
				fullPlayControllerHd.setText(PreferencesManager.getInstance()
						.getPlayHigh_zh());
			}
			fullPlayControllerHd.setVisibility(View.VISIBLE);
			fullPlayControllerBack.setOnClickListener(fullClick);
			fullPlayControllerSoundSeekbar
					.setOnSeekBarChangeListener(volumeSeekBarChangeListener);
			fullLayout.setOnTouchListener(shieldTouchListener);
			fullPlayControllerBottom.setOnTouchListener(shieldTouchListener);
			fullPlayControllerSeekbar.setEnabled(false);
			toPip.setOnClickListener(pipClick);
			fullPlayControllerSoundIcon.setOnClickListener(vloumeIconClick);
			break;
		case PlayController.PLAY_VIDEO:

			halfView.setVisibility(View.GONE);
			fullPlayControllerBack.setOnClickListener(fullClick);
			// fullPlayControllerBack.setOnClickListener(backClick);
			fullLayout.setOnTouchListener(shieldTouchListener);
			fullPlayControllerLowOrHigh.measure(0, 0);
			fullLayout.measure(0, 0);
			fullPlayControllerSoundLayout.measure(0, 0);
			switch (playAlbumController.isHd) {
			case 1:
				fullPlayControllerHd.setText(PreferencesManager.getInstance()
						.getPlayNormal_zh());
				break;
			case 0:
				fullPlayControllerHd.setText(PreferencesManager.getInstance()
						.getPlayLow_zh());
				break;
			case 2:
				fullPlayControllerHd.setText(PreferencesManager.getInstance()
						.getPlayHigh_zh());
			}

			fullPlayControllerHd.setVisibility(View.VISIBLE);

			fullPlayControllerSoundSeekbar
					.setOnSeekBarChangeListener(volumeSeekBarChangeListener);
			fullLayout.setOnTouchListener(shieldTouchListener);
			fullPlayControllerBottom.setOnTouchListener(shieldTouchListener);
			fullPlayControllerSeekbar.setEnabled(false);
			toPip.setOnClickListener(pipClick);
			fullPlayControllerSoundIcon.setOnClickListener(vloumeIconClick);
			break;
		case PlayController.PLAY_DEFAULT:
			fullPlayControllerHd.setVisibility(View.GONE);
			halfView.setVisibility(View.GONE);
			fullPlayControllerBack.setOnClickListener(backClick);
			fullPlayControllerSoundSeekbar
					.setOnSeekBarChangeListener(volumeSeekBarChangeListener);
			fullPlayControllerBottom.setOnTouchListener(shieldTouchListener);
			fullPlayControllerSeekbar.setEnabled(false);
			toPip.setOnClickListener(pipClick);
			fullPlayControllerSoundIcon.setOnClickListener(vloumeIconClick);
			break;
		}

	}

	@Override
	public void show() {
		setShow(true);
		// if(!playAlbumController.isLock()){
		fullPlayControllerLayoutt.setVisibility(View.VISIBLE);
		startHandlerHide();
		// }
	}

	@Override
	public void hide() {
		setShow(false);
		fullPlayControllerLayoutt.setVisibility(View.GONE);
		stopHandlerHide();
		if (fullLayout.getVisibility() == View.VISIBLE) {
			fullLayout.setVisibility(View.GONE);
		}
	}

	public void hideNoState() {
		fullPlayControllerLayoutt.setVisibility(View.GONE);
		stopHandlerHide();
		if (fullLayout.getVisibility() == View.VISIBLE) {
			fullLayout.setVisibility(View.GONE);
		}
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
				if (fullLayout.getVisibility() == View.VISIBLE) {
					fullLayout.setVisibility(View.GONE);
				}
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
	public void initProgress(int max, int progress, int buffer) {
		fullPlayControllerSeekbar.setMax(max);
		fullPlayControllerSeekbar.setProgress(progress);
		fullPlayControllerSeekbar.setSecondaryProgress(buffer);
		fullPlayControllerProgressText.setText(LetvUtil
				.stringForTime(progress * 1000)
				+ " / "
				+ LetvUtil.stringForTime(max * 1000));

		updateSkipState();
		initHighOrLow();

		fullPlayControllerSeekbar
				.setOnSeekBarChangeListener(playSeekBarChangeListener);

		fullPlayControllerSeekbar.setEnabled(true);
	}

	@Override
	public void updateProgress(int progress, int buffer) {
		fullPlayControllerSeekbar.setProgress(progress);
		fullPlayControllerSeekbar.setSecondaryProgress(buffer);
	}

	@Override
	public void star() {
		fullPlayControllerPlay
				.setImageResource(R.drawable.play_controller_pause_btn);
		fullPlayControllerPlay.setOnClickListener(pauseClick);
		fullPlayControllerForward.setOnClickListener(forwardClick);
		fullPlayControllerRewind.setOnClickListener(rewindClick);
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
		fullPlayControllerForward.setOnClickListener(null);
		fullPlayControllerRewind.setOnClickListener(null);
		fullPlayControllerBegin.setVisibility(View.GONE);
		fullPlayControllerEnd.setVisibility(View.GONE);
		fullPlayControllerHd.setOnClickListener(null);
		fullPlayControllerSeekbar.setOnSeekBarChangeListener(null);
		fullPlayControllerSeekbar.setEnabled(false);
		fullPlayControllerSeekbar.setProgress(0);
		fullPlayControllerSeekbar.setSecondaryProgress(0);
	}

	@Override
	public void format() {
		Inoperable();
		// IntroductionBuilder.clear(fullPlayControllerRightContentIntroduction);
		clearVideos();
	}

	/**
	 * 屏蔽事件不向下传
	 * */
	private OnTouchListener shieldTouchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			return true;
		}
	};

	/**
	 * 进度条变化的监听
	 * */
	private OnSeekBarChangeListener playSeekBarChangeListener = new OnSeekBarChangeListener() {

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			startHandlerHide();
			if (callBack != null)
				callBack.seekFinish(seekBar.getProgress());
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			stopHandlerHide();
		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			fullPlayControllerProgressText.setText(LetvUtil
					.stringForTime(progress * 1000)
					+ " / "
					+ LetvUtil.stringForTime(seekBar.getMax() * 1000));
		}
	};

	public void soundshow() {
		int[] locations = new int[2];
		fullPlayControllerSoundIcon.getLocationOnScreen(locations);
		int x = locations[0];// 获取组件当前位置的横坐标
		int y = locations[1];// 获取组件当前位置的纵坐标
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) fullPlayControllerSoundLayout
				.getLayoutParams();
		params.leftMargin = x;
		params.topMargin = y
				- fullPlayControllerSoundLayout.getMeasuredHeight();

		fullPlayControllerSoundLayout.setLayoutParams(params);
		fullPlayControllerSoundLayout.requestLayout();
		fullPlayControllerSoundLayout.setVisibility(View.VISIBLE);
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
			int volumeMax = playAlbumController.setSoundVolume(progress, false);
			changeSoundState(progress, max);
			if (max != volumeMax) {
				if (progress <= max) {
					seekBar.setMax(volumeMax);
				} else {
					seekBar.setProgress(max);
				}

			}
			// 把看球音量控制bug修复了
			// show();
			// soundshow();
		}
	};

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
	 * 点击返回的监听
	 * */
	private OnClickListener fullClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (callBack != null) {
				if (UIs.isLandscape(playAlbumController.getActivity())
						&& playAlbumController.getLaunchMode() == playAlbumController.PLAY_ALBUM) {
					callBack.half();
				} else {
					callBack.back();
				}
			}
		}
	};

	/**
	 * 点击下载的监听
	 * */
	private OnClickListener backClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// startHandlerHide();
			if (callBack != null)
				callBack.half();
		}
	};

	/**
	 * 点击快进的监听
	 * */
	private OnClickListener forwardClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			startHandlerHide();
			if (callBack != null)
				callBack.forward();
		}
	};

	/**
	 * 点击快退的监听
	 * */
	private OnClickListener rewindClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			startHandlerHide();
			if (callBack != null)
				callBack.rewind();
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

	@Override
	public void initIntroduction() {
		// fullPlayControllerRight.setVisibility(View.VISIBLE);
		// fullPlayControllerRightBar.setVisibility(View.VISIBLE);
		// fullPlayControllerRightBarIntroduction.setVisibility(View.VISIBLE);
		// AlbumNew album = playAlbumController.getAlbum();
		// IntroductionBuilder.build(album,
		// fullPlayControllerRightContentIntroduction);
		//
		// fullPlayControllerRightBarIntroduction.setOnClickListener(introductionBarClick);
	}

	/**
	 * 清除视频列表数据
	 * */
	public void clearVideos() {

	}

	/**
	 * 打上片头和片尾的点
	 * */
	private void updateSkipState() {
		if (fullPlayControllerBegin != null && fullPlayControllerEnd != null) {
			if (PreferencesManager.getInstance().isSkip()) {
				long btime = playAlbumController.bTime;
				long etime = playAlbumController.eTime;

				if (btime > 0) {
					int totalWidth = UIs.getScreenWidth();
					int position = (int) (btime * totalWidth * 1.0 / playAlbumController.playRecord
							.getTotalDuration());
					RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) fullPlayControllerBegin
							.getLayoutParams();
					params.leftMargin = position;
					fullPlayControllerBegin.setLayoutParams(params);
					fullPlayControllerBegin.setVisibility(View.VISIBLE);
				} else {
					fullPlayControllerBegin.setVisibility(View.GONE);
				}
				if (etime > 0) {
					int totalWidth = UIs.getScreenWidth();
					int position = (int) (etime * totalWidth * 1.0 / playAlbumController.playRecord
							.getTotalDuration());
					RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) fullPlayControllerEnd
							.getLayoutParams();
					params.leftMargin = position;
					fullPlayControllerEnd.setLayoutParams(params);
					fullPlayControllerEnd.setVisibility(View.VISIBLE);
				} else {
					fullPlayControllerEnd.setVisibility(View.GONE);
				}
			} else {
				fullPlayControllerBegin.setVisibility(View.GONE);
				fullPlayControllerEnd.setVisibility(View.GONE);
			}
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
			if (playAlbumController.isHd != 0) {
				playAlbumController.isHd = 0;
				PreferencesManager.getInstance().setIsPlayHd(0);
				// fullPlayControllerLowOrHigh.setBackgroundResource(R.drawable.player_low_bg);
				fullPlayControllerLowText.setTextColor(playAlbumController
						.getActivity().getResources()
						.getColor(R.color.letv_color_ff00a0e9));
				fullPlayControllerHighText.setTextColor(playAlbumController
						.getActivity().getResources()
						.getColor(R.color.letv_color_ffadadad));
				fullPlayController1080p.setTextColor(playAlbumController
						.getActivity().getResources()
						.getColor(R.color.letv_color_ffadadad));

				fullPlayControllerHd.setText(fullPlayControllerLowText
						.getText());

				playAlbumController.play();
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
			if (playAlbumController.isHd != 1) {
				playAlbumController.isHd = 1;
				PreferencesManager.getInstance().setIsPlayHd(1);
				// fullPlayControllerLowOrHigh.setBackgroundResource(R.drawable.player_high_bg);
				fullPlayControllerHighText.setTextColor(playAlbumController
						.getActivity().getResources()
						.getColor(R.color.letv_color_ff00a0e9));
				fullPlayControllerLowText.setTextColor(playAlbumController
						.getActivity().getResources()
						.getColor(R.color.letv_color_ffadadad));
				fullPlayController1080p.setTextColor(playAlbumController
						.getActivity().getResources()
						.getColor(R.color.letv_color_ffadadad));

				fullPlayControllerHd.setText(fullPlayControllerHighText
						.getText());

				playAlbumController.play();
			}
		}
	};

	/**
	 * 超清按钮点击
	 * */
	private View.OnClickListener play_1080p_listener = new View.OnClickListener() {
		public void onClick(View v) {
			if (null != fullLayout) {
				fullLayout.setVisibility(View.GONE);
			}
			if (playAlbumController.isHd != 2) {
				playAlbumController.isHd = 2;
				PreferencesManager.getInstance().setIsPlayHd(2);
				// fullPlayControllerLowOrHigh.setBackgroundResource(R.drawable.player_high_bg);
				fullPlayController1080p.setTextColor(playAlbumController
						.getActivity().getResources()
						.getColor(R.color.letv_color_ff00a0e9));
				fullPlayControllerLowText.setTextColor(playAlbumController
						.getActivity().getResources()
						.getColor(R.color.letv_color_ffadadad));
				fullPlayControllerHighText.setTextColor(playAlbumController
						.getActivity().getResources()
						.getColor(R.color.letv_color_ffadadad));

				fullPlayControllerHd.setText(fullPlayControllerHighText
						.getText());

				playAlbumController.play();
			}
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

	/**
	 * 点击全屏的监听
	 * */
	private OnClickListener vloumeIconClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (fullPlayControllerSoundLayout.getVisibility() != View.VISIBLE) {

				soundshow();
			} else {
				fullPlayControllerSoundLayout.setVisibility(View.INVISIBLE);
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

	/**
	 * 初始化高清标清选项功能
	 * */
	private void initHighOrLow() {
		boolean hasHigh = playAlbumController.hasHd;
		boolean hasStandard = playAlbumController.hasStandard;
		int isHd = playAlbumController.isHd;
		String hdName = PreferencesManager.getInstance().getPlayNormal_zh();
		String lowName = PreferencesManager.getInstance().getPlayLow_zh();
		String highName = PreferencesManager.getInstance().getPlayHigh_zh();
		switch (isHd) {
		case 0:
			fullPlayControllerHd.setText(lowName);
			break;
		case 1:
			fullPlayControllerHd.setText(hdName);
			break;
		case 2:
			fullPlayControllerHd.setText(highName);
		}

		if (hasHigh && hasStandard) {
			fullPlayControllerHd.setEnabled(true);

			fullPlayControllerHd.setOnClickListener(lowOrHighClick);
			fullPlayControllerHighText.setText(hdName);
			fullPlayControllerLowText.setText(lowName);
			switch (isHd) {
			case 1:
				// fullPlayControllerLowOrHigh.setBackgroundResource(R.drawable.player_high_bg);
				fullPlayControllerHighText.setTextColor(playAlbumController
						.getActivity().getResources()
						.getColor(R.color.letv_color_ff00a0e9));
				fullPlayControllerLowText.setTextColor(playAlbumController
						.getActivity().getResources()
						.getColor(R.color.letv_color_ffadadad));
				fullPlayController1080p.setTextColor(playAlbumController
						.getActivity().getResources()
						.getColor(R.color.letv_color_ffadadad));
				break;
			case 0:
				// fullPlayControllerLowOrHigh.setBackgroundResource(R.drawable.player_low_bg);
				fullPlayControllerLowText.setTextColor(playAlbumController
						.getActivity().getResources()
						.getColor(R.color.letv_color_ff00a0e9));
				fullPlayControllerHighText.setTextColor(playAlbumController
						.getActivity().getResources()
						.getColor(R.color.letv_color_ffadadad));
				fullPlayController1080p.setTextColor(playAlbumController
						.getActivity().getResources()
						.getColor(R.color.letv_color_ffadadad));
				break;
			case 2:
				fullPlayControllerLowText.setTextColor(playAlbumController
						.getActivity().getResources()
						.getColor(R.color.letv_color_ffadadad));
				fullPlayControllerHighText.setTextColor(playAlbumController
						.getActivity().getResources()
						.getColor(R.color.letv_color_ffadadad));
				fullPlayController1080p.setTextColor(playAlbumController
						.getActivity().getResources()
						.getColor(R.color.letv_color_ff00a0e9));
			}
			fullPlayControllerLowText.setOnClickListener(play_low_listener);
			fullPlayControllerHighText.setOnClickListener(play_high_listener);
			fullPlayController1080p.setOnClickListener(play_1080p_listener);
		} else {
			fullPlayControllerHd.setEnabled(false);
		}

		if (playAlbumController.getLaunchMode() != PlayController.PLAY_DEFAULT) {
			fullPlayControllerHd.setVisibility(View.VISIBLE);
		}
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

	/**
	 * 电池状态
	 * */
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

	@Override
	public void onVolumeChange(int max, int progress) {
		fullPlayControllerSoundSeekbar.setMax(max);
		fullPlayControllerSoundSeekbar.setProgress(progress);
		changeSoundState(progress, max);
	}

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

	@Override
	public void setTitle(String title) {
		fullPlayControllerTitle.setText(title);
	}

	@Override
	public void videoChange(AlbumNew album, Video video) {
		if (video == null) {
			fullPlayControllerTitle.setText(null);
			return;
		} else {
			fullPlayControllerTitle.setText(video.getNameCn());

		}

	}
}
