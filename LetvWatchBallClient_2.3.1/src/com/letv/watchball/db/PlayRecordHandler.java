package com.letv.watchball.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.letv.watchball.bean.AlbumNew;
import com.letv.watchball.bean.PlayRecord;
import com.letv.watchball.bean.PlayRecordList;
import com.letv.watchball.utils.LetvConstant;
import com.letv.watchball.utils.LetvUtil;
import com.letv.watchball.utils.LogInfo;

public class PlayRecordHandler {

	private Context context;

	public PlayRecordHandler(Context context) {
		this.context = context;
	}

	/**
	 * 上传成功后，将状态切换成正常
	 * */
	public synchronized void save2Normal(int pid, int vid) {
		ContentValues cv = new ContentValues();
		cv.put(LetvConstant.DataBase.PlayRecord.Field.STATE, 0);
		context.getContentResolver().update(LetvContentProvider.URI_PLAYTRACE, cv,
				pid <= 0 ? LetvConstant.DataBase.PlayRecord.Field.VID + "=?" : LetvConstant.DataBase.PlayRecord.Field.PID + "=?",
				pid <= 0 ? new String[] { vid + "" } : new String[] { pid + "" });
	}

	/**
	 * 保存播放记录
	 * */
	public synchronized void savePlayTrace(int cid, int pid, int vid, int nvid, String uid, int from, int vtype, long vtime, long htime, long utime,
			String title, String img, int state, int nc, String img300) {
		LogInfo.log("PlayRecord", "cid=" + cid + " pid=" + pid + " vid=" + vid + " nvid=" + nvid + " uid=" + uid + " from=" + from + " vtype=" + vtype
				+ " vtime=" + vtime + " htime=" + htime + " utime=" + utime + " title=" + title + " img=" + img + " state=" + state + " nc =" + nc);
		if (pid <= 0) {
			PlayRecord playRecord = getPlayTraceByEpsodeId(vid);
			if (playRecord != null) {
				if (playRecord.getVideoId() == vid && !TextUtils.isEmpty(playRecord.getImg300())) {//判断，原纪录和新纪录是一个视频，并且有300的图，就不替换了
					updateByVid(vid, nvid, from, htime, utime, title, img, state, playRecord.getImg300());
				} else {
					updateByVid(vid, nvid, from, htime, utime, title, img, state, img300);
				}
			} else {

				int type = 0;
				if (pid <= 0) {
					type = AlbumNew.Type.VRS_ONE;
				} else {
					if (pid == vid) {
						type = AlbumNew.Type.VRS_ONE;
					} else {
						type = AlbumNew.Type.VRS_MANG;
					}
				}

				ContentValues cv = new ContentValues();

				cv.put(LetvConstant.DataBase.PlayRecord.Field.CID, cid);
				cv.put(LetvConstant.DataBase.PlayRecord.Field.PID, pid <= 0 ? vid : pid);
				cv.put(LetvConstant.DataBase.PlayRecord.Field.VID, vid);
				cv.put(LetvConstant.DataBase.PlayRecord.Field.NVID, nvid);
				cv.put(LetvConstant.DataBase.PlayRecord.Field.UID, uid);
				cv.put(LetvConstant.DataBase.PlayRecord.Field.FROM, from);
				cv.put(LetvConstant.DataBase.PlayRecord.Field.VTYPE, vtype);
				cv.put(LetvConstant.DataBase.PlayRecord.Field.VTIME, vtime);
				cv.put(LetvConstant.DataBase.PlayRecord.Field.HTIME, htime);
				cv.put(LetvConstant.DataBase.PlayRecord.Field.UTIME, utime);
				cv.put(LetvConstant.DataBase.PlayRecord.Field.TITLE, title);
				cv.put(LetvConstant.DataBase.PlayRecord.Field.IMG, img);
				cv.put(LetvConstant.DataBase.PlayRecord.Field.IMG300, img300);
				cv.put(LetvConstant.DataBase.PlayRecord.Field.STATE, state);
				cv.put(LetvConstant.DataBase.PlayRecord.Field.NC, nc);
				cv.put(LetvConstant.DataBase.PlayRecord.Field.TYPE, type);

				context.getContentResolver().insert(LetvContentProvider.URI_PLAYTRACE, cv);
			}
		} else {
			PlayRecord playRecord = getPlayTraceByAlbumId(pid);
			if (playRecord != null) {
				if (playRecord.getVideoId() == vid && !TextUtils.isEmpty(playRecord.getImg300())) {//判断，原纪录和新纪录是一个视频，并且有300的图，就不替换了
					updateByVid(vid, nvid, from, htime, utime, title, img, state, playRecord.getImg300());
				} else {
					updateByVid(vid, nvid, from, htime, utime, title, img, state, img300);
				}
				updateByPid(pid, vid, nvid, from, htime, utime, title, img, state, img300);
			} else {

				int type = 0;
				if (pid <= 0) {
					type = AlbumNew.Type.VRS_ONE;
				} else {
					if (pid == vid) {
						type = AlbumNew.Type.VRS_ONE;
					} else {
						type = AlbumNew.Type.VRS_MANG;
					}
				}

				ContentValues cv = new ContentValues();

				cv.put(LetvConstant.DataBase.PlayRecord.Field.CID, cid);
				cv.put(LetvConstant.DataBase.PlayRecord.Field.PID, pid < 0 ? vid : pid);
				cv.put(LetvConstant.DataBase.PlayRecord.Field.VID, vid);
				cv.put(LetvConstant.DataBase.PlayRecord.Field.NVID, nvid);
				cv.put(LetvConstant.DataBase.PlayRecord.Field.UID, uid);
				cv.put(LetvConstant.DataBase.PlayRecord.Field.FROM, from);
				cv.put(LetvConstant.DataBase.PlayRecord.Field.VTYPE, vtype);
				cv.put(LetvConstant.DataBase.PlayRecord.Field.VTIME, vtime);
				cv.put(LetvConstant.DataBase.PlayRecord.Field.HTIME, htime);
				cv.put(LetvConstant.DataBase.PlayRecord.Field.UTIME, utime);
				cv.put(LetvConstant.DataBase.PlayRecord.Field.TITLE, title);
				cv.put(LetvConstant.DataBase.PlayRecord.Field.IMG, img);
				cv.put(LetvConstant.DataBase.PlayRecord.Field.IMG300, img300);
				cv.put(LetvConstant.DataBase.PlayRecord.Field.NC, nc);
				cv.put(LetvConstant.DataBase.PlayRecord.Field.STATE, state);
				cv.put(LetvConstant.DataBase.PlayRecord.Field.TYPE, type);

				context.getContentResolver().insert(LetvContentProvider.URI_PLAYTRACE, cv);
			}
		}
	}

	/**
	 * 通过VID或PID得到详情
	 * */
	public synchronized PlayRecord getPlayTrace(int id) {
		Cursor cursor = null;
		PlayRecord mPlayRecord = null;
		try {
			cursor = context.getContentResolver().query(LetvContentProvider.URI_PLAYTRACE, null,
					LetvConstant.DataBase.PlayRecord.Field.PID + "= ? OR " + LetvConstant.DataBase.PlayRecord.Field.VID + "= ?",
					new String[] { id + "", id + "" }, null);

			if (cursor != null && cursor.moveToNext()) {
				mPlayRecord = createPlayRecord(cursor);
			}

		} finally {
			LetvUtil.closeCursor(cursor);
		}

		return mPlayRecord;
	}

	/**
	 * 通过专辑ID得到播放记录
	 * */
	public synchronized PlayRecord getPlayTraceByAlbumId(int pid) {
		Cursor cursor = null;
		PlayRecord mPlayTrace = null;
		try {
			cursor = context.getContentResolver().query(LetvContentProvider.URI_PLAYTRACE, null, LetvConstant.DataBase.PlayRecord.Field.PID + "= ?",
					new String[] { pid + "" }, null);

			if (cursor != null && cursor.moveToNext()) {

				mPlayTrace = createPlayRecord(cursor);
			}

		} finally {
			LetvUtil.closeCursor(cursor);
		}

		return mPlayTrace;
	}

	/**
	 * 通过视频ID得到播放记录
	 * */
	public synchronized PlayRecord getPlayTraceByEpsodeId(int vid) {
		Cursor cursor = null;
		PlayRecord mPlayRecord = null;
		try {
			cursor = context.getContentResolver().query(LetvContentProvider.URI_PLAYTRACE, null, LetvConstant.DataBase.PlayRecord.Field.VID + "= ?",
					new String[] { vid + "" }, null);

			if (cursor != null && cursor.moveToNext()) {
				mPlayRecord = createPlayRecord(cursor);
			}

		} finally {
			LetvUtil.closeCursor(cursor);
		}

		return mPlayRecord;
	}

	/**
	 * 得到所有的播放记录
	 * */
	public synchronized PlayRecordList getAllPlayTrace() {
		Cursor cursor = null;
		try {
			cursor = context.getContentResolver().query(LetvContentProvider.URI_PLAYTRACE, null, LetvConstant.DataBase.PlayRecord.Field.STATE + "<> ?",
					new String[] { "2" }, LetvConstant.DataBase.PlayRecord.Field.UTIME + " desc");
			PlayRecordList list = new PlayRecordList();
			while (cursor.moveToNext()) {
				list.add(createPlayRecord(cursor));
			}

			return list;
		} finally {
			LetvUtil.closeCursor(cursor);
		}
	}

	/**
	 * 得到最近一条记录
	 * */
	public synchronized PlayRecord getLastPlayTrace() {
		Cursor cursor = null;
		try {
			cursor = context.getContentResolver().query(LetvContentProvider.URI_PLAYTRACE, null, LetvConstant.DataBase.PlayRecord.Field.STATE + "<> ?",
					new String[] { "2" }, LetvConstant.DataBase.PlayRecord.Field.UTIME + " desc");
			if (cursor.moveToNext()) {
				return createPlayRecord(cursor);
			}

			return null;
		} finally {
			LetvUtil.closeCursor(cursor);
		}
	}

	/**
	 * 清除所有播放记录
	 * */
	public synchronized void clearAll() {
		context.getContentResolver().delete(LetvContentProvider.URI_PLAYTRACE, null, null);
	}

	/**
	 * 标记清除所有播放记录，等待上传
	 * */
	public synchronized void tagClearAll() {
		ContentValues cv = new ContentValues();

		cv.put(LetvConstant.DataBase.PlayRecord.Field.UTIME, System.currentTimeMillis() / 1000);
		cv.put(LetvConstant.DataBase.PlayRecord.Field.STATE, "2");

		context.getContentResolver().update(LetvContentProvider.URI_PLAYTRACE, cv, null, null);
	}

	/**
	 * 清除所有播放记录
	 * */
	public synchronized void clearAllCloud() {
		context.getContentResolver().delete(LetvContentProvider.URI_PLAYTRACE, LetvConstant.DataBase.PlayRecord.Field.STATE + "= ?", new String[] { "0" });
	}

	/**
	 * 根据专辑ID删除专辑的播放记录
	 */
	public synchronized void deleteByPid(int pid) {
		context.getContentResolver().delete(LetvContentProvider.URI_PLAYTRACE, LetvConstant.DataBase.PlayRecord.Field.PID + "= ?", new String[] { pid + "" });
	}

	/**
	 * 根据视频ID删除一条的播放记录
	 */
	public synchronized void deleteByVid(int vid) {
		context.getContentResolver().delete(LetvContentProvider.URI_PLAYTRACE, LetvConstant.DataBase.PlayRecord.Field.VID + "= ?", new String[] { vid + "", });
	}

	/**
	 * 根据专辑ID删除专辑的播放记录
	 */
	public synchronized void tagDeleteByPid(int pid) {
		ContentValues cv = new ContentValues();

		cv.put(LetvConstant.DataBase.PlayRecord.Field.UTIME, System.currentTimeMillis() / 1000);
		cv.put(LetvConstant.DataBase.PlayRecord.Field.STATE, "2");

		context.getContentResolver()
				.update(LetvContentProvider.URI_PLAYTRACE, cv, LetvConstant.DataBase.PlayRecord.Field.PID + "=?", new String[] { pid + "" });
	}

	/**
	 * 根据视频ID删除一条的播放记录
	 */
	public synchronized void tagDeleteByVid(int vid) {
		ContentValues cv = new ContentValues();

		cv.put(LetvConstant.DataBase.PlayRecord.Field.UTIME, System.currentTimeMillis() / 1000);
		cv.put(LetvConstant.DataBase.PlayRecord.Field.STATE, "2");

		context.getContentResolver()
				.update(LetvContentProvider.URI_PLAYTRACE, cv, LetvConstant.DataBase.PlayRecord.Field.VID + "=?", new String[] { vid + "" });
	}

	public synchronized PlayRecordList getTagDeletes() {
		Cursor cursor = null;
		try {
			cursor = context.getContentResolver().query(LetvContentProvider.URI_PLAYTRACE, null, LetvConstant.DataBase.PlayRecord.Field.STATE + "== ?",
					new String[] { "2" }, null);
			PlayRecordList list = new PlayRecordList();
			while (cursor.moveToNext()) {
				list.add(createPlayRecord(cursor));
			}

			return list;
		} finally {
			LetvUtil.closeCursor(cursor);
		}
	}

	public synchronized PlayRecordList getTagSubmits() {
		Cursor cursor = null;
		try {
			cursor = context.getContentResolver().query(LetvContentProvider.URI_PLAYTRACE, null, LetvConstant.DataBase.PlayRecord.Field.STATE + "== ?",
					new String[] { "1" }, null);
			PlayRecordList list = new PlayRecordList();
			while (cursor.moveToNext()) {
				list.add(createPlayRecord(cursor));
			}

			return list;
		} finally {
			LetvUtil.closeCursor(cursor);
		}
	}

	/**
	 * 查询播放记录是否存在
	 */
	public synchronized long getPlayPostion(int vid) {
		Cursor cursor = null;
		try {
			long position = 0;

			cursor = context.getContentResolver().query(LetvContentProvider.URI_PLAYTRACE, new String[] { LetvConstant.DataBase.PlayRecord.Field.HTIME },
					LetvConstant.DataBase.PlayRecord.Field.VID + "= ?", new String[] { vid + "" }, null);

			if (cursor.moveToNext()) {
				position = cursor.getLong(cursor.getColumnIndex(LetvConstant.DataBase.PlayRecord.Field.HTIME));
			}
			return position;
		} finally {
			LetvUtil.closeCursor(cursor);
		}
	}

	/**
	 * 查询播放记录是否存在
	 */
	// public int getPlayEpisodeOrder(int pid) {
	// Cursor cursor = null;
	// try {
	// int vid = -1;
	//
	// cursor =
	// context.getContentResolver().query(LetvContentProvider.URI_PLAYTRACE,
	// new String[] { LetvConstant.DataBase.PlayRecord.Field.VID },
	// LetvConstant.DataBase.PlayRecord.Field.PID + "= ?", new String[] { pid +
	// "" }, null);
	//
	// if (cursor.moveToNext()) {
	// vid =
	// cursor.getInt(cursor.getColumnIndex(LetvConstant.DataBase.PlayRecord.Field.VID));
	// }
	//
	// return vid;
	// } finally {
	// LetvUtil.closeCursor(cursor);
	// }
	// }

	public synchronized boolean hasByPid(int pid) {
		Cursor cursor = null;
		try {
			cursor = context.getContentResolver().query(LetvContentProvider.URI_PLAYTRACE, new String[] { LetvConstant.DataBase.PlayRecord.Field.VID },
					LetvConstant.DataBase.PlayRecord.Field.PID + "= ?", new String[] { pid + "" }, null);
			return cursor.getCount() > 0;
		} finally {
			LetvUtil.closeCursor(cursor);
		}
	}

	public synchronized boolean hasByVid(int vid) {
		Cursor cursor = null;
		try {
			cursor = context.getContentResolver().query(LetvContentProvider.URI_PLAYTRACE, new String[] { LetvConstant.DataBase.PlayRecord.Field.VID },
					LetvConstant.DataBase.PlayRecord.Field.VID + "= ?", new String[] { vid + "" }, null);
			return cursor.getCount() > 0;
		} finally {
			LetvUtil.closeCursor(cursor);
		}
	}

	/**
	 * 通过专辑ID更新播放记录，存在专辑ID的情况
	 * */
	public synchronized void updateByPid(int pid, int vid, int nvid, int from, long htime, long utime, String title, String img, int state, String img300) {
		ContentValues cv = new ContentValues();

		cv.put(LetvConstant.DataBase.PlayRecord.Field.PID, pid);
		cv.put(LetvConstant.DataBase.PlayRecord.Field.VID, vid);
		cv.put(LetvConstant.DataBase.PlayRecord.Field.NVID, nvid);
		cv.put(LetvConstant.DataBase.PlayRecord.Field.FROM, from);
		cv.put(LetvConstant.DataBase.PlayRecord.Field.HTIME, htime);
		cv.put(LetvConstant.DataBase.PlayRecord.Field.UTIME, utime);
		cv.put(LetvConstant.DataBase.PlayRecord.Field.TITLE, title);
		cv.put(LetvConstant.DataBase.PlayRecord.Field.IMG, img);
		cv.put(LetvConstant.DataBase.PlayRecord.Field.IMG300, img300);
		cv.put(LetvConstant.DataBase.PlayRecord.Field.STATE, state);

		int i = context.getContentResolver().update(LetvContentProvider.URI_PLAYTRACE, cv,
				LetvConstant.DataBase.PlayRecord.Field.PID + "=? AND " + LetvConstant.DataBase.PlayRecord.Field.UTIME + " <=?",
				new String[] { pid + "", utime + "" });
	}

	/**
	 * 通过视频ID更新播放记录，不存在专辑ID的情况
	 * */
	public synchronized void updateByVid(int vid, int nvid, int from, long htime, long utime, String title, String img, int state, String img300) {

		ContentValues cv = new ContentValues();

		cv.put(LetvConstant.DataBase.PlayRecord.Field.VID, vid);
		cv.put(LetvConstant.DataBase.PlayRecord.Field.NVID, nvid);
		cv.put(LetvConstant.DataBase.PlayRecord.Field.FROM, from);
		cv.put(LetvConstant.DataBase.PlayRecord.Field.HTIME, htime);
		cv.put(LetvConstant.DataBase.PlayRecord.Field.UTIME, utime);
		cv.put(LetvConstant.DataBase.PlayRecord.Field.TITLE, title);
		cv.put(LetvConstant.DataBase.PlayRecord.Field.IMG, img);
		cv.put(LetvConstant.DataBase.PlayRecord.Field.IMG300, img300);
		cv.put(LetvConstant.DataBase.PlayRecord.Field.STATE, state);

		int i = context.getContentResolver().update(LetvContentProvider.URI_PLAYTRACE, cv,
				LetvConstant.DataBase.PlayRecord.Field.VID + "=? AND " + LetvConstant.DataBase.PlayRecord.Field.UTIME + " <=?",
				new String[] { vid + "", utime + "" });

	}

	private PlayRecord createPlayRecord(Cursor cursor) {
		PlayRecord record = new PlayRecord();

		record.setChannelId(cursor.getInt(cursor.getColumnIndex(LetvConstant.DataBase.PlayRecord.Field.CID)));
		record.setAlbumId(cursor.getInt(cursor.getColumnIndex(LetvConstant.DataBase.PlayRecord.Field.PID)));
		record.setVideoId(cursor.getInt(cursor.getColumnIndex(LetvConstant.DataBase.PlayRecord.Field.VID)));
		record.setVideoNextId(cursor.getInt(cursor.getColumnIndex(LetvConstant.DataBase.PlayRecord.Field.NVID)));
		record.setUserId(cursor.getString(cursor.getColumnIndex(LetvConstant.DataBase.PlayRecord.Field.UID)));
		record.setFrom(cursor.getInt(cursor.getColumnIndex(LetvConstant.DataBase.PlayRecord.Field.FROM)));
		record.setVideoType(cursor.getInt(cursor.getColumnIndex(LetvConstant.DataBase.PlayRecord.Field.VTYPE)));
		record.setType(cursor.getInt(cursor.getColumnIndex(LetvConstant.DataBase.PlayRecord.Field.TYPE)));
		record.setTotalDuration(cursor.getLong(cursor.getColumnIndex(LetvConstant.DataBase.PlayRecord.Field.VTIME)));
		record.setPlayedDuration(cursor.getLong(cursor.getColumnIndex(LetvConstant.DataBase.PlayRecord.Field.HTIME)));
		record.setUpdateTime(cursor.getLong(cursor.getColumnIndex(LetvConstant.DataBase.PlayRecord.Field.UTIME)));
		record.setTitle(cursor.getString(cursor.getColumnIndex(LetvConstant.DataBase.PlayRecord.Field.TITLE)));
		record.setImg(cursor.getString(cursor.getColumnIndex(LetvConstant.DataBase.PlayRecord.Field.IMG)));
		record.setImg300(cursor.getString(cursor.getColumnIndex(LetvConstant.DataBase.PlayRecord.Field.IMG300)));
		record.setCurEpsoid(cursor.getInt(cursor.getColumnIndex(LetvConstant.DataBase.PlayRecord.Field.NC)));

		return record;
	}
}
