package com.letv.watchball.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.letv.watchball.utils.LetvConstant;

public class LetvContentProvider extends ContentProvider {

	public static final String AUTHORITY = "com.letv.watchball.db.LetvContentProvider";

	public static final Uri URI_SUBSCRIBE_GAME_TRACE = Uri.parse("content://" + AUTHORITY + "/"
			+ LetvConstant.DataBase.SubscribeGameTrace.TABLE_NAME);
	public static final Uri URI_LOCALCACHETRACE = Uri.parse("content://" + AUTHORITY + "/"
			+ LetvConstant.DataBase.LocalCacheTrace.TABLE_NAME);
	
	public static final Uri URI_PLAYTRACE = Uri.parse("content://" + AUTHORITY + "/"
			+ LetvConstant.DataBase.PlayRecord.TABLE_NAME);
	public static final Uri URI_DIALOGMSGTRACE = Uri.parse("content://" + AUTHORITY + "/"
			+ LetvConstant.DataBase.DialogMsgTrace.TABLE_NAME);
	public static final Uri URI_PUSHADIAMGECACHETRACE = Uri.parse("content://" + AUTHORITY + "/"
			+ LetvConstant.DataBase.PushAdImageTrace.TABLE_NAME);

	private static final int PLAY_TRACE = 102;
	private static final int SUBSCRIBE_GAME_TRACE = 106;
	private static final int DIALOGMSG_TRACE = 107;
	private static final int LOCALCACHE_TRACE = 108;
	private static final int PUSHADIMAGECACHE_TRACE = 109;

	private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

	private SQLiteDataBase sqliteDataBase;

	static {
		URI_MATCHER.addURI(AUTHORITY, LetvConstant.DataBase.SubscribeGameTrace.TABLE_NAME, SUBSCRIBE_GAME_TRACE);
		URI_MATCHER.addURI(AUTHORITY, LetvConstant.DataBase.LocalCacheTrace.TABLE_NAME, LOCALCACHE_TRACE);
		URI_MATCHER.addURI(AUTHORITY, LetvConstant.DataBase.PlayRecord.TABLE_NAME, PLAY_TRACE);
		URI_MATCHER.addURI(AUTHORITY, LetvConstant.DataBase.DialogMsgTrace.TABLE_NAME, DIALOGMSG_TRACE);
		URI_MATCHER.addURI(AUTHORITY, LetvConstant.DataBase.PushAdImageTrace.TABLE_NAME, PUSHADIMAGECACHE_TRACE);
	}

	@Override
	public boolean onCreate() {
		sqliteDataBase = new SQLiteDataBase(getContext());
		return true;
	}

	@Override
	public String getType(Uri uri) {

		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {

		long rowId;
		Uri newUri = null;

		int match = URI_MATCHER.match(uri);

		SQLiteDatabase db = sqliteDataBase.getWritableDatabase();

		switch (match) {

		case SUBSCRIBE_GAME_TRACE:
			rowId = db.insert(LetvConstant.DataBase.SubscribeGameTrace.TABLE_NAME, null, values);
			if (rowId > 0) {
				newUri = ContentUris.withAppendedId(URI_SUBSCRIBE_GAME_TRACE, rowId);
			}
			break;
		case PLAY_TRACE:
			rowId = db.insert(LetvConstant.DataBase.PlayRecord.TABLE_NAME, null, values);
			if (rowId > 0) {
				newUri = ContentUris.withAppendedId(URI_PLAYTRACE, rowId);
			}
			break;
		case LOCALCACHE_TRACE:
			rowId = db.insert(LetvConstant.DataBase.LocalCacheTrace.TABLE_NAME, null, values);
			if (rowId > 0) {
				newUri = ContentUris.withAppendedId(URI_LOCALCACHETRACE, rowId);
			}
			break;
		case DIALOGMSG_TRACE:
			rowId = db.insert(LetvConstant.DataBase.DialogMsgTrace.TABLE_NAME, null, values);
			if (rowId > 0) {
				newUri = ContentUris.withAppendedId(URI_DIALOGMSGTRACE, rowId);
			}
			break;
		case PUSHADIMAGECACHE_TRACE:
			rowId = db.insert(LetvConstant.DataBase.PushAdImageTrace.TABLE_NAME, null, values);
			if (rowId > 0) {
				newUri = ContentUris.withAppendedId(URI_PUSHADIAMGECACHETRACE, rowId);
			}
			break;
		default:
			throw new UnsupportedOperationException("Unknown or unsupported URL: " + uri.toString());

		}

		if (newUri != null) {
			getContext().getContentResolver().notifyChange(uri, null);
		}

		return newUri;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {

		int count;

		int match = URI_MATCHER.match(uri);
		SQLiteDatabase db = sqliteDataBase.getWritableDatabase();

		switch (match) {
		case SUBSCRIBE_GAME_TRACE:
			count = db.delete(LetvConstant.DataBase.SubscribeGameTrace.TABLE_NAME, selection, selectionArgs);
			break;
		case PLAY_TRACE:
			count = db.delete(LetvConstant.DataBase.PlayRecord.TABLE_NAME, selection, selectionArgs);
			break;
		case LOCALCACHE_TRACE:
			count = db.delete(LetvConstant.DataBase.LocalCacheTrace.TABLE_NAME, selection, selectionArgs);
			break;
		case DIALOGMSG_TRACE:
			count = db.delete(LetvConstant.DataBase.DialogMsgTrace.TABLE_NAME, selection, selectionArgs);
			break;
		case PUSHADIMAGECACHE_TRACE:
			count = db.delete(LetvConstant.DataBase.PushAdImageTrace.TABLE_NAME, selection, selectionArgs);
			break;
		default:
			throw new UnsupportedOperationException("Unknown or unsupported URL: " + uri.toString());
		}

		this.getContext().getContentResolver().notifyChange(uri, null);

		return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

		int count;
		int match = URI_MATCHER.match(uri);
		SQLiteDatabase db = sqliteDataBase.getWritableDatabase();

		switch (match) {
		case SUBSCRIBE_GAME_TRACE:
			count = db.update(LetvConstant.DataBase.SubscribeGameTrace.TABLE_NAME, values, selection,
					selectionArgs);
			break;
		case PLAY_TRACE:
			count = db.update(LetvConstant.DataBase.PlayRecord.TABLE_NAME, values, selection, selectionArgs);
			break;
		case LOCALCACHE_TRACE:
			count = db.update(LetvConstant.DataBase.LocalCacheTrace.TABLE_NAME, values, selection,
					selectionArgs);
			break;
		case DIALOGMSG_TRACE:
			count = db.update(LetvConstant.DataBase.DialogMsgTrace.TABLE_NAME, values, selection,
					selectionArgs);
			break;
		case PUSHADIMAGECACHE_TRACE:
			count = db.update(LetvConstant.DataBase.PushAdImageTrace.TABLE_NAME, values, selection,
					selectionArgs);
			break;
		default:
			throw new UnsupportedOperationException("Unknown or unsupported URL: " + uri.toString());
		}

		this.getContext().getContentResolver().notifyChange(uri, null);

		return count;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
			String sortOrder) {

		Cursor cursor = null;
		int match = URI_MATCHER.match(uri);
		SQLiteDatabase db = sqliteDataBase.getWritableDatabase();
		switch (match) {
		case SUBSCRIBE_GAME_TRACE:
			cursor = db.query(LetvConstant.DataBase.SubscribeGameTrace.TABLE_NAME, null, selection, selectionArgs,
					null, null, sortOrder);
			cursor.setNotificationUri(getContext().getContentResolver(), uri);
			break;
		case PLAY_TRACE:
			cursor = db.query(LetvConstant.DataBase.PlayRecord.TABLE_NAME, null, selection, selectionArgs,
					null, null, sortOrder);
			cursor.setNotificationUri(getContext().getContentResolver(), uri);
			break;
		case LOCALCACHE_TRACE:
			cursor = db.query(LetvConstant.DataBase.LocalCacheTrace.TABLE_NAME, null, selection, selectionArgs,
					null, null, sortOrder);
			cursor.setNotificationUri(getContext().getContentResolver(), uri);
			break;
		case DIALOGMSG_TRACE:
			cursor = db.query(LetvConstant.DataBase.DialogMsgTrace.TABLE_NAME, null, selection, selectionArgs,
					null, null, sortOrder);
			cursor.setNotificationUri(getContext().getContentResolver(), uri);
			break;
		case PUSHADIMAGECACHE_TRACE:
			cursor = db.query(LetvConstant.DataBase.PushAdImageTrace.TABLE_NAME, null, selection, selectionArgs,
					null, null, sortOrder);
			cursor.setNotificationUri(getContext().getContentResolver(), uri);
			break;
		default:
			throw new UnsupportedOperationException("Unknown or unsupported URL: " + uri.toString());
		}
		return cursor;
	}
}
