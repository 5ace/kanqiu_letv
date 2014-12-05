package com.letv.watchball.parser;

import org.json.JSONArray;
import org.json.JSONObject;

import com.letv.watchball.bean.PlayRecord;
import com.letv.watchball.bean.PlayRecordList;

public class PlayRecordParser extends LetvMobileParser<PlayRecordList> {

	@Override
	public PlayRecordList parse(JSONObject data) throws Exception {

		PlayRecordList list = new PlayRecordList();

		list.setPage(getInt(data, "page"));
		list.setPagesize(getInt(data, "pagesize"));
		list.setTotal(getInt(data, "total"));

		JSONArray array = getJSONArray(data, "items");

		for (int i = 0; i < array.length(); i++) {
			JSONObject object = getJSONObject(array, i);
			if (object != null) {
				PlayRecord record = new PlayRecord();

				record.setChannelId(getInt(object, "cid"));
				record.setAlbumId(getInt(object, "pid"));
				record.setVideoId(getInt(object, "vid"));
				record.setVideoNextId(getInt(object, "nvid"));
				record.setUserId(getString(object, "uid"));
				record.setFrom(getInt(object, "from"));
				record.setVideoType(getInt(object, "vtype"));
				record.setTotalDuration(getLong(object, "vtime"));
				record.setPlayedDuration(getLong(object, "htime"));
				record.setUpdateTime(getLong(object, "utime"));
				record.setTitle(getString(object, "title"));
				record.setImg(getString(object, "img"));
				if (has(object, "picAll")) {
					JSONObject obj = getJSONObject(object, "picAll");
					record.setImg300(getString(obj, "300*300"));
				}

				list.add(record);
			}
		}

		return list;
	}
}
