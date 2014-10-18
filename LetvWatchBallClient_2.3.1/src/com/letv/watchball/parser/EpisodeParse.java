package com.letv.watchball.parser;

import org.json.JSONException;
import org.json.JSONObject;

import com.letv.watchball.bean.Episode;

/**
 * 视频对象解析
 * */
public class EpisodeParse extends LetvMobileParser<Episode> {

	/**
	 * 视频名称
	 * */
	private final String TITLE = "title";
	/**
	 * 上映时间，(支持格式：2011 | 2011-06 | 2011-06-13)
	 * */
	private final String RELEASEDATE = "releasedate";
	/**
	 * 视频类型：1,正片 2,预告片 3,花絮 4,资讯 5,其他
	 * */
	private final String VIDEOTYPE = "videotype";
	/**
	 * 视频id
	 * */
	private final String VID = "vid";
	/**
	 * 媒体资源id
	 * */
	private final String MMSID = "mmsid";
	/**
	 * 该视频支持的码率，多个值使用半角逗号","分隔；码率取值：350,1000,1300
	 * */
	private final String BRLIST = "brList";
	/**
	 * 该视频是否允许下载：1-表示允许，2-表示不允许
	 * */
	private final String ALLOWNDOWNLOAD = "allownDownload";
	/**
	 * 该视频是否允许下载：1-表示允许，2-表示不允许
	 * */
	private final String BTIME = "btime";
	/**
	 * 该视频是否允许下载：1-表示允许，2-表示不允许
	 * */
	private final String ETIME = "etime";
	/**
	 * 该视频是否允许下载：1-表示允许，2-表示不允许
	 * */
	private final String DURATION = "duration";
	/**
	 * 该视频是否允许下载：1-表示允许，2-表示不允许
	 * */
//	private final String ICON = "icon";
	
	/**
	 * 是否需要支付:1-否，2-是 字段
	 * */
	private final String PAY = "pay" ;

	@Override
	public Episode parse(JSONObject data) throws JSONException {
		Episode episode = new Episode();
		episode.setTitle(getString(data, TITLE));
		episode.setReleasedate(getString(data, RELEASEDATE));
		episode.setVideotype(getInt(data, VIDEOTYPE));
		episode.setVid(getInt(data, VID));
		episode.setMmsid(getInt(data, MMSID));
		episode.setBrList(getString(data, BRLIST));
		episode.setAllownDownload(getInt(data, ALLOWNDOWNLOAD));
		episode.setBtime(getLong(data, BTIME));
		episode.setEtime(getLong(data, ETIME));
		episode.setDuration(getLong(data, DURATION));
//		episode.setIcon(getString(data, ICON));
		
		if(has(data, PAY)){
			episode.setPay(getInt(data,PAY));
		}else{
			episode.setPay(1);//默认不付费
		}

		return episode;
	}

}
