package com.letv.watchball.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.letv.watchball.bean.LocalCacheBean;
import com.letv.watchball.utils.LetvConstant;
import com.letv.watchball.utils.LetvUtil;

/**
 * 客户端显示缓存
 */
public class LocalCacheTraceHandler {
	private Context context;

	public LocalCacheTraceHandler(Context context) {
		this.context = context;
	}

	/**
	 * 保存记录
	 */
	public synchronized boolean saveLocalCache(LocalCacheBean mLocalCacheBean) {
		if (mLocalCacheBean != null) {
			if (hasByCacheId(mLocalCacheBean.getCacheId())) {
				updateByCacheId(mLocalCacheBean);
			} else {
				ContentValues cv = new ContentValues();
				cv.put(LetvConstant.DataBase.LocalCacheTrace.Field.CACHEID, mLocalCacheBean.getCacheId());
				cv.put(LetvConstant.DataBase.LocalCacheTrace.Field.MARKID, mLocalCacheBean.getMarkId());
				cv.put(LetvConstant.DataBase.LocalCacheTrace.Field.CACHEDATA, mLocalCacheBean.getCacheData());
				cv.put(LetvConstant.DataBase.LocalCacheTrace.Field.CACHETIME, mLocalCacheBean.getCacheTime());
				cv.put(LetvConstant.DataBase.LocalCacheTrace.Field.ASSISTKEY, mLocalCacheBean.getAssistKey());
				context.getContentResolver().insert(LetvContentProvider.URI_LOCALCACHETRACE, cv);
			}
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 根据 cacheID 更新数据表
	 */
	private void updateByCacheId(LocalCacheBean mLocalCacheBean) {
		if (mLocalCacheBean != null) {
			ContentValues cv = new ContentValues();
			cv.put(LetvConstant.DataBase.LocalCacheTrace.Field.CACHEID, mLocalCacheBean.getCacheId());
			cv.put(LetvConstant.DataBase.LocalCacheTrace.Field.MARKID, mLocalCacheBean.getMarkId());
			cv.put(LetvConstant.DataBase.LocalCacheTrace.Field.CACHEDATA, mLocalCacheBean.getCacheData());
			cv.put(LetvConstant.DataBase.LocalCacheTrace.Field.CACHETIME, mLocalCacheBean.getCacheTime());
			cv.put(LetvConstant.DataBase.LocalCacheTrace.Field.ASSISTKEY, mLocalCacheBean.getAssistKey());
			context.getContentResolver().update(LetvContentProvider.URI_LOCALCACHETRACE, cv, LetvConstant.DataBase.LocalCacheTrace.Field.CACHEID + "=?",
					new String[] { mLocalCacheBean.getCacheId() });
		}
	}

	/**
	 * 根据cacheId获取数据
	 * 
	 * @return LocalCacheBean
	 */
	public synchronized LocalCacheBean getLocalCacheByCacheId(String cacheId) {
		Cursor cursor = null;
		LocalCacheBean mLocalCacheBean = null;
		try {
			cursor = context.getContentResolver().query(LetvContentProvider.URI_LOCALCACHETRACE, null,
					LetvConstant.DataBase.LocalCacheTrace.Field.CACHEID + "= ?", new String[] { cacheId }, null);
			if (cursor != null && cursor.moveToNext()) {
				mLocalCacheBean = new LocalCacheBean(cursor.getString(cursor.getColumnIndex(LetvConstant.DataBase.LocalCacheTrace.Field.CACHEID)),
						cursor.getString(cursor.getColumnIndex(LetvConstant.DataBase.LocalCacheTrace.Field.MARKID)), cursor.getString(cursor
								.getColumnIndex(LetvConstant.DataBase.LocalCacheTrace.Field.CACHEDATA)), cursor.getLong(cursor
								.getColumnIndex(LetvConstant.DataBase.LocalCacheTrace.Field.CACHETIME)));
			}
		} finally {
			LetvUtil.closeCursor(cursor);
		}
		return mLocalCacheBean;
	}

	/**
	 * 根据cacheId获取数据
	 * 
	 * @return LocalCacheBean
	 */
	public synchronized List<LocalCacheBean> getLocalCacheByAssistKey(String assistKey) {
		Cursor cursor = null;
		LocalCacheBean mLocalCacheBean = null;
		List<LocalCacheBean> beans = null;
		try {
			cursor = context.getContentResolver().query(LetvContentProvider.URI_LOCALCACHETRACE, null,
					LetvConstant.DataBase.LocalCacheTrace.Field.ASSISTKEY + "= ?", new String[] { assistKey }, null);
			if (cursor != null && cursor.getCount() > 0) {
				beans = new ArrayList<LocalCacheBean>();
				while (cursor.moveToNext()) {
					mLocalCacheBean = new LocalCacheBean(cursor.getString(cursor.getColumnIndex(LetvConstant.DataBase.LocalCacheTrace.Field.CACHEID)),
							cursor.getString(cursor.getColumnIndex(LetvConstant.DataBase.LocalCacheTrace.Field.MARKID)), cursor.getString(cursor
									.getColumnIndex(LetvConstant.DataBase.LocalCacheTrace.Field.CACHEDATA)), cursor.getLong(cursor
									.getColumnIndex(LetvConstant.DataBase.LocalCacheTrace.Field.CACHETIME)));

					beans.add(mLocalCacheBean);
				}
			}
		} finally {
			LetvUtil.closeCursor(cursor);
		}
		return beans;
	}

	/**
	 * 清除所有记录
	 * */
	public synchronized void clearAll() {
		context.getContentResolver().delete(LetvContentProvider.URI_LOCALCACHETRACE, null, null);
	}

	/**
	 * 根据cacheId删除一条的记录
	 */
	public synchronized void deleteByCacheId(String cacheId) {
		context.getContentResolver().delete(LetvContentProvider.URI_LOCALCACHETRACE, LetvConstant.DataBase.LocalCacheTrace.Field.CACHEID + "= ?",
				new String[] { cacheId });
	}

	/**
	 * 清除过期数据
	 */
	public synchronized void clearOverdueData() {
		// long cacheTime = System.currentTimeMillis() -
		// LetvApplication.CACHE_TIME;
		// context.getContentResolver().delete(LetvContentProvider.URI_LOCALCACHETRACE,
		// LetvConstant.DataBase.LocalCacheTrace.Field.CACHETIME + "<= ?",
		// new String[] { cacheTime + "" });
	}

	/**
	 * 根据cacheId查询数据表是否有记录
	 * 
	 * @param cacheId
	 * @return
	 */
	public synchronized boolean hasByCacheId(String cacheId) {
		Cursor cursor = null;
		try {
			cursor = context.getContentResolver().query(LetvContentProvider.URI_LOCALCACHETRACE,
					new String[] { LetvConstant.DataBase.LocalCacheTrace.Field.CACHEID }, LetvConstant.DataBase.LocalCacheTrace.Field.CACHEID + "= ?",
					new String[] { cacheId }, null);
			return cursor.getCount() > 0;
		} finally {
			LetvUtil.closeCursor(cursor);
		}
	}
}
