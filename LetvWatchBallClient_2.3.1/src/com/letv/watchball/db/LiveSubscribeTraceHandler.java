package com.letv.watchball.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.letv.watchball.bean.PushSubscribeGame;
import com.letv.watchball.utils.LetvConstant;
import com.letv.watchball.utils.LetvUtil;

public class LiveSubscribeTraceHandler {

	private Context context;

	public LiveSubscribeTraceHandler(Context context) {
		this.context = context;
	}

	/**
	 * 保存预定节目
	 * */
	public void saveSubscribeGameTrace(PushSubscribeGame body) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(LetvConstant.DataBase.SubscribeGameTrace.Field.id, body.id);
		contentValues.put(LetvConstant.DataBase.SubscribeGameTrace.Field.guest, body.guest);
		contentValues.put(LetvConstant.DataBase.SubscribeGameTrace.Field.home, body.home);
		contentValues.put(LetvConstant.DataBase.SubscribeGameTrace.Field.level, body.level);
		contentValues.put(LetvConstant.DataBase.SubscribeGameTrace.Field.playDate, body.playDate);
		contentValues.put(LetvConstant.DataBase.SubscribeGameTrace.Field.playTime, body.playTime);
		contentValues.put(LetvConstant.DataBase.SubscribeGameTrace.Field.playTimeMillisecond, LetvUtil.timeFormatSubscribeGame(body.playDate, body.playTime));
		contentValues.put(LetvConstant.DataBase.SubscribeGameTrace.Field.status, body.status);
		if (!hasSubscribeGameTrace(body.id)) {
			context.getContentResolver().insert(LetvContentProvider.URI_SUBSCRIBE_GAME_TRACE, contentValues);
		} else {
			context.getContentResolver().update(LetvContentProvider.URI_SUBSCRIBE_GAME_TRACE, contentValues,
					LetvConstant.DataBase.SubscribeGameTrace.Field.id + "=?", new String[] { body.id });
		}
	}

	/**
	 * 得到离当前时间最近的一场比赛
	 * */
	public long getNearestTrace() {
		Cursor cursor = null;
		try {
			cursor = context.getContentResolver().query(LetvContentProvider.URI_SUBSCRIBE_GAME_TRACE, null,
					LetvConstant.DataBase.SubscribeGameTrace.Field.playTimeMillisecond + ">=? AND "+ 
					LetvConstant.DataBase.SubscribeGameTrace.Field.isNotify + "=?",
//					new String[] { (System.currentTimeMillis()) + "" }, LetvConstant.DataBase.SubscribeGameTrace.Field.playTimeMillisecond);
			new String[] {(System.currentTimeMillis() - 5 * 60 * 1000) + "", 0+"" }, LetvConstant.DataBase.SubscribeGameTrace.Field.playTimeMillisecond);
			if (cursor != null && cursor.getCount() > 0) {
				cursor.moveToFirst();
				return cursor.getLong(cursor.getColumnIndexOrThrow(LetvConstant.DataBase.SubscribeGameTrace.Field.playTimeMillisecond));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			LetvUtil.closeCursor(cursor);
		}
		return -1;
	}

	/**
	 * 得到当前时间，前10分钟，后(赛果提醒设定时间)分钟内的节目
	 * */
	public ArrayList<PushSubscribeGame> getCurrentTrace() {
		Cursor cursor = null;
		ArrayList<PushSubscribeGame> list = null;
		long currentTime = System.currentTimeMillis();
		try {
			cursor = context.getContentResolver().query(
					LetvContentProvider.URI_SUBSCRIBE_GAME_TRACE,
					null,
					LetvConstant.DataBase.SubscribeGameTrace.Field.playTimeMillisecond + " > ? AND "
							+ LetvConstant.DataBase.SubscribeGameTrace.Field.playTimeMillisecond + " < ? AND "
							+ LetvConstant.DataBase.SubscribeGameTrace.Field.isNotify + " = ? ",
					new String[] { (currentTime - 10 * 60 * 1000) + "", (currentTime + PreferencesManager.getInstance().getGameStartRemind() * 60 * 1000) + "",0+"" },
					LetvConstant.DataBase.SubscribeGameTrace.Field.playTimeMillisecond);
			list = new ArrayList<PushSubscribeGame>();

			while (cursor != null && cursor.moveToNext()) {
				PushSubscribeGame subscribeGame = new PushSubscribeGame();

				subscribeGame.id = (cursor.getString(cursor.getColumnIndexOrThrow(LetvConstant.DataBase.SubscribeGameTrace.Field.id)));
				subscribeGame.level = (cursor.getString(cursor.getColumnIndexOrThrow(LetvConstant.DataBase.SubscribeGameTrace.Field.level)));
				subscribeGame.home = (cursor.getString(cursor.getColumnIndexOrThrow(LetvConstant.DataBase.SubscribeGameTrace.Field.home)));
				subscribeGame.guest = (cursor.getString(cursor.getColumnIndexOrThrow(LetvConstant.DataBase.SubscribeGameTrace.Field.guest)));
				subscribeGame.playDate = (cursor.getString(cursor.getColumnIndexOrThrow(LetvConstant.DataBase.SubscribeGameTrace.Field.playDate)));
				subscribeGame.playTime = (cursor.getString(cursor.getColumnIndexOrThrow(LetvConstant.DataBase.SubscribeGameTrace.Field.playTime)));
				subscribeGame.playTimeMillisecond = (cursor.getLong(cursor
						.getColumnIndexOrThrow(LetvConstant.DataBase.SubscribeGameTrace.Field.playTimeMillisecond)));
				subscribeGame.status = (cursor.getInt(cursor.getColumnIndexOrThrow(LetvConstant.DataBase.SubscribeGameTrace.Field.status)));
				subscribeGame.isNotify = (cursor.getInt(cursor.getColumnIndexOrThrow(LetvConstant.DataBase.SubscribeGameTrace.Field.isNotify)));
				list.add(subscribeGame);
			}
		} finally {
			LetvUtil.closeCursor(cursor);
		}

		return list;
	}

	/**
	 * 得到所有预约比赛记录
	 * */
	public ArrayList<PushSubscribeGame> getAllTrace() {
		Cursor cursor = null;
		ArrayList<PushSubscribeGame> list = null;
		try {
			cursor = context.getContentResolver().query(LetvContentProvider.URI_SUBSCRIBE_GAME_TRACE, null, null, null, null);
			list = new ArrayList<PushSubscribeGame>();

			while (cursor != null && cursor.moveToNext()) {
				PushSubscribeGame subscribeGame = new PushSubscribeGame();

				subscribeGame.id = (cursor.getString(cursor.getColumnIndexOrThrow(LetvConstant.DataBase.SubscribeGameTrace.Field.id)));
				subscribeGame.level = (cursor.getString(cursor.getColumnIndexOrThrow(LetvConstant.DataBase.SubscribeGameTrace.Field.level)));
				subscribeGame.home = (cursor.getString(cursor.getColumnIndexOrThrow(LetvConstant.DataBase.SubscribeGameTrace.Field.home)));
				subscribeGame.guest = (cursor.getString(cursor.getColumnIndexOrThrow(LetvConstant.DataBase.SubscribeGameTrace.Field.guest)));
				subscribeGame.playDate = (cursor.getString(cursor.getColumnIndexOrThrow(LetvConstant.DataBase.SubscribeGameTrace.Field.playDate)));
				subscribeGame.playTime = (cursor.getString(cursor.getColumnIndexOrThrow(LetvConstant.DataBase.SubscribeGameTrace.Field.playTime)));
				subscribeGame.playTimeMillisecond = (cursor.getLong(cursor
						.getColumnIndexOrThrow(LetvConstant.DataBase.SubscribeGameTrace.Field.playTimeMillisecond)));
				subscribeGame.status = (cursor.getInt(cursor.getColumnIndexOrThrow(LetvConstant.DataBase.SubscribeGameTrace.Field.status)));
				subscribeGame.isNotify = (cursor.getInt(cursor.getColumnIndexOrThrow(LetvConstant.DataBase.SubscribeGameTrace.Field.isNotify)));
				subscribeGame.isPushResult = (cursor.getInt(cursor.getColumnIndexOrThrow(LetvConstant.DataBase.SubscribeGameTrace.Field.isPushResult)));
				list.add(subscribeGame);
			}
		} finally {
			LetvUtil.closeCursor(cursor);
		}

		return list;
	}

	/**
	 * 清除全部预定
	 * */
	public void clearAll() {
		context.getContentResolver().delete(LetvContentProvider.URI_SUBSCRIBE_GAME_TRACE, null, null);
	}

	/**
	 * 删除一条记录
	 * */
	public void remove(String id) {
		context.getContentResolver().delete(LetvContentProvider.URI_SUBSCRIBE_GAME_TRACE, LetvConstant.DataBase.SubscribeGameTrace.Field.id + "=?",
				new String[] { id });
	}

	/**
	 * 更新一条预约提醒
	 * */
	public void updateNotity(String id, boolean isNotify) {
		int in = isNotify ? 1 : 0;
		ContentValues cv = new ContentValues();
		cv.put(LetvConstant.DataBase.SubscribeGameTrace.Field.isNotify, in);
		context.getContentResolver().update(LetvContentProvider.URI_SUBSCRIBE_GAME_TRACE, cv, LetvConstant.DataBase.SubscribeGameTrace.Field.id + "=?",
				new String[] { id + "" });
	}
	
	/**
	 * 更新一条赛果提醒
	 * */
	public void updatePush(String id, boolean isPush) {
		int in = isPush ? 1 : 0;
		ContentValues cv = new ContentValues();
		cv.put(LetvConstant.DataBase.SubscribeGameTrace.Field.isPushResult, in);
		context.getContentResolver().update(LetvContentProvider.URI_SUBSCRIBE_GAME_TRACE, cv, LetvConstant.DataBase.SubscribeGameTrace.Field.id + "=?",
				new String[] { id + "" });
	}
	
	/**
	 * 判断是否记录已存在
	 * */
	public boolean hasSubscribeGameTrace(String id) {
		Cursor cursor = null;
		try {
			cursor = context.getContentResolver().query(LetvContentProvider.URI_SUBSCRIBE_GAME_TRACE, null,
					LetvConstant.DataBase.SubscribeGameTrace.Field.id + "=?", new String[] { id + "" }, null);
			int count = cursor != null ? cursor.getCount() : 0;
                  return count > 0;
		} finally {
			LetvUtil.closeCursor(cursor);
		}
	}

	/**
	 * 得到所有可进行赛果轮训的记录
	 * */
	public ArrayList<PushSubscribeGame> getAllSubscribeGamesTrace() {
		Cursor cursor = null;
		ArrayList<PushSubscribeGame> list = null;
		try {
			cursor = context.getContentResolver().query(LetvContentProvider.URI_SUBSCRIBE_GAME_TRACE, null,
					LetvConstant.DataBase.SubscribeGameTrace.Field.isPushResult + "=? AND " + LetvConstant.DataBase.SubscribeGameTrace.Field.status + ">?",
					new String[] {0 + "" , 1 + "" }, null);
			list = new ArrayList<PushSubscribeGame>();

			while (cursor != null && cursor.moveToNext()) {
				PushSubscribeGame subscribeGame = new PushSubscribeGame();

				subscribeGame.id = (cursor.getString(cursor.getColumnIndexOrThrow(LetvConstant.DataBase.SubscribeGameTrace.Field.id)));
				subscribeGame.level = (cursor.getString(cursor.getColumnIndexOrThrow(LetvConstant.DataBase.SubscribeGameTrace.Field.level)));
				subscribeGame.home = (cursor.getString(cursor.getColumnIndexOrThrow(LetvConstant.DataBase.SubscribeGameTrace.Field.home)));
				subscribeGame.guest = (cursor.getString(cursor.getColumnIndexOrThrow(LetvConstant.DataBase.SubscribeGameTrace.Field.guest)));
				subscribeGame.playDate = (cursor.getString(cursor.getColumnIndexOrThrow(LetvConstant.DataBase.SubscribeGameTrace.Field.playDate)));
				subscribeGame.playTime = (cursor.getString(cursor.getColumnIndexOrThrow(LetvConstant.DataBase.SubscribeGameTrace.Field.playTime)));
				subscribeGame.playTimeMillisecond = (cursor.getLong(cursor
						.getColumnIndexOrThrow(LetvConstant.DataBase.SubscribeGameTrace.Field.playTimeMillisecond)));
				subscribeGame.status = (cursor.getInt(cursor.getColumnIndexOrThrow(LetvConstant.DataBase.SubscribeGameTrace.Field.status)));
				subscribeGame.isNotify = (cursor.getInt(cursor.getColumnIndexOrThrow(LetvConstant.DataBase.SubscribeGameTrace.Field.isNotify)));
				subscribeGame.isPushResult = (cursor.getInt(cursor.getColumnIndexOrThrow(LetvConstant.DataBase.SubscribeGameTrace.Field.isPushResult)));
				list.add(subscribeGame);
			}
		} finally {
			LetvUtil.closeCursor(cursor);
		}

		return list;
	}

}
