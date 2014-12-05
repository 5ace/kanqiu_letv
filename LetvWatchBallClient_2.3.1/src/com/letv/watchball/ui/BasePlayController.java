package com.letv.watchball.ui;

import com.letv.watchball.bean.AlbumNew;
import com.letv.watchball.bean.Video;

public abstract class BasePlayController {

	private boolean isShow;

	protected PlayControllerCallBack callBack;

	public PlayControllerCallBack getCallBack() {
		return callBack;
	}

	public void setCallBack(PlayControllerCallBack callBack) {
		this.callBack = callBack;
	}

	/**
	 * 当视频对象变化
	 * */
	public abstract void videoChange(AlbumNew album, Video video);

	/**
	 * 隐藏控制栏
	 * */
	public abstract void hide();

	/**
	 * 显示控制栏
	 * */
	public abstract void show();

	/**
	 * 单击显示或者隐藏 true 代表显示 false 代表隐藏
	 * */
	public abstract boolean clickShowAndHide();

	/**
	 * 单击显示或者隐藏 true 代表显示 false 代表隐藏
	 * */
	public abstract void clickShowAndHide(boolean isShow);

	/**
	 * 初始化进度条
	 * */
	public abstract void initProgress(int max, int progress, int buffer);

	/**
	 * 更新进度条
	 * */
	public abstract void updateProgress(int progress, int buffer);

	/**
	 * 切换到播放状态
	 * */
	public abstract void star();

	/**
	 * 切换到暂停状态
	 * */
	public abstract void pause();

	/**
	 * 切换到不可操作
	 * */
	public abstract void Inoperable();

	/**
	 * 恢复到初始状态
	 * */
	public abstract void format();

	/**
	 * 初始化简介
	 * */
	public abstract void initIntroduction();

	/**
	 * 时间改变
	 * */
	public abstract void onTimeChange();

	/**
	 * 网络改变
	 * */
	public abstract void onNetChange();

	/**
	 * 电量改变
	 * */
	public abstract void onBatteryChange(int curStatus, int curPower);

	/**
	 * 声音改变
	 * */
	public abstract void onVolumeChange(int mar, int progress);

	/**
	 * 设置title
	 * */
	public abstract void setTitle(String title);

	public boolean isShow() {
		return isShow;
	}

	public void setShow(boolean isShow) {
		this.isShow = isShow;
	}

}
