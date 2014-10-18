package com.letv.ads.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.letv.adlib.model.ad.common.CommonAdItem;
import com.letv.adlib.model.ad.converters.CommonAdDataConverter;
import com.letv.ads.util.ClientInfoUtil;
import com.letv.ads.util.LogInfo;

public class AdsDBHandler {

	/**
	 * 保存广告
	 * */
	public static void saveAd(Context context, String ad, CommonAdItem adInfo) {
		if (has(context, ad)) {
			remove(context, ad);
		}
		if (adInfo != null) {
			ContentValues contentValues = new ContentValues();

			contentValues.put(DBConstant.AD, ad);
			contentValues.put(DBConstant.CONTENT, adInfo.AdJSONStr);

			context.getContentResolver().insert(AdsContentProviderForWB.URI_ADS, contentValues);
		}
	}

	/**
	 * 得到广告
	 * */
	public static ArrayList<CommonAdItem> getAd(Context context, String ad) {
		Cursor cursor = null;
		ArrayList<CommonAdItem> adInfo = null;
		try {

			cursor = context.getContentResolver().query(AdsContentProviderForWB.URI_ADS, null, DBConstant.AD + "= ?", new String[] { ad }, null);
                  LogInfo.log("ads" , AdsContentProviderForWB.URI_ADS + "   " + DBConstant.AD + "   " +ad + "    " + context + "    " + cursor);

			if (cursor != null && cursor.getCount() > 0) {
				cursor.moveToNext();
				String string = cursor.getString(cursor.getColumnIndexOrThrow(DBConstant.CONTENT));
				adInfo = CommonAdDataConverter.convertWithJSONStrAndClientInfo(string, ClientInfoUtil.getBeginInfo(context));
			}
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}

		return adInfo;
	}

	public static void update(Context context, String ad, CommonAdItem adInfo) {
		ContentValues contentValues = new ContentValues();

		contentValues.put(DBConstant.AD, ad);
		contentValues.put(DBConstant.CONTENT, adInfo.AdJSONStr);

		context.getContentResolver().update(AdsContentProviderForWB.URI_ADS, contentValues, DBConstant.AD + "=?", new String[] { ad });
	}

	/**
	 * 删除全部收藏记录
	 * */
	public static void clearAll(Context context) {
		context.getContentResolver().delete(AdsContentProviderForWB.URI_ADS, null, null);
	}

	/**
	 * 删除一条记录
	 * */
	public static void remove(Context context, String ad) {
		context.getContentResolver().delete(AdsContentProviderForWB.URI_ADS, DBConstant.AD + "=?", new String[] { ad });
	}

	/**
	 * 判断是否记录已存在
	 * */
	public static boolean has(Context context, String ad) {
		Cursor cursor = null;
		try {
			cursor = context.getContentResolver().query(AdsContentProviderForWB.URI_ADS, null, DBConstant.AD + "=?", new String[] { ad }, null);
			if (cursor != null && cursor.getCount() > 0) {
				return true;
			} else {
				return false;
			}
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
	}
}
