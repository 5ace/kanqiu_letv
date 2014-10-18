package com.letv.ads.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteDataBase extends SQLiteOpenHelper {

	/**
	 * 数据库版本号
	 * */
	private static final int DATABASE_VERSION = 2;

	/**
	 * 数据库名称
	 */
	public static final String DATABASE_NAME = "ads.db";

	public SQLiteDataBase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		createTable(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if(oldVersion == 1){
			db.execSQL("drop table if exists " + DBConstant.TABLE_NAME);
			createTable(db);
		}
	}
	
	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if(newVersion == 2){
			
		}
	}

	/**
	 * 创建广告表
	 */
	private void createTable(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + DBConstant.TABLE_NAME + "(" 
										+ DBConstant.AD + " TEXT PRIMARY KEY," 
										+ DBConstant.CONTENT + " TEXT" + ");");
	}
}
