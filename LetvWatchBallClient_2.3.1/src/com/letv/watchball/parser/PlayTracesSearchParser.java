package com.letv.watchball.parser;

import org.json.JSONException;
import org.json.JSONObject;

import com.letv.watchball.bean.PlayRecord;

public class PlayTracesSearchParser extends LetvMobileParser<PlayRecord>{

	private String pid ;
	private String vid ;
	public void setPid(String pid) {
		this.pid = pid;
	}
	public void setVid(String vid) {
		this.vid = vid;
	}
	public PlayTracesSearchParser(String vid, String pid){
		this.pid = pid;
		this.vid = vid;
	}
	@Override
	public PlayRecord parse(JSONObject data) throws Exception {
		
		if(data.has("pid")){
			data = getJSONObject(data, "pid");
			if(data.has(pid + "")){
				data = getJSONObject(data, pid);
				return getPlayTrace(data) ;
			}
		}else if(data.has("vid")) {
			data = getJSONObject(data, "vid");
			if(data.has(vid + "")){
				data = getJSONObject(data, vid);
				return getPlayTrace(data) ;
			}
		}
		return null;
	}
	
	private PlayRecord getPlayTrace(JSONObject data) throws JSONException {
		PlayRecord record = new PlayRecord();
		record.setChannelId(getInt(data, "cid"));
		record.setAlbumId(getInt(data, "pid"));
		record.setVideoNextId(getInt(data, "nvid"));
		record.setVideoType(getInt(data, "vtype"));
		record.setFrom(getInt(data, "from"));
		record.setPlayedDuration(getLong(data, "htime"));
		record.setUpdateTime(getLong(data, "utime"));
		record.setVideoId(getInt(data, "vid"));
		record.setTitle(getString(data, "title"));
		record.setImg(getString(data, "img"));
		return record;
	}
}
