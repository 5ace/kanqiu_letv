package com.letv.watchball.parser;

import org.json.JSONException;
import org.json.JSONObject;

import com.letv.watchball.bean.VideoFile;

public class VideoFileParser extends LetvMobileParser<VideoFile> {

	private final String VIDEOFILE = "videofile";

	private final String INFOS = "infos";

	private String MP4_350 = "mp4_350";

	private String MP4_1000 = "mp4_1000";

	private String MP4_1300 = "mp4_1300";

	// add dolby type

	private final String MP4_800_DB = "mp4_800_db";

	private final String MP4_1300_DB = "mp4_1300_db";

	private final String MP4_720P_DB = "mp4_720p_db";

	private final String MP4_1080P6M_DB = "mp4_1080p6m_db";

	private final String MMSID = "mmsid";

	private boolean needPay;

	public VideoFileParser(boolean needPay) {
		this.needPay = needPay;
	}

	@Override
	public VideoFile parse(JSONObject data) throws JSONException {

		VideoFile videoFile = null;
		data = getJSONObject(data, VIDEOFILE);

		videoFile = new VideoFile();

		videoFile.setMmsid(getString(data, MMSID));

		data = getJSONObject(data, INFOS);

		if (has(data, MP4_350)) {
			videoFile.setMp4_350(new VideoSchedulingAddressParser(needPay)
					.parse(getJSONObject(data, MP4_350)));
		}

		if (has(data, MP4_1000)) {
			videoFile.setMp4_1000(new VideoSchedulingAddressParser(needPay)
					.parse(getJSONObject(data, MP4_1000)));
		}

		if (has(data, MP4_1300)) {
			videoFile.setMp4_1300(new VideoSchedulingAddressParser(needPay)
					.parse(getJSONObject(data, MP4_1300)));
		}

		if (has(data, MP4_800_DB)) {
			videoFile.setMp4_800_db(new VideoSchedulingAddressParser(needPay)
					.parse(getJSONObject(data, MP4_800_DB)));
		}

		if (has(data, MP4_1300_DB)) {
			videoFile.setMp4_1300_db(new VideoSchedulingAddressParser(needPay)
					.parse(getJSONObject(data, MP4_1300_DB)));
		}

		if (has(data, MP4_720P_DB)) {
			videoFile.setMp4_720p_db(new VideoSchedulingAddressParser(needPay)
					.parse(getJSONObject(data, MP4_720P_DB)));
		}

		if (has(data, MP4_1080P6M_DB)) {
			videoFile.setMp4_1080p6m_db(new VideoSchedulingAddressParser(
					needPay).parse(getJSONObject(data, MP4_1080P6M_DB)));
		}

		return videoFile;
	}

}
