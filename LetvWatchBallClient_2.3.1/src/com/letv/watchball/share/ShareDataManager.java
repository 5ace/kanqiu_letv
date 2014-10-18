package com.letv.watchball.share;

import com.letv.watchball.bean.Album;


public class ShareDataManager<T> {
	
	public enum WEIBO{
		NULL ,
		SINA_WEIBO , TENCENT_WEIBO , TENCENT_QZONE , RENREN , KAIXIN , LETVSTAR ;
	}
	
	private T shareData ;
	
	private WEIBO lastLogin = WEIBO.NULL ;
	
	private static ShareDataManager<Album> instance ;
	
	private ShareDataManager(){
		
	}
	
	public synchronized static ShareDataManager<Album> getInstance(){
		if(instance == null){
			instance = new ShareDataManager<Album>();
		}
		
		return instance ;
	}

	public T getShareData() {
		return shareData;
	}

	public void setShareData(T shareData) {
		this.shareData = shareData;
	}

	public WEIBO getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(WEIBO lastLogin) {
		this.lastLogin = lastLogin;
	}
}
