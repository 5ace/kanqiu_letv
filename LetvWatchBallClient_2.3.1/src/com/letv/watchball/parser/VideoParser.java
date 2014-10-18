package com.letv.watchball.parser;

import org.json.JSONObject;

import com.letv.watchball.bean.Video;

public class VideoParser extends LetvMobileParser<Video> {

	@Override
	public Video parse(JSONObject data) throws Exception {

		if (data != null) {
			Video video = new Video();

			video.setId(getLong(data, "id"));
			video.setNameCn(getString(data, "nameCn"));
			video.setSubTitle(getString(data, "subTitle"));
			video.setPic(getString(getJSONObject(data, "picAll"), "120*90"));
			video.setBtime(getLong(data, "btime"));
			video.setEtime(getLong(data, "etime"));
			video.setCid(getInt(data, "cid"));
			video.setPid(getLong(data, "pid"));
			video.setType(getInt(data, "type"));
			video.setAt(getInt(data, "at"));
			video.setReleaseDate(getString(data, "releaseDate"));
			video.setDuration(getLong(data, "duration"));
			video.setStyle(getInt(data, "style"));
			video.setPlay(getInt(data, "play"));
			video.setJump(getInt(data, "jump"));
			video.setPay(getInt(data, "pay"));
			video.setDownload(getInt(data, "download"));
			video.setDescription(getString(data, "description"));
			video.setControlAreas(getString(data, "controlAreas"));
			video.setDisableType(getInt(data, "disableType"));
			video.setMid(getString(data, "mid"));
			video.setBrList(getString(data, "brList"));
			video.setEpisode(getInt(data, "episode"));

			return video;
		}

		return null;
	}

}
