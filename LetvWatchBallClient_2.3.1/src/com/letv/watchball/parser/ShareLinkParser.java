package com.letv.watchball.parser;

import org.json.JSONException;
import org.json.JSONObject;

import com.letv.watchball.bean.Share;
/**
 * 分享接口解析
 * */
public class ShareLinkParser extends LetvMobileParser<Share>{

	private final String ALBUM_URL = "album_url" ;
	private final String VIDEO_URL = "video_url" ;
	@Override
	public Share parse(JSONObject data) throws JSONException {
		
		Share share = new Share();
		share.setAlbum_url(getString(data, ALBUM_URL));
//		share.setAlbumEnd(parseEndUrl(getString(data, ALBUM_URL)));
		
		share.setVideo_url(getString(data, VIDEO_URL));
//		share.setVideoEnd(parseEndUrl(getString(data, VIDEO_URL)));
		
		
//		LetvHttpLog.Err(ALBUM_URL+"----"+share.getAlbum_url());
//		LetvHttpLog.Err(VIDEO_URL+"----"+share.getVideo_url());
		return share;
	}
//	private String parseUrl(String url) {
//		String urlTmp = url.substring(0,url.indexOf("{"));
//		return urlTmp;
//	}
//	private String parseEndUrl(String url) {
//		String endUrl = url.substring(url.lastIndexOf("."));
//		return endUrl;
//	}
}
