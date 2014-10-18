package com.letv.watchball.view;

import android.content.Context;
import android.media.AudioManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.letv.ads.ADPlayFragment.VipViewCallBack;
import com.letv.watchball.R;

public class PlayAdLayout extends FrameLayout{

	public TextView mTimeTextView;
	public ImageView muteView;
	/**
	 * 初始音量
	 * */
	private int old = 0;

	/**
	 * 会员去广告按钮
	 * */
	private View vipView;

	/**
	 * 会员去广告按钮回调
	 * */
	private VipViewCallBack viewCallBack;
	/**
	 * 声音管理者
	 * */
	private AudioManager audioManager;
	
	public PlayAdLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	private void init(Context context) {
		inflate(context, R.layout.play_adjoin_layout, this);
		setVisibility(GONE);
		findView();
	}
	
	private void findView(){
		mTimeTextView = (TextView) findViewById(R.id.ad_time);
		muteView = (ImageView) findViewById(R.id.ad_mute);
		vipView = (TextView) findViewById(R.id.ad_vip);
		audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
		if (audioManager != null) {
			if (audioManager.getMode() == AudioManager.MODE_INVALID) {
				audioManager.setMode(AudioManager.MODE_NORMAL);
			}
			
			// 初始化声音管理器
			int cur = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
			if (cur == 0) {
				muteView.setImageResource(R.drawable.mute);
			} else {
				muteView.setImageResource(R.drawable.not_muted);
			}
		
		}

		muteView.setOnClickListener(new OnClickListener() {// 静音按钮监听

			@Override
			public void onClick(View v) {
				int cur = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
				if (cur == 0) {
					if (old == 0) {// 无声音，且上一次的声音也为0，那么给一个初始值
						audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 5, 0);
						muteView.setImageResource(R.drawable.not_muted);
					} else {
						audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, old, 0);
						muteView.setImageResource(R.drawable.not_muted);
					}
				} else {
					old = cur;
					audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
					muteView.setImageResource(R.drawable.mute);
				}
			}
		});

		vipView.setOnClickListener(new OnClickListener() {// 会员去广告按钮，点击回调主工程

			@Override
			public void onClick(View v) {
				if (viewCallBack != null) {
					viewCallBack.onClick();
				}
			}
		});
	}
	
	public void startAd(){
		setVisibility(View.VISIBLE);
		muteView.setVisibility(View.VISIBLE);
		mTimeTextView.setVisibility(View.VISIBLE);
		vipView.setVisibility(View.VISIBLE);
	}
	
	public void finishAd() {
		setVisibility(View.GONE);
		muteView.setVisibility(View.GONE);
		mTimeTextView.setVisibility(View.GONE);
		vipView.setVisibility(View.GONE);
		if (audioManager != null) {
			int cur = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
			if (cur == 0) {
				audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, old, 0);
				muteView.setImageResource(R.drawable.not_muted);
			}
		}
	}
	
	public VipViewCallBack getViewCallBack() {
		return viewCallBack;
	}

	public void setViewCallBack(VipViewCallBack viewCallBack) {
		this.viewCallBack = viewCallBack;
	}
	
}
