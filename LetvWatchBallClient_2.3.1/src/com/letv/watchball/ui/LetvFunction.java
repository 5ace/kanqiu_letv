package com.letv.watchball.ui;

import com.letv.watchball.bean.AlbumNew;

public class LetvFunction {

	/**
	 * 根据频道和style区分merge 0 不合并 ， 1 合并
	 * */
	public static int getMerge(String style) {
		return "1".equals(style) ? 0 : 1;
	}
	/**
	 * 根据style区分视频列表是否列表形势
	 * */
	public static boolean getIsList(String style) {
		return "1".equals(style) ? false : true;
	}

	/**
	 * 根据频道和style区分merge 0 不合并 ， 1 合并
	 * */
	public static String getOrder(int cid) {
		return cid == AlbumNew.Channel.TYPE_TVSHOW ? "1" : "-1";
	}
	/**
	 * 根据数量，计算行数(起点1)
	 * */
	public static int calculateRows(int index, int rowSize) {
		int a = index % rowSize;
		int b = index / rowSize;

		return a == 0 ? b : b + 1;
	}
}
