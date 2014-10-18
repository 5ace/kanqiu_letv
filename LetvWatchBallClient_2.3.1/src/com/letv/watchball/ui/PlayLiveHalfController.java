package com.letv.watchball.ui;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.letv.watchball.R;

public class PlayLiveHalfController extends BaseLivePlayController {

	private View mBack;
	
	public ImageView mPlayPause;
	
	private ImageView mFullHalf;
	
	private ImageView mBook;
	
	private PlayLiveController mController;
	
	/**
	 * 半屏控制条
	 * */
	private View halfPlayControllerLayout;
	
	
	public PlayLiveHalfController(PlayLiveController controller, View root) {
		findView(root);
		this.mController = controller;
	}
	
	private void findView(View root) {
		halfPlayControllerLayout = root.findViewById(R.id.live_half_controller);
		mBack = root.findViewById(R.id.btn_back);
		mPlayPause = (ImageView) root.findViewById(R.id.controller_play);
		mFullHalf = (ImageView) root.findViewById(R.id.controller_full);
		mBook = (ImageView) root.findViewById(R.id.controller_book);
		mBack.setOnClickListener(backClick);
		mBook.setOnClickListener(bookClick);
		mFullHalf.setOnClickListener(fullClick);
	}
	
	@Override
	public void hide() {
		// TODO Auto-generated method stub
		halfPlayControllerLayout.setVisibility(View.GONE);
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		halfPlayControllerLayout.setVisibility(View.VISIBLE);
	}

	@Override
	public void star() {
		// TODO Auto-generated method stub
		mPlayPause.setImageResource(R.drawable.pause_selecter);
		mPlayPause.setOnClickListener(pauseClick);
	}
	/**
	 * 返回
	 */
	private OnClickListener backClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (callBack != null)
				callBack.back();
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
	 * 点击播放的监听
	 * */
	private OnClickListener bookClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (callBack != null){
				callBack.book();
			}
		}
	};
	
	@Override
	public void pause() {
		// TODO Auto-generated method stub
		mPlayPause.setImageResource(R.drawable.play_selecter);
		mPlayPause.setOnClickListener(playClick);
	}

	@Override
	public void format() {
		// TODO Auto-generated method stub
//		Inoperable();
	}

	@Override
	public void onTimeChange() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNetChange() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onBatteryChange(int curStatus, int curPower) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onVolumeChange(int mar, int progress) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void Inoperable() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onVolumeChange(int mar, int progress, boolean isUp) {
		// TODO Auto-generated method stub
		
	}

}
