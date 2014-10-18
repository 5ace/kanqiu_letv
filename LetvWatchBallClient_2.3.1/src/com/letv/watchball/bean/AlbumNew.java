package com.letv.watchball.bean;

import com.letv.http.bean.LetvBaseBean;

public class AlbumNew implements LetvBaseBean {

	private static final long serialVersionUID = 1L;
	
	public static class Copyright {
		/**
		 * 0-有播放和下载版权
		 */
		public static final String ALL_P_D = "0";
		/**
		 * 3-无播放和下载版权
		 */
		public static final String NEITHER_P_D = "3";
	}

	public static class Channel {
		// 1 电影
		public static final int TYPE_MOVIE = 1;
		// 2 电视剧
		public static final int TYPE_TV = 2;
		// 3 娱乐
		public static final int TYPE_JOY = 3;
		// 4 体育
		public static final int TYPE_PE = 4;
		// 5 动漫
		public static final int TYPE_CARTOON = 5;
		// 6 资讯
		public static final int TYPE_INFORMATION = 6;
		// 7 原创
		public static final int TYPE_ORIGINAL = 7;
		// 8 其他
		public static final int TYPE_OTHERS = 8;
		// 9 音乐
		public static final int TYPE_MUSIC = 9;
		// 10 搞笑
		public static final int TYPE_FUNNY = 10;
		// 11 综艺
		public static final int TYPE_TVSHOW = 11;
		// 12 科教
		public static final int TYPE_SCIENCE = 12;
		// 13 生活
		public static final int TYPE_LIFE = 13;
		// 14 汽车
		public static final int TYPE_CAR = 14;
		// 15 电视节目
		public static final int TYPE_TVPROGRAM = 15;
		// 16 纪录片
		public static final int TYPE_DOCUMENT_FILM = 16;
		// 17 公开课
		public static final int TYPE_OPEN_CLASS = 17;
		// 19 乐视制造
		public static final int TYPE_LETV_MAKE = 19;
		// 20 风尚
		public static final int TYPE_FASHION = 20;
		// 22 财经频道
		public static final int TYPE_FINANCIAL = 22;
		// 23 旅游频道
		public static final int TYPE_TOURISM = 23;
		/**
		 * 乐视出品
		 * */
		public static final int TYPE_LETV_PRODUCT = 202;
		/**
		 * 杜比频道CID
		 */
		public static final int TYPE_DOLBY = 1001;
		/**
		 * 会员频道
		 */
		public static final int TYPE_VIP = 1000;
		/**
		 * H265频道 
		 * wangxuefang 高清频道  这个是客户端自己加的，服务端并不存在高清频道ID
		 */
		public static final int TYPE_H265 = 2001;
	}

	private long id;

	private String nameCn;

	private String albumType;

	private String subTitle;

	private String pic;
	
	private String pic300;

	private float score;

	private int cid;

	private int type;

	private int at;

      private long pid;

	private String releaseDate;

	/**
	 * 推送到移动平台总集数（合并的情况，包含所有正片和花絮等）
	 * 用于翻页
	 * */
	private int platformVideoNum;
	
	/**
	 * 推送到移动平台总集数（不合并的情况，只包含正片）
	 * 用于翻页
	 * */
	private int platformVideoInfo ;

	/**
	 * 运营填写的总集数
	 * 用于UI显示
	 * */
	private int episode;

	/**
	 * 推到移动端更新的集数
	 * 用于UI显示
	 * */
	private int nowEpisodes;

	private int isEnd;

	private long duration;

	private String directory;

	private String starring;

	private String description;

	private String area;

	private String language;

	private String instructor;

	private String subCategory;

	private String style;

	private String playTv;

	private String school;

	private String controlAreas;

	private int disableType;

	private int play;

	private int jump;

	private int pay;

	private int download;
	
	private String compere ;
	
	private String tag ;
	
	private String travelType ;
	
	//========================================
	/**
	 * 专辑类型：42-电影、43-微电影
	 */
	private int filmstyle;
	private long aid;
	private String icon_400_300;
	private String icon_200_150;
	private long vid;
	private int isDolby;// 0不是杜比，1是杜比
	//========================================
	
	public long getId() {
		return id;
	}
	/**
	 * @return the vid
	 */
	public long getVid() {
		return vid;
	}

	/**
	 * @param vid the vid to set
	 */
	public void setVid(long vid) {
		this.vid = vid;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getNameCn() {
		return nameCn;
	}

	public void setNameCn(String nameCn) {
		this.nameCn = nameCn;
	}

	public String getAlbumType() {
		return albumType;
	}

	public void setAlbumType(String albumType) {
		this.albumType = albumType;
	}

	public String getSubTitle() {
		return subTitle;
	}

	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	public String getPic300() {
		return pic300;
	}
	
	public void setPic300(String pic300) {
		this.pic300 = pic300;
	}
	
	public float getScore() {
		return score;
	}

	public void setScore(float score) {
		this.score = score;
	}

	public int getCid() {
		return cid;
	}

	public void setCid(int cid) {
		this.cid = cid;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getAt() {
		return at;
	}

	public void setAt(int at) {
		this.at = at;
	}

	public String getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(String releaseDate) {
		this.releaseDate = releaseDate;
	}

	public int getPlatformVideoNum() {
		return platformVideoNum;
	}

	public void setPlatformVideoNum(int platformVideoNum) {
		this.platformVideoNum = platformVideoNum;
	}

	public int getPlatformVideoInfo() {
		return platformVideoInfo;
	}
	public void setPlatformVideoInfo(int platformVideoInfo) {
		this.platformVideoInfo = platformVideoInfo;
	}
	
	public int getEpisode() {
		return episode;
	}

	public void setEpisode(int episode) {
		this.episode = episode;
	}

	public int getNowEpisodes() {
		return nowEpisodes;
	}

	public void setNowEpisodes(int nowEpisodes) {
		this.nowEpisodes = nowEpisodes;
	}

	public int getIsEnd() {
		return isEnd;
	}

	public void setIsEnd(int isEnd) {
		this.isEnd = isEnd;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}

	public void starring(String directory) {
		this.directory = directory;
	}

	public String getStarring() {
		return starring;
	}

	public void setStarring(String starring) {
		this.starring = starring;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getInstructor() {
		return instructor;
	}

	public void setInstructor(String instructor) {
		this.instructor = instructor;
	}

	public String getSubCategory() {
		return subCategory;
	}

	public void setSubCategory(String subCategory) {
		this.subCategory = subCategory;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getPlayTv() {
		return playTv;
	}

	public void setPlayTv(String playTv) {
		this.playTv = playTv;
	}

	public String getSchool() {
		return school;
	}

	public void setSchool(String school) {
		this.school = school;
	}

	public String getControlAreas() {
		return controlAreas;
	}

	public void setControlAreas(String controlAreas) {
		this.controlAreas = controlAreas;
	}

	public int getDisableType() {
		return disableType;
	}

	public void setDisableType(int disableType) {
		this.disableType = disableType;
	}

	public int getPlay() {
		return play;
	}

	public void setPlay(int play) {
		this.play = play;
	}
	
	public boolean canPlay(){
		return this.play == 1 ;
	}

	public int getJump() {
		return jump;
	}

	public void setJump(int jump) {
		this.jump = jump;
	}

	public int getPay() {
		return pay;
	}

	public void setPay(int pay) {
		this.pay = pay;
	}

	public int getDownload() {
		return download;
	}

	public void setDownload(int download) {
		this.download = download;
	}
	
	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getTravelType() {
		return travelType;
	}

	public void setTravelType(String travelType) {
		this.travelType = travelType;
	}

	public String getCompere() {
		return compere;
	}

	public void setCompere(String compere) {
		this.compere = compere;
	}

	public boolean needJump() {
		return jump == 1;
	}

	public boolean needPay() {
		return pay == 1;
	}
	
	public boolean canDownload() {
		return download == 1;
	}

	public int getFilmstyle() {
		return filmstyle;
	}

	public void setFilmstyle(int filmstyle) {
		this.filmstyle = filmstyle;
	}

	public String getIcon_400_300() {
		return icon_400_300;
	}

	public void setIcon_400_300(String icon_400_300) {
		this.icon_400_300 = icon_400_300;
	}

	public String getIcon_200_150() {
		return icon_200_150;
	}

	public void setIcon_200_150(String icon_200_150) {
		this.icon_200_150 = icon_200_150;
	}

	public long getAid() {
		return aid;
	}

	public void setAid(long aid) {
		this.aid = aid;
	}
	public int getIsDolby() {
		return isDolby;
	}

	public void setIsDolby(int isDolby) {
		this.isDolby = isDolby;
	}

      public long getPid() {
            return pid;
      }

      public void setPid(long pid) {
            this.pid = pid;
      }

      /**
	 * 影片来源
	 * */
	public static class Type {
		/**
		 * 专辑来源 vrs专辑
		 */
		public static final int VRS_MANG = 1;
		/**
		 * 专辑来源 vrs 视频
		 */
		public static final int VRS_ONE = 3;
		/**
		 * 专辑来源 PTV 视频
		 */
		public static final int PTV = 2;
	}
	/**
	 * 新老频道ID映射（会员频道与杜比频道暂时未加映射） 会员频道与杜比频道 不用变化,还是用原值
	 * */
//	public static int channelMappingToNew(int channelId) {
//		switch (channelId) {
//		case Album.Channel.TYPE_MOVIE:
//			return Album.NewChannel.TYPE_MOVIE;
//
//		case Album.Channel.TYPE_TV:
//			return Album.NewChannel.TYPE_TV;
//
//		case Album.Channel.TYPE_CARTOON:
//			return Album.NewChannel.TYPE_CARTOON;
//
//		case Album.Channel.TYPE_MUSIC:
//			return Album.NewChannel.TYPE_MUSIC;
//
//		case Album.Channel.TYPE_TVSHOW:
//			return Album.NewChannel.TYPE_TVSHOW;
//
//		case Album.Channel.TYPE_JOY:
//			return Album.NewChannel.TYPE_JOY;
//
//		case Album.Channel.TYPE_DOCUMENT_FILM:
//			return Album.NewChannel.TYPE_DOCUMENT_FILM;
//
//		case Album.Channel.TYPE_OPEN_CLASS:
//			return Album.NewChannel.TYPE_OPEN_CLASS;
//
//		case Album.Channel.TYPE_LETV_MAKE:
//			return Album.NewChannel.TYPE_LETV_MAKE;
//
//		case Album.Channel.TYPE_FASHION:
//			return Album.NewChannel.TYPE_FASHION;
//
//		case Album.Channel.TYPE_PE:
//			return Album.NewChannel.TYPE_PE;
//
//		case Album.Channel.TYPE_CAR:
//			return Album.NewChannel.TYPE_CAR;
//
//		case Album.Channel.TYPE_TOURISM:
//			return Album.NewChannel.TYPE_TOURISM;
//
//		case Album.Channel.TYPE_FINANCIAL:
//			return Album.NewChannel.TYPE_FINANCIAL;
//		}
//
//		return channelId;
//	}
}
