package com.letv.watchball.ui;


public abstract class BaseLivePlayController {
	protected PlayLiveControllerCallBack callBack;
	private boolean isShow;

	public PlayLiveControllerCallBack getCallBack() {
		return callBack;
	}

	public void setCallBack(PlayLiveControllerCallBack callBack) {
		this.callBack = callBack;
	}

	/**
	 * 隐藏控制栏
	 * */
	public abstract void hide();

	/**
	 * 显示控制栏
	 * */
	public abstract void show();
	
	/**
	 * 单击显示或者隐藏  true 代表显示    false 代表隐藏
	 * */
	public boolean clickShowAndHide(){
		return true;
	};
	
	/**
	 * 单击显示或者隐藏  true 代表显示    false 代表隐藏
	 * */
	public void clickShowAndHide(boolean isShow){};

	/**
	 * 切换到播放状态
	 * */
	public abstract void star();

	/**
	 * 切换到暂停状态
	 * */
	public abstract void pause();

	/**
	 * 恢复到初始状态
	 * */
	public abstract void format();

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
	
	public abstract void onVolumeChange(int mar, int progress,boolean isUp);
	/**
	 * 切换到不可操作
	 * */
	public abstract void Inoperable();
	
	
	public boolean isShow() {
		return isShow;
	}

	public void setShow(boolean isShow) {
		this.isShow = isShow;
	}

	/**
	 * 设置标题
	 * @param title
	 */
	public void setTitle(String title){};
	
	
}
