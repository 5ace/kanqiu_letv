package com.letv.watchball.utils;

import java.util.List;

import com.letv.watchball.bean.LocalCacheBean;
import com.letv.watchball.db.DBManager;

public class LocalCacheTool {
	private LocalCacheTool() {
		
	}

	private static LocalCacheTool instance = new LocalCacheTool();

	public static LocalCacheTool getInstance() {
		return instance;
	}
	
	/**
	 * 读取缓存数据
	 * @param cacheId
	 * @return
	 */
	public LocalCacheBean readCacheData(String cacheId){
		LocalCacheBean mLocalCacheBean = DBManager.getInstance().getLocalCacheTrace().getLocalCacheByCacheId(cacheId);
		if(mLocalCacheBean!=null){
			LogInfo.log("cacheId ="+cacheId);
		}else {
			LogInfo.log("readCacheData ----- null");
		}
		return mLocalCacheBean;
	}
	
	/**
	 * 读取缓存数据
	 * @param cacheId
	 * @return
	 */
	public List<LocalCacheBean> readCacheDataByAssistKey(String assistKey){
		List<LocalCacheBean> mLocalCacheBeans = DBManager.getInstance().getLocalCacheTrace().getLocalCacheByAssistKey(assistKey);
		return mLocalCacheBeans;
	}
	
	/**
	 * 缓存数据写入数据库
	 * @param mLocalCacheBean
	 * @return
	 */
	public boolean writeCacheData(LocalCacheBean mLocalCacheBean){
		return DBManager.getInstance().getLocalCacheTrace().saveLocalCache(mLocalCacheBean);
	}
	
	/**根据cacheId删除缓存
	 * @param cacheId
	 */
	public void deleteCacheDate(String cacheId){
		DBManager.getInstance().getLocalCacheTrace().deleteByCacheId(cacheId);
	}
	/**
	 *删除过期的数据 
	 */
	public void clearOverdueCache(){
		DBManager.getInstance().getLocalCacheTrace().clearOverdueData();
	}
}
