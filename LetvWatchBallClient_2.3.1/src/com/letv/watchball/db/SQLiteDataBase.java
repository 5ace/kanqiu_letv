package com.letv.watchball.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.letv.watchball.utils.LetvConstant;

public class SQLiteDataBase extends SQLiteOpenHelper {

	/**
	 * 数据库版本号
	 * 
	 * 1.0 ---------------------- 10 
	 * 2.0------------------------20
	 * 2.2------------------------22
	 */
	private static final int DATABASE_VERSION = 22;

	/**
	 * 数据库名称
	 */
	public static final String DATABASE_NAME = "letvWatchBall.db";

	public SQLiteDataBase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		
		/**
		 * 直播预定数据库
		 * */
		createTable_subscribeGameTrace(db);
		/**
		 * 创建播放记录表
		 * */
		createTable_playTrace(db);
		/**
		 * 客户端多页面显示缓存 数据库
		 */
		createTable_LocalCacheTrace(db);
		/**
		 * 客户端提示语服务端化数据库
		 */
		createTable_DialogMsgTrace(db);
		/**
		 * 开机广告图
		 * */
		createTable_PushAdImage(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		if(oldVersion == 10){
			/**
			 * 1.0 数据库
			 */
			createTable_playTrace(db);
			createTable_LocalCacheTrace(db);
			createTable_DialogMsgTrace(db);
			createTable_PushAdImage(db);
		}
		
		if(oldVersion == 20){
			/**
			 * 2.0数据库
			 * */
			createTable_PushAdImage(db);
		}
	}

	/**
	 * 客户端开机广告
	 */
	private void createTable_PushAdImage(SQLiteDatabase db){
		db.execSQL("CREATE TABLE " + LetvConstant.DataBase.PushAdImageTrace.TABLE_NAME + "(" + LetvConstant.DataBase.PushAdImageTrace.Field.ID
				+ " INTEGER PRIMARY KEY," + LetvConstant.DataBase.PushAdImageTrace.Field.IMAGEURL + " TEXT ," 
				+ LetvConstant.DataBase.PushAdImageTrace.Field.CREATETIME + " TEXT ,"
				+ LetvConstant.DataBase.PushAdImageTrace.Field.MTIME + " TEXT"
				+ ");");
	}
	
	/**
	 * 客户端多页面显示本地缓存
	 */
	private void createTable_LocalCacheTrace(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + LetvConstant.DataBase.LocalCacheTrace.TABLE_NAME + "(" + LetvConstant.DataBase.LocalCacheTrace.Field.CACHEID
				+ " TEXT PRIMARY KEY," + LetvConstant.DataBase.LocalCacheTrace.Field.ASSISTKEY + " TEXT," + LetvConstant.DataBase.LocalCacheTrace.Field.MARKID
				+ " TEXT," + LetvConstant.DataBase.LocalCacheTrace.Field.CACHETIME + " TEXT," + LetvConstant.DataBase.LocalCacheTrace.Field.CACHEDATA + " TEXT"
				+ ");");
	}
	
	private void createTable_playTrace(SQLiteDatabase db) {

		db.execSQL("CREATE TABLE " + LetvConstant.DataBase.PlayRecord.TABLE_NAME + "(" + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ LetvConstant.DataBase.PlayRecord.Field.CID + " INTEGER," + LetvConstant.DataBase.PlayRecord.Field.IMG300 + " TEXT,"
				+ LetvConstant.DataBase.PlayRecord.Field.PID + " INTEGER," + LetvConstant.DataBase.PlayRecord.Field.VID + " INTEGER,"
				+ LetvConstant.DataBase.PlayRecord.Field.NVID + " INTEGER," + LetvConstant.DataBase.PlayRecord.Field.UID + " TEXT,"
				+ LetvConstant.DataBase.PlayRecord.Field.VTYPE + " INTEGER," + LetvConstant.DataBase.PlayRecord.Field.FROM + " INTEGER,"
				+ LetvConstant.DataBase.PlayRecord.Field.VTIME + " INTEGER," + LetvConstant.DataBase.PlayRecord.Field.HTIME + " INTEGER,"
				+ LetvConstant.DataBase.PlayRecord.Field.UTIME + " INTEGER," + LetvConstant.DataBase.PlayRecord.Field.STATE + " INTEGER,"
				+ LetvConstant.DataBase.PlayRecord.Field.TYPE + " INTEGER," + LetvConstant.DataBase.PlayRecord.Field.TITLE + " TEXT,"
				+ LetvConstant.DataBase.PlayRecord.Field.IMG + " TEXT," + LetvConstant.DataBase.PlayRecord.Field.NC + " TEXT " + ");");
	}
	
	/**
	 * 直播预定
	 */
	private void createTable_subscribeGameTrace(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + LetvConstant.DataBase.SubscribeGameTrace.TABLE_NAME + "("
				+ LetvConstant.DataBase.SubscribeGameTrace.Field.id + " TEXT PRIMARY KEY,"
				+ LetvConstant.DataBase.SubscribeGameTrace.Field.level + " TEXT,"
				+ LetvConstant.DataBase.SubscribeGameTrace.Field.home + " TEXT,"
				+ LetvConstant.DataBase.SubscribeGameTrace.Field.guest + " TEXT,"
				+ LetvConstant.DataBase.SubscribeGameTrace.Field.status + " INTEGER DEFAULT 0,"
				+ LetvConstant.DataBase.SubscribeGameTrace.Field.isNotify + " INTEGER DEFAULT 0,"
				+ LetvConstant.DataBase.SubscribeGameTrace.Field.isPushResult + " INTEGER DEFAULT 0,"
				+ LetvConstant.DataBase.SubscribeGameTrace.Field.playDate + " TEXT,"
				+ LetvConstant.DataBase.SubscribeGameTrace.Field.playTime + " TEXT,"
				+ LetvConstant.DataBase.SubscribeGameTrace.Field.playTimeMillisecond + " INTEGER" + ");");
	}
	/**
	 * 客户端提示语服务端化
	 */
	private void createTable_DialogMsgTrace(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + LetvConstant.DataBase.DialogMsgTrace.TABLE_NAME + "(" + LetvConstant.DataBase.DialogMsgTrace.Field.MSGID
				+ " TEXT PRIMARY KEY," + LetvConstant.DataBase.DialogMsgTrace.Field.MAGTITLE + " TEXT," + LetvConstant.DataBase.DialogMsgTrace.Field.MESSAGE
				+ " TEXT" + ");");
	}

}
