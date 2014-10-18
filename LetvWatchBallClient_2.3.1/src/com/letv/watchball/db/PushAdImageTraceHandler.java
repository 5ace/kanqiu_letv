package com.letv.watchball.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.letv.watchball.bean.PushAdImage;
import com.letv.watchball.utils.LetvConstant;
import com.letv.watchball.utils.LetvUtil;

/**
 * 客户端显示缓存
 */
public class PushAdImageTraceHandler {
	private Context context;

	public PushAdImageTraceHandler(Context context) {
		this.context = context;
	}

	/**
	 * 保存记录
	 */
	public synchronized boolean savePushAdImage(PushAdImage pushAdImage) {
		if (pushAdImage != null) {
				ContentValues cv = new ContentValues();
				cv.put(LetvConstant.DataBase.PushAdImageTrace.Field.ID, pushAdImage.getId());
				cv.put(LetvConstant.DataBase.PushAdImageTrace.Field.IMAGEURL, pushAdImage.getPic1());
				cv.put(LetvConstant.DataBase.PushAdImageTrace.Field.CREATETIME, pushAdImage.getCreateTime());
				cv.put(LetvConstant.DataBase.PushAdImageTrace.Field.MTIME, pushAdImage.getmTime());
				context.getContentResolver().insert(LetvContentProvider.URI_PUSHADIAMGECACHETRACE, cv);
				return true;
		} else {
			return false;
		}
	}

	/**
	 * 根据 cacheID 更新数据表	
	 */
	private void updatePushAdImageById(PushAdImage pushAdImage) {
		if (pushAdImage != null) {
			ContentValues cv = new ContentValues();
			cv.put(LetvConstant.DataBase.PushAdImageTrace.Field.ID, pushAdImage.getId());
			cv.put(LetvConstant.DataBase.PushAdImageTrace.Field.IMAGEURL, pushAdImage.getPic1());
			cv.put(LetvConstant.DataBase.PushAdImageTrace.Field.CREATETIME, pushAdImage.getCreateTime());
			cv.put(LetvConstant.DataBase.PushAdImageTrace.Field.MTIME, pushAdImage.getmTime());
			context.getContentResolver().update(LetvContentProvider.URI_PUSHADIAMGECACHETRACE, cv, LetvConstant.DataBase.PushAdImageTrace.Field.ID + " = " + pushAdImage.getId(),
					null);
		}
	}

	/**
	 * 根据cacheId获取数据
	 * 
	 * @return LocalCacheBean
	 */
	public synchronized PushAdImage getPushAdImage() {
		Cursor cursor = null;
		PushAdImage PushAdImage = null;
		try {
			cursor = context.getContentResolver().query(LetvContentProvider.URI_PUSHADIAMGECACHETRACE, null, null, null, null);
			if (cursor != null && cursor.moveToNext()) {
				PushAdImage = new PushAdImage();
				PushAdImage.setId(cursor.getInt(cursor.getColumnIndex(LetvConstant.DataBase.PushAdImageTrace.Field.ID)));
				PushAdImage.setPic1(cursor.getString(cursor.getColumnIndex(LetvConstant.DataBase.PushAdImageTrace.Field.IMAGEURL)));
				PushAdImage.setCreateTime(Long.parseLong(cursor.getString(cursor.getColumnIndex(LetvConstant.DataBase.PushAdImageTrace.Field.CREATETIME))));
				PushAdImage.setmTime(Long.parseLong(cursor.getString(cursor.getColumnIndex(LetvConstant.DataBase.PushAdImageTrace.Field.MTIME))));
			}
		} finally {
			LetvUtil.closeCursor(cursor);
		}
		return PushAdImage;
	}

	

	/**
	 * 清除所有记录
	 * */
	public synchronized void clearAll() {
		context.getContentResolver().delete(LetvContentProvider.URI_PUSHADIAMGECACHETRACE, null, null);
	}

	/**
	 * 根据cacheId删除一条的记录
	 */
	public synchronized void deletePushAdImageById(int id) {
		context.getContentResolver().delete(LetvContentProvider.URI_PUSHADIAMGECACHETRACE, LetvConstant.DataBase.PushAdImageTrace.Field.ID + " = " + id,null);
	}

}
