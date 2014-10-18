package com.letv.datastatistics.entity;

import java.io.Serializable;
import java.util.HashMap;

public class DataStatusInfo implements Serializable{

	private static final long serialVersionUID = -4203703140646543081L;
	
	/**
	 * 接口初始化状态
	 */
	private ApiInfo mApiInfo = null;
	
	/**
	 * 客户端设备信息上报,返回数据
	 */
	private StatInfo mStatInfo = null;
	
	/**
	 * jpush- 极光推送消息
	 */
	private JpushInfo mJpushInfo = null;
	
	/**
	 * 升级信息
	 */
	private UpgradeInfo mUpgradeInfo = null;
	
	/**
	 * 广告控制信息
	 */
	private AdsInfo mAdsInfo = null;
	
	/**
	 * 精品推荐控制信息
	 */
	private HashMap<String, RecommendInfo> recommendInfos = null;
	
	/**
	 * 播放码流控制
	 * */
	private Defaultbr playDefaultbr ;
	
	/**
	 * 下载流控制
	 * */
	private Defaultbr downloadDefaultbr ;
	/**
	 * 首页底部精品推荐 开关
	 * @return
	 */
	private boolean bottomRecommendSwitch = true;
	/**
	 * 精品推荐频道开关
	 * @return
	 */
	private boolean channelRecommendSwitch = true;
	
	private LogoInfo mLogoInfo;
	/**
	 * 服务器时间戳
	 * */
	private int tm ;
	
	private int androidOpen350 = 0;
	
	public boolean isBottomRecommendSwitch() {
		return bottomRecommendSwitch;
	}

	public void setBottomRecommendSwitch(boolean bottomRecommendSwitch) {
		this.bottomRecommendSwitch = bottomRecommendSwitch;
	}

	public boolean isChannelRecommendSwitch() {
		return channelRecommendSwitch;
	}

	public void setChannelRecommendSwitch(boolean channelRecommendSwitch) {
		this.channelRecommendSwitch = channelRecommendSwitch;
	}

	public ApiInfo getApiInfo() {
		return mApiInfo;
	}

	public void setApiInfo(ApiInfo mApiInfo) {
		this.mApiInfo = mApiInfo;
	}

	public StatInfo getStatInfo() {
		return mStatInfo;
	}

	public void setStatInfo(StatInfo mStatInfo) {
		this.mStatInfo = mStatInfo;
	}
	
	/**
	 * @return the mJpushInfo
	 */
	public JpushInfo getmJpushInfo() {
		return mJpushInfo;
	}

	/**
	 * @param mJpushInfo the mJpushInfo to set
	 */
	public void setmJpushInfo(JpushInfo mJpushInfo) {
		this.mJpushInfo = mJpushInfo;
	}

	public UpgradeInfo getUpgradeInfo() {
		return mUpgradeInfo;
	}

	public void setUpgradeInfo(UpgradeInfo mUpgradeInfo) {
		this.mUpgradeInfo = mUpgradeInfo;
	}
	
	public AdsInfo getAdsInfo() {
		return mAdsInfo;
	}

	public void setAdsInfo(AdsInfo mAdsInfo) {
		this.mAdsInfo = mAdsInfo;
	}
	
	public HashMap<String, RecommendInfo> getRecommendInfos() {
		return recommendInfos;
	}

	public void setRecommendInfos(HashMap<String, RecommendInfo> recommendInfos) {
		this.recommendInfos = recommendInfos;
	}

	public Defaultbr getPlayDefaultbr() {
		return playDefaultbr;
	}

	public void setPlayDefaultbr(Defaultbr playDefaultbr) {
		this.playDefaultbr = playDefaultbr;
	}

	public Defaultbr getDownloadDefaultbr() {
		return downloadDefaultbr;
	}

	public void setDownloadDefaultbr(Defaultbr downloadDefaultbr) {
		this.downloadDefaultbr = downloadDefaultbr;
	}

	public int getTm() {
		return tm;
	}

	public void setTm(int tm) {
		this.tm = tm;
	}

	int getAndroidOpen350() {
		return androidOpen350;
	}

	public void setAndroidOpen350(int androidOpen350) {
		this.androidOpen350 = androidOpen350;
	}
	
	public boolean istAndroidOpen350() {
		return this.androidOpen350 == 1;
	}

	/**
	 * @return the mLogoInfo
	 */
	public LogoInfo getmLogoInfo() {
		return mLogoInfo;
	}

	/**
	 * @param mLogoInfo the mLogoInfo to set
	 */
	public void setmLogoInfo(LogoInfo mLogoInfo) {
		this.mLogoInfo = mLogoInfo;
	}
	
}
