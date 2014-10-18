package com.letv.watchball.ui;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.letv.watchball.R;
import com.letv.watchball.bean.AlbumNew;
import com.letv.watchball.bean.Video;
import com.letv.watchball.db.PreferencesManager;
import com.letv.watchball.utils.UIs;

public class PlayAlbumHalfController extends BasePlayController {

	private PlayAlbumController playAlbumController;

	/**
	 * 半屏进度布局
	 * */
	private View halfPlayProgressLayout;

	/**
	 * 半屏控制条
	 * */
	private View halfPlayControllerLayout;

	/**
	 * 半屏进度跳(可操作)
	 * */
	private SeekBar halfPlaySeekBer;
	/**
	 * 半屏进度跳(不可操作)
	 * */
	private ProgressBar halfPlayProgressBar;

	/**
	 * 半屏进度跳(可操作)布局
	 * */
	private View halfPlaySeekBerLayout;
	/**
	 * 半屏进度跳(不可操作)布局
	 * */
	private View halfPlayProgressBarLayout;

	/**
	 * 半屏顶起进度条的view
	 * */
	private View halfPlaySupport;

	/**
	 * 半屏控制 返回
	 * */
	private View halfPlayControllerBack;

	/**
	 * 半屏控制 播放
	 * */
	public ImageView halfPlayControllerPlay;

	/**
	 * 半屏控制 全屏
	 * */
	private ImageView halfPlayControllerFull;

	/**
	 * 片头打点 SeekBer上的
	 * */
	private View halfPlaySeekBerBegin;
	/**
	 * 片尾打点 SeekBer上的
	 * */
	private View halfPlaySeekBerEnd;

	/**
	 * 片头打点 Progress上的
	 * */
	private View halfPlayProgressBarEnd;

	/**
	 * 片尾打点 Progress上的
	 * */
	private View halfPlayProgressBarBegin;
	
	/**
	 * 播放布局
	 * */
	private View halfPlayLayout;
	
	/**
	 * 倒计时关闭控制条
	 * */
	private Handler handler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			clickShowAndHide() ;
			return false;
		}
	});

	public PlayAlbumHalfController(PlayAlbumController playAlbumController, View root) {
		findView(root);
		this.playAlbumController = playAlbumController;
	}

	private void findView(View root) {

		halfPlayProgressLayout = root.findViewById(R.id.detailplay_half_progress);
		halfPlayControllerLayout = root.findViewById(R.id.detailplay_half_controller);
		halfPlayLayout = root.findViewById(R.id.detailplay_half_controller_play);

		halfPlaySeekBer = (SeekBar) root.findViewById(R.id.play_seekbar);
		halfPlayProgressBar = (ProgressBar) root.findViewById(R.id.play_progressbar);
		halfPlaySeekBerLayout = root.findViewById(R.id.play_seekbar_layout);
		halfPlayProgressBarLayout = root.findViewById(R.id.play_progressbar_layout);
		halfPlaySupport = root.findViewById(R.id.play_support);
		halfPlayControllerBack = root.findViewById(R.id.controller_back);
		halfPlayControllerPlay = (ImageView) root.findViewById(R.id.controller_play);
		halfPlayControllerFull = (ImageView) root.findViewById(R.id.controller_full);
		halfPlayProgressLayout = root.findViewById(R.id.detailplay_half_progress);
		halfPlaySeekBerBegin = root.findViewById(R.id.play_seekbar_skip_begin);
		halfPlaySeekBerEnd = root.findViewById(R.id.play_seekbar_skip_end);
		halfPlayProgressBarEnd = root.findViewById(R.id.play_progressbar_skip_begin);
		halfPlayProgressBarBegin = root.findViewById(R.id.play_progressbar_skip_end);

		halfPlayControllerBack.setOnClickListener(backClick);
		halfPlayControllerFull.setOnClickListener(fullClick);
		halfPlayLayout.setVisibility(View.VISIBLE);
		
		halfPlaySeekBer.setEnabled(false);
		halfPlayProgressBar.setEnabled(false);
	}

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
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			halfPlayProgressBar.setProgress(progress);
		}
	};

	/**
	 * 点击暂停的监听
	 * */
	private OnClickListener pauseClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
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
			if (callBack != null)
				callBack.full();
		}
	};


	/**
	 * 点击下载的监听
	 * */
	private OnClickListener backClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (callBack != null)
				callBack.back();
		}
	};
	

	@Override
	public void show() {
		setShow(true);
		halfPlayControllerLayout.setVisibility(View.VISIBLE);
		halfPlayProgressLayout.setVisibility(View.VISIBLE);
	}

	@Override
	public void hide() {
		setShow(false);
		halfPlayControllerLayout.setVisibility(View.GONE);
		halfPlayProgressLayout.setVisibility(View.GONE);
	}

	@Override
	public boolean clickShowAndHide() {
		if(halfPlayControllerLayout.getVisibility() == View.VISIBLE){
			if(halfPlaySupport.getVisibility() == View.VISIBLE){
				halfPlaySupport.setVisibility(View.GONE);
				halfPlaySeekBerLayout.setVisibility(View.GONE);
				halfPlayProgressBarLayout.setVisibility(View.VISIBLE);
				stopHandlerHide();
			} else {
				halfPlaySupport.setVisibility(View.VISIBLE);
				halfPlaySeekBerLayout.setVisibility(View.VISIBLE);
				halfPlayProgressBarLayout.setVisibility(View.GONE);
				startHandlerHide();
				
				return true ;
			}
		}
		
		return false ;
	}
	

	@Override
	public void clickShowAndHide(boolean isShow) {
		if(isShow()){
			if(isShow){
				halfPlaySupport.setVisibility(View.VISIBLE);
				halfPlaySeekBerLayout.setVisibility(View.VISIBLE);
				halfPlayProgressBarLayout.setVisibility(View.GONE);
				startHandlerHide();
			} else {
				halfPlaySupport.setVisibility(View.GONE);
				halfPlaySeekBerLayout.setVisibility(View.GONE);
				halfPlayProgressBarLayout.setVisibility(View.VISIBLE);
				stopHandlerHide();
			}
		}
	}
	
	@Override
	public void initProgress(int max, int progress, int buffer) {
		halfPlayProgressBar.setMax(max);
		halfPlayProgressBar.setProgress(progress);
		halfPlayProgressBar.setSecondaryProgress(buffer);
		halfPlaySeekBer.setMax(max);
		halfPlaySeekBer.setProgress(progress);
		halfPlaySeekBer.setSecondaryProgress(buffer);

		halfPlaySeekBerLayout.setVisibility(View.GONE);
		halfPlayProgressBarLayout.setVisibility(View.VISIBLE);
		
		updateSkipState();

		halfPlaySeekBer.setOnSeekBarChangeListener(playSeekBarChangeListener);
		halfPlaySeekBer.setEnabled(true);
	}

	@Override
	public void updateProgress(int progress, int buffer) {
		halfPlaySeekBer.setProgress(progress);
		halfPlaySeekBer.setSecondaryProgress(buffer);
	}

	@Override
	public void star() {
		halfPlayControllerPlay.setImageResource(R.drawable.pause_selecter);
		halfPlayControllerPlay.setOnClickListener(pauseClick);
	}

	@Override
	public void pause() {
		halfPlayControllerPlay.setImageResource(R.drawable.play_selecter);
		halfPlayControllerPlay.setOnClickListener(playClick);
	}

	@Override
	public void Inoperable() {
		halfPlayControllerPlay.setImageResource(R.drawable.play_inoperable);
		halfPlayControllerPlay.setOnClickListener(null);
		halfPlaySeekBer.setOnSeekBarChangeListener(null);
		halfPlaySeekBer.setProgress(0);
		halfPlaySeekBer.setSecondaryProgress(0);
		halfPlayProgressBar.setProgress(0);
		halfPlaySeekBer.setEnabled(false);
		halfPlayProgressBarBegin.setVisibility(View.GONE);
		halfPlaySeekBerBegin.setVisibility(View.GONE);
		halfPlaySeekBerEnd.setVisibility(View.GONE);
		halfPlayProgressBarEnd.setVisibility(View.GONE);
	}

	@Override
	public void format() {
		Inoperable();
	}

	@Override
	public void initIntroduction() {
	}

	/**
	 * 打上片头和片尾的点
	 * */
	private void updateSkipState() {
		if (halfPlaySeekBerBegin != null && halfPlaySeekBerEnd != null && halfPlayProgressBarBegin != null && halfPlayProgressBarEnd != null) {
			if (PreferencesManager.getInstance().isSkip()) {
				long btime = playAlbumController.bTime ;
				long etime = playAlbumController.eTime ;
				if (btime > 0) {
					int totalWidth = UIs.getScreenWidth();
					int position = (int) (btime * totalWidth * 1.0 / playAlbumController.playRecord.getTotalDuration());
					RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) halfPlaySeekBerBegin.getLayoutParams();
					params.leftMargin = position;
					halfPlaySeekBerBegin.setLayoutParams(params);
					halfPlaySeekBerBegin.setVisibility(View.VISIBLE);

					params = (RelativeLayout.LayoutParams) halfPlayProgressBarBegin.getLayoutParams();
					params.leftMargin = position;
					halfPlayProgressBarBegin.setLayoutParams(params);
					halfPlayProgressBarBegin.setVisibility(View.VISIBLE);
				} else {
					halfPlayProgressBarBegin.setVisibility(View.GONE);
					halfPlaySeekBerBegin.setVisibility(View.GONE);
				}
				if (etime > 0) {
					int totalWidth = UIs.getScreenWidth();
					int position = (int) (etime * totalWidth * 1.0 / playAlbumController.playRecord.getTotalDuration());
					RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) halfPlaySeekBerEnd.getLayoutParams();
					params.leftMargin = position;
					halfPlaySeekBerEnd.setLayoutParams(params);
					halfPlaySeekBerEnd.setVisibility(View.VISIBLE);

					params = (RelativeLayout.LayoutParams) halfPlayProgressBarEnd.getLayoutParams();
					params.leftMargin = position;
					halfPlayProgressBarEnd.setLayoutParams(params);
					halfPlayProgressBarEnd.setVisibility(View.VISIBLE);
				} else {
					halfPlaySeekBerEnd.setVisibility(View.GONE);
					halfPlayProgressBarEnd.setVisibility(View.GONE);
				}
			} else {
				halfPlayProgressBarBegin.setVisibility(View.GONE);
				halfPlaySeekBerBegin.setVisibility(View.GONE);
				halfPlaySeekBerEnd.setVisibility(View.GONE);
				halfPlayProgressBarEnd.setVisibility(View.GONE);
			}
		}
	}

	@Override
	public void onTimeChange() {

	}

	@Override
	public void onNetChange() {

	}

	@Override
	public void onBatteryChange(int curStatus, int curPower) {

	}


	@Override
	public void onVolumeChange(int mar, int progress) {
	}

	@Override
	public void videoChange(AlbumNew album, Video video) {
		
	}
	
	private void startHandlerHide(){
		if(isShow()){
			handler.removeMessages(1);
			handler.sendEmptyMessageDelayed(1, 3000);
		}
	}
	
	/**
	 * 移除自动隐藏
	 * */
	private void stopHandlerHide(){
		handler.removeMessages(1);
	}

	@Override
	public void setTitle(String title) {
		
	}
}
