package com.letv.watchball.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.letv.watchball.bean.MessageBean;
import com.letv.watchball.bean.MessageBeanListMap;
import com.letv.watchball.utils.LetvConstant;
import com.letv.watchball.utils.LetvTools;

/**
 * 客户端提示语服务端化
 * 
 * @author haitian
 * 
 */
public class DialogMsgTraceHandler {
	private Context context;

	public DialogMsgTraceHandler(Context context) {
		this.context = context;
	}

	/**
	 * 保存记录
	 * 
	 * @param msgId
	 * @param msgTitle
	 * @param msg
	 */
	public synchronized void saveDialogMsg(String msgId, String msgTitle, String msg) {
		if (hasByMsgId(msgId)) {
			updateByMsgId(msgId, msgTitle, msg);
		} else {
			ContentValues cv = new ContentValues();
			cv.put(LetvConstant.DataBase.DialogMsgTrace.Field.MSGID, msgId);
			cv.put(LetvConstant.DataBase.DialogMsgTrace.Field.MAGTITLE, msgTitle);
			cv.put(LetvConstant.DataBase.DialogMsgTrace.Field.MESSAGE, msg);
			context.getContentResolver().insert(LetvContentProvider.URI_DIALOGMSGTRACE, cv);
		}
	}

	/**
	 * 根据 msgId 更新数据表
	 * 
	 * @param msgId
	 * @param msgTitle
	 * @param msg
	 */
	private void updateByMsgId(String msgId, String msgTitle, String msg) {
		// TODO Auto-generated method stub
		ContentValues cv = new ContentValues();

		cv.put(LetvConstant.DataBase.DialogMsgTrace.Field.MSGID, msgId);
		cv.put(LetvConstant.DataBase.DialogMsgTrace.Field.MAGTITLE, msgTitle);
		cv.put(LetvConstant.DataBase.DialogMsgTrace.Field.MESSAGE, msg);

		context.getContentResolver().update(LetvContentProvider.URI_DIALOGMSGTRACE, cv,
				LetvConstant.DataBase.DialogMsgTrace.Field.MSGID + "=?", new String[] { msgId });
	}

	public synchronized MessageBean getDialogMsgByMsgId(String msgId) {
		Cursor cursor = null;
		MessageBean mMessageBean = null;
		try {
			cursor = context.getContentResolver().query(LetvContentProvider.URI_DIALOGMSGTRACE, null,
					LetvConstant.DataBase.DialogMsgTrace.Field.MSGID + "= ?", new String[] { msgId },
					null);

			if (cursor != null && cursor.moveToNext()) {

				mMessageBean = new MessageBean(
						cursor.getString(cursor
								.getColumnIndex(LetvConstant.DataBase.DialogMsgTrace.Field.MSGID)),
						cursor.getString(cursor
								.getColumnIndex(LetvConstant.DataBase.DialogMsgTrace.Field.MAGTITLE)),
						cursor.getString(cursor
								.getColumnIndex(LetvConstant.DataBase.DialogMsgTrace.Field.MESSAGE)));
			}

		} finally {
			LetvTools.closeCursor(cursor);
		}
//		LetvHttpLog.Err("mMessageBean.toString ="+mMessageBean.toString());
		return mMessageBean;
	}
	/**
	 * 得到所有的记录
	 * @return
	 */
	public synchronized MessageBeanListMap getAllDialogMsg() {
		Cursor cursor = null;
		try {
			cursor = context.getContentResolver()
					.query(LetvContentProvider.URI_DIALOGMSGTRACE, null,
							LetvConstant.DataBase.DialogMsgTrace.Field.MESSAGE + "<> ?",
							new String[] { "" },
							LetvConstant.DataBase.DialogMsgTrace.Field.MSGID + " desc");
			MessageBeanListMap list = new MessageBeanListMap();
			MessageBean mMessageBean = null;
			while (cursor != null && cursor.moveToNext()) {
				mMessageBean = new MessageBean(
						cursor.getString(cursor
								.getColumnIndex(LetvConstant.DataBase.DialogMsgTrace.Field.MSGID)),
						cursor.getString(cursor
								.getColumnIndex(LetvConstant.DataBase.DialogMsgTrace.Field.MAGTITLE)),
						cursor.getString(cursor
								.getColumnIndex(LetvConstant.DataBase.DialogMsgTrace.Field.MESSAGE)));

				list.put(mMessageBean.msgId, mMessageBean);
			}
			return list;
		} finally {
			LetvTools.closeCursor(cursor);
		}
	}
	public synchronized boolean getDialogMsgSize(){
		Cursor cursor = null;
		try {
			cursor = context.getContentResolver()
					.query(LetvContentProvider.URI_DIALOGMSGTRACE, null,
							LetvConstant.DataBase.DialogMsgTrace.Field.MESSAGE + "<> ?",
							new String[] { "" },
							LetvConstant.DataBase.DialogMsgTrace.Field.MSGID + " desc");
			if(cursor != null && cursor.moveToNext()){
				return cursor.getCount()>20?true:false;
			}else {
				return false;
			}
		} finally {
			LetvTools.closeCursor(cursor);
		}
	}
	/**
	 * 清除所有记录
	 * */
	public synchronized void clearAll() {
		context.getContentResolver().delete(LetvContentProvider.URI_DIALOGMSGTRACE, null, null);
	}

	/**
	 * 根据msgId删除一条的记录
	 */
	public synchronized void deleteByVid(String msgId) {
		context.getContentResolver().delete(LetvContentProvider.URI_DIALOGMSGTRACE,
				LetvConstant.DataBase.DialogMsgTrace.Field.MSGID + "= ?", new String[] { msgId });
	}

	/**
	 * 根据msgId查询数据表是否有记录
	 * 
	 * @param msgId
	 * @return
	 */
	public synchronized boolean hasByMsgId(String msgId) {
		Cursor cursor = null;
		try {
			cursor = context.getContentResolver().query(LetvContentProvider.URI_DIALOGMSGTRACE,
					new String[] { LetvConstant.DataBase.DialogMsgTrace.Field.MSGID },
					LetvConstant.DataBase.DialogMsgTrace.Field.MSGID + "= ?",
					new String[] { msgId }, null);
			return cursor.getCount() > 0;
		} finally {
			LetvTools.closeCursor(cursor);
		}
	}
}
