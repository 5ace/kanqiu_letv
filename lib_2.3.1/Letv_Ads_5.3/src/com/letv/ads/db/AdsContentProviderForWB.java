package com.letv.ads.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class AdsContentProviderForWB extends ContentProvider {

	public static final String AUTHORITY = "com.letv.ads.db.AdsContentProviderForWB";

	public static final Uri URI_ADS = Uri.parse("content://" + AUTHORITY + "/" + DBConstant.TABLE_NAME);

	private static final int ADS = 100;

	private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

	private SQLiteDataBase sqliteDataBase;

	static {
		URI_MATCHER.addURI(AUTHORITY, DBConstant.TABLE_NAME, ADS);
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
		case ADS:
			rowId = db.insert(DBConstant.TABLE_NAME , DBConstant.AD, values);
			if (rowId > 0) {
				newUri = ContentUris.withAppendedId(URI_ADS, rowId);
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
		case ADS:
			count = db.delete(DBConstant.TABLE_NAME, selection, selectionArgs);
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
		case ADS:
			count = db.update(DBConstant.TABLE_NAME, values, selection, selectionArgs);
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
		case ADS:
			cursor = db.query(DBConstant.TABLE_NAME, null, selection, selectionArgs,null, null, sortOrder);
			cursor.setNotificationUri(getContext().getContentResolver(), uri);
			break;
		default:
			throw new UnsupportedOperationException("Unknown or unsupported URL: " + uri.toString());
		}
		return cursor;
	}
}
