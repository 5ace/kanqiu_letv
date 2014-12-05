package com.letv.watchball.parser;

import org.json.JSONObject;

import android.text.TextUtils;

import com.letv.watchball.bean.AlbumNew;

/**
 * 专辑对象解析器
 * */
public class AlbumNewParse extends LetvMobileParser<AlbumNew> {
	public interface FROM {
		/**
		 * 杜比列表
		 * */
		int DOLBY_CHANNEL_LIST_ALBUMS = 0x110;
		int DOLBY_CHANNEL_LIST_VIDEOS = 0x111;
		/**
		 * 普通专辑，视频列表
		 * */
		int COMMON_CHANNEL_LIST_ALBUMS = 0x102;
		int COMMON_CHANNEL_LIST_VIDEOS = 0x103;
		int COMMON_CHANNEL_LIST = 0x104;
		/**
		 * 普通详情
		 * */
		int COMMON_DETAILS = 0x105;
	}

	public AlbumNewParse(int from) {
		super(from);
	}

	public AlbumNewParse() {
		super(AlbumNewParse.FROM.COMMON_DETAILS);
	}

	@Override
	public AlbumNew parse(JSONObject data) throws Exception {

		try {
			if (data != null) {
				AlbumNew album = new AlbumNew();
				int from = getFrom();
				switch (from) {
				case AlbumNewParse.FROM.DOLBY_CHANNEL_LIST_ALBUMS:
				case AlbumNewParse.FROM.DOLBY_CHANNEL_LIST_VIDEOS:
					album.setIsDolby(1);
				case AlbumNewParse.FROM.COMMON_CHANNEL_LIST_ALBUMS:
				case AlbumNewParse.FROM.COMMON_CHANNEL_LIST_VIDEOS:
					album.setType(AlbumNew.Type.VRS_MANG);
					if (from == AlbumNewParse.FROM.DOLBY_CHANNEL_LIST_VIDEOS
							|| from == AlbumNewParse.FROM.COMMON_CHANNEL_LIST_VIDEOS) {
						album.setType(AlbumNew.Type.VRS_ONE);
					}
					album.setId(getInt(data, "aid"));
					album.setVid(getInt(data, "vid"));
					album.setNameCn(getString(data, "name"));
					album.setSubTitle(getString(data, "subname"));

					album.setIcon_200_150(getString(
							getJSONObject(data, "images"), "200*150"));
					album.setIcon_400_300(getString(
							getJSONObject(data, "images"), "400*300"));
					album.setPic(album.getIcon_400_300());
					album.setCid(getInt(data, "category"));
					album.setReleaseDate(getString(data, "year"));
					album.setEpisode(getInt(data, "episode"));
					int count = getInt(data, "nowEpisodes");
					if (count <= 0) {
						count = 0;
					}
					album.setNowEpisodes(count);
					album.setIsEnd(getInt(data, "isEnd"));
					album.setJump(getInt(data, "jump"));

					album.setPay(getInt(data, "pay"));

					if (has(data, "id")) {
						int aid = getInt(data, "id");
						if (aid <= 0) {
							album.setAid(album.getId());
						} else {
							album.setAid(aid);
						}
					} else {
						album.setAid(album.getId());
					}
					break;
				case AlbumNewParse.FROM.COMMON_CHANNEL_LIST:
					album.setId(getInt(data, "id"));
					album.setNameCn(getString(data, "title"));
					album.setSubTitle(getString(data, "subtitle"));
					album.setPic(getString(data, "icon"));
					if (has(data, "icon_400x300")) {
						album.setIcon_400_300(getString(data, "icon_400x300"));
					} else {
						album.setIcon_400_300(null);
					}
					if (has(data, "icon_200x150")) {
						album.setIcon_200_150(getString(data, "icon_200x150"));
					} else {
						album.setIcon_200_150(null);
					}
					album.setScore(getFloat(data, "score"));
					album.setCid(getInt(data, "cid"));
					album.setType(getInt(data, "type"));
					album.setAt(getInt(data, "at"));
					album.setReleaseDate(getString(data, "year"));

					count = getInt(data, "count");
					if (count <= 0) {
						count = 0;
					}
					album.setNowEpisodes(count);
					album.setIsEnd(getInt(data, "isend"));

					album.setDuration(getLong(data, "time_length"));
					album.setDirectory(getString(data, "director"));
					album.setStarring(getString(data, "actor"));
					album.setDescription(getString(data, "intro"));
					album.setArea(getString(data, "area"));

					album.setStyle(getString(data, "style"));
					album.setPlayTv(getString(data, "tv"));
					album.setSchool(getString(data, "rcompany"));
					album.setJump(getInt(data, "needJump"));

					if (has(data, "pay")) {
						if (getInt(data, "pay") == 1) {
							album.setPay(0);
						} else {
							album.setPay(1);
						}
					} else {
						album.setPay(0);// 默认不付费
					}

					if (has(data, "aid")) {
						int aid = getInt(data, "aid");
						if (aid <= 0) {
							album.setAid(album.getId());
						} else {
							album.setAid(aid);
						}
					} else {
						album.setAid(album.getId());
					}
					if (has(data, "filmstyle")) {
						album.setFilmstyle(getInt(data, "filmstyle"));
					}
					break;
				default:
					album.setId(getLong(data, "id"));
					album.setNameCn(getString(data, "nameCn"));
					album.setPid(getLong(data, "pid"));
					album.setAlbumType(getString(data, "albumType"));
					album.setSubTitle(getString(data, "subTitle"));
					if (has(data, "picCollections")) {
						album.setPic(getString(
								getJSONObject(data, "picCollections"),
								"150*200"));
					} else if (has(data, "picAll")) {
						album.setPic(getString(getJSONObject(data, "picAll"),
								"120*90"));
					}
					album.setScore(getFloat(data, "score"));
					album.setCid(getInt(data, "cid"));
					album.setType(getInt(data, "type"));
					album.setAt(getInt(data, "at"));
					album.setReleaseDate(getString(data, "releaseDate"));
					album.setPlatformVideoNum(getInt(data, "platformVideoNum"));
					album.setPlatformVideoInfo(getInt(data, "platformVideoInfo"));
					album.setEpisode(getInt(data, "episode"));
					album.setNowEpisodes(getInt(data, "nowEpisodes"));
					album.setIsEnd(getInt(data, "isEnd"));
					album.setDuration(getLong(data, "duration"));
					album.setDirectory(getString(data, "directory"));
					album.setStarring(getString(data, "starring"));

					album.setDescription(getString(data, "description"));
					album.setArea(getString(data, "area"));
					album.setLanguage(getString(data, "language"));
					album.setInstructor(getString(data, "instructor"));
					album.setSubCategory(getString(data, "subCategory"));
					album.setStyle(getString(data, "style"));
					album.setPlayTv(getString(data, "playTv"));
					album.setSchool(getString(data, "school"));
					album.setControlAreas(getString(data, "controlAreas"));
					album.setDisableType(getInt(data, "disableType"));
					album.setPlay(getInt(data, "play"));
					album.setJump(getInt(data, "jump"));
					album.setPay(getInt(data, "pay"));
					album.setDownload(getInt(data, "download"));
					album.setTag(getString(data, "tag"));
					album.setTravelType(getString(data, "travelType"));

					/**
					 * 首页播放记录区块图片，首选300*300
					 */
					// JSONObject picAll = null;
					// if (has(data, "picCollections")) {
					// picAll = getJSONObject(data, "picCollections");
					// } else if (has(data, "picAll")) {
					// picAll = getJSONObject(data, "picAll");
					// }
					// if (picAll != null) {
					// String str = getString(picAll, "300*300");
					// if (!TextUtils.isEmpty(str)) {
					// album.setPic300(str);
					// } else {
					// str = getString(picAll, "400*300");
					// if (!TextUtils.isEmpty(str)) {
					// album.setPic300(str);
					// } else {
					// album.setPic300(album.getPic());
					// }
					// }
					// }
					break;
				}
				return album;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
