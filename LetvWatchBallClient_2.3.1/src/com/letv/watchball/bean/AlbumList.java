package com.letv.watchball.bean;

import java.util.ArrayList;

import com.letv.http.bean.LetvBaseBean;
import com.letv.watchball.http.api.LetvHttpApi;

/**
 * 专辑对象
 * 请求:
 * {@link LetvHttpApi#requestVRSAlbums(int, String, String, String, String, String, String, String, String, com.letv.exp.parser.LetvMainParser)}  
 * {@link LetvHttpApi#requestVRSVideos(int, String, String, String, String, String, String, String, String, com.letv.exp.parser.LetvMainParser)}
 * {@link LetvHttpApi#requestPTVVideos(int, String, String, String, com.letv.exp.parser.LetvMainParser)}
 * 解析:
 * {@link AlbumListParse}
 * */
public class AlbumList extends ArrayList<Album> implements LetvBaseBean{
	
	private static final long serialVersionUID = 1L;

	/**
	 * 最大值
	 */
	private int max ;
	
	/**
	 * 是否有热点
	 */
	private boolean isHasHot = false;

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}
	
	public boolean isHasHot() {
		return isHasHot;
	}

	public void setHasHot(boolean isHasHot) {
		this.isHasHot = isHasHot;
	}
}
