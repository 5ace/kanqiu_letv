package com.letv.watchball.bean;


import java.util.List;

import com.letv.http.bean.LetvBaseBean;
import com.letv.watchball.R;
import com.letv.watchball.parser.AlbumParse;
import com.letv.watchball.utils.LetvUtil;
/**
 * 专辑对象
 * 解析{@link AlbumParse}
 * */
public class Album implements LetvBaseBean{
	
	/**
	 * 版权状态
	 * */
	public static class Copyright {
		/**
		 * 0-有海外播放和下载版权
		 */
		public static final String ALL = "0";
		/**
		 * 1-无海外播放版权
		 */
		public static final String NO_OVERSEAS_PLAY = "1";
		/**
		 * 2-无海外下载版权
		 */
		public static final String NO_OVERSEAS_DOWNLOAD = "2";
		/**
		 * 3-无海外播放和下载版权
		 */
		public static final String NEITHER = "3";
	}

	/**
	 * 详情页面类型
	 * */
	public static class Style {
		/**
		 * 剧集形式
		 */
		public static final String EPISODE = "1";
		/**
		 * 列表形式
		 */
		public static final String LIST = "2";
		/**
		 * 单片形式
		 */
		public static final String SINGLE = "3";
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
	 * 点击展示
	 * */
	public static class At {
		/**
		 * 进详情
		 */
		public static final int DETAIL = 1;
		/**
		 * 直接播放
		 */
		public static final int PLAY = 2;
		/**
		 * 跳出页面
		 */
		public static final int WEB = 3;
		/**
		 * 跳到客户端内专题
		 */
		public static final int SPECIAL = 4;
		/**
		 * 跳到客户端WebView
		 */
		public static final int WEB_INSIDE = 5;
		/**
		 * 直播
		 */
		public static final int LIVE = 6;
		/**
		 * 跳到客户端精品推荐页
		 */
		public static final int Recommend = 7;
		/**
		 * 电视台直播
		 */
		public static final int TV_LIVE = 8;
	}

	/**
	 * 专辑属性
	 * */
	public static class AlbumStyle {
		/**
		 * 电影
		 */
		public static final String MOVIE = "23";
		/**
		 * 电视剧
		 */
		public static final String TVSHOW = "24";

	}

	/**
	 * 频道类型
	 * */
	public static class Channel {
		/**
		 * 电影
		 */
		public static final int TYPE_MOVIE = 4;
		/**
		 * 电视剧
		 */
		public static final int TYPE_TV = 5;
		/**
		 * 动画片
		 */
		public static final int TYPE_CARTOON = 6;
		/**
		 * 音乐
		 */
		public static final int TYPE_MUSIC = 66;
		/**
		 * 综艺
		 */
		public static final int TYPE_TVSHOW = 78;
		/**
		 * 娱乐
		 */
		public static final int TYPE_JOY = 86;
		/**
		 * 纪录片
		 * */
		public static final int TYPE_DOCUMENT_FILM = 111;
		/**
		 * 公开课
		 */
		public static final int TYPE_OPEN_CLASS = 92;
		/**
		 * 乐视制造
		 * */
		public static final int TYPE_LETV_MAKE = 164;
		/**
		 * 乐视出品
		 * */
		public static final int TYPE_LETV_PRODUCT = 202;
		/**
		 * 风尚
		 */
		public static final int TYPE_FASHION = 186;
		/**
		 * 体育
		 */
		public static final int TYPE_PE = 221;
	}

	/**
	 * 盖章类型
	 * */
	public class Stamp {
		/**
		 * 最新
		 * */
		public static final int NEW = 0;
		/**
		 * 最热
		 * */
		public static final int HOT = 1;
		/**
		 * 独播
		 * */
		public static final int EXCLUSIVE = 2;
		/**
		 * 大结局
		 * */
		public static final int FINAL = 3;
		/**
		 * 花絮
		 * */
		public static final int TITBITS = 4;
		/**
		 * 预告
		 * */
		public static final int PREVUE = 5;
		/**
		 * 高清
		 * */
		public static final int CLEAR = 6;
		/**
		 * 完结
		 * */
		public static final int END = 7;
		/**
		 * 完结
		 * */
		public static final int CLASSIC = 8;
	}
	
	private int id;
	
	private int aid;

	private String title;

	private String urlTitle;
	
	private String urlIntro;
	
	private String urlTvCode;

	private String subTitle;

	private String icon;

	private String icon_2;

	private String url_pic_2;

	private String url;
	
	private String url_350;

	private String rec_icon_1;

	private float score;

	private int cid ;

	private int type;

	private int at;

	private String year;

	private int count;

	private boolean isEnd;

	private long timeLength;

	private String director;

	private String actor;

	private String intro;

	private String area;

	private String subcate;

	private String style;

	private String tv;

	private String rcompany;

	private String language;

	private long position;

	private String ctime;

	private String albumtype;

	private int albumstyle;

	private String allowforeign;

	private int stamp = -1;
	
	private int albumtype_stamp ;
	
	private double singleprice ;
	
	private int allowmonth = -1;
	
	private String paydate ;
	
	private int order ;
	
	private String tags ;
	
	private String playcount ;
	
	private String starringtype ;
	
	private String albumstyleName ;
	
	private int needJump = 1; 
	
	private List<Episode> epsiodes ;

	private AlbumList correlationLoves ;

	private AlbumList correlationDirectors ;

	private AlbumList correlationActors ;
	
	private int pay = 1;
	
	private String listInfo01 ;
	
	private String listInfo02 ;
	
	private String json ;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrlTitle() {
		return urlTitle;
	}

	public void setUrlTitle(String urlTitle) {
		this.urlTitle = urlTitle;
	}

	public String getUrlIntro() {
		return urlIntro;
	}

	public void setUrlIntro(String urlIntro) {
		this.urlIntro = urlIntro;
	}

	public String getUrlTvCode() {
		return urlTvCode;
	}

	public void setUrlTvCode(String urlTvCode) {
		this.urlTvCode = urlTvCode;
	}

	public String getSubTitle() {
		return subTitle;
	}

	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getIcon_2() {
		if (Album.At.WEB == at || Album.At.WEB_INSIDE == at) {
			return url_pic_2;
		} else {
			return icon_2;
		}
	}

	public void setIcon_2(String icon_2) {
		this.icon_2 = icon_2;
	}

	public String getUrl_pic_2() {
		return url_pic_2;
	}

	public void setUrl_pic_2(String url_pic_2) {
		this.url_pic_2 = url_pic_2;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrl_350() {
		return url_350;
	}

	public void setUrl_350(String url_350) {
		this.url_350 = url_350;
	}

	public String getRec_icon_1() {
		return rec_icon_1;
	}

	public void setRec_icon_1(String rec_icon_1) {
		this.rec_icon_1 = rec_icon_1;
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

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public boolean isEnd() {
		return isEnd;
	}

	public void setEnd(boolean isSend) {
		this.isEnd = isSend;
	}

	public long getTimeLength() {
		return timeLength;
	}

	public void setTimeLength(long timeLength) {
		this.timeLength = timeLength;
	}

	public String getDirector() {
		return director;
	}

	public void setDirector(String director) {
		this.director = director;
	}

	public String getActor() {
		return actor;
	}

	public void setActor(String actor) {
		this.actor = actor;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getSubcate() {
		return subcate;
	}

	public void setSubcate(String subcate) {
		this.subcate = subcate;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getTv() {
		return tv;
	}

	public void setTv(String tv) {
		this.tv = tv;
	}

	public String getRcompany() {
		return rcompany;
	}

	public void setRcompany(String rcompany) {
		this.rcompany = rcompany;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public long getPosition() {
		return position;
	}

	public void setPosition(long position) {
		this.position = position;
	}

	public String getCtime() {
		return ctime;
	}

	public void setCtime(String ctime) {
		this.ctime = ctime;
	}

	public String getAlbumtype() {
		return albumtype;
	}

	public void setAlbumtype(String albumtype) {
		this.albumtype = albumtype;
	}

	public int getAlbumstyle() {
		return albumstyle;
	}

	public void setAlbumstyle(int albumstyle) {
		this.albumstyle = albumstyle;
	}

	public String getAllowforeign() {
		return allowforeign;
	}

	public void setAllowforeign(String allowforeign) {
		this.allowforeign = allowforeign;
	}

	public int getStamp() {
		return stamp;
	}

	public void setStamp(int stamp) {
		this.stamp = stamp;
	}

	public int getAlbumtype_stamp() {
		return albumtype_stamp;
	}

	public void setAlbumtype_stamp(int albumtype_stamp) {
		this.albumtype_stamp = albumtype_stamp;
	}

	public double getSingleprice() {
		return singleprice;
	}

	public void setSingleprice(double singleprice) {
		this.singleprice = singleprice;
	}

	public int getAllowmonth() {
		return allowmonth;
	}

	public void setAllowmonth(int allowmonth) {
		this.allowmonth = allowmonth;
	}

	public String getPaydate() {
		return paydate;
	}

	public void setPaydate(String paydate) {
		this.paydate = paydate;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public String getPlaycount() {
		return playcount;
	}

	public void setPlaycount(String playcount) {
		this.playcount = playcount;
	}

	public List<Episode> getEpsiodes() {
		return epsiodes;
	}

	public void setEpsiodes(List<Episode> epsiodes) {
		this.epsiodes = epsiodes;
	}

	public AlbumList getCorrelationLoves() {
		return correlationLoves;
	}

	public void setCorrelationLoves(AlbumList correlationLoves) {
		this.correlationLoves = correlationLoves;
	}

	public AlbumList getCorrelationDirectors() {
		return correlationDirectors;
	}

	public void setCorrelationDirectors(AlbumList correlationDirectors) {
		this.correlationDirectors = correlationDirectors;
	}

	public AlbumList getCorrelationActors() {
		return correlationActors;
	}

	public void setCorrelationActors(AlbumList correlationActors) {
		this.correlationActors = correlationActors;
	}

	public String getStarringtype() {
		return starringtype;
	}

	public void setStarringtype(String starringtype) {
		this.starringtype = starringtype;
	}

	public String getAlbumstyleName() {
		return albumstyleName;
	}

	public void setAlbumstyleName(String albumstyleName) {
		this.albumstyleName = albumstyleName;
	}

	public int getNeedJump() {
		return needJump;
	}

	public void setNeedJump(int needJump) {
		this.needJump = needJump;
	}

	public int getPay() {
		return pay;
	}

	public void setPay(int pay) {
		this.pay = pay;
	}
	
	public boolean needPay(){
		return pay == 2;
	}

	public String getListInfo01() {
		return listInfo01;
	}

	public void setListInfo01(String listInfo01) {
		this.listInfo01 = listInfo01;
	}

	public String getListInfo02() {
		return listInfo02;
	}

	public void setListInfo02(String listInfo02) {
		this.listInfo02 = listInfo02;
	}
	
	public String getTimeLengthString() {
		return LetvUtil.getNumberTime(this.timeLength);
	}
	
	public void setJsonString(String json){
		this.json = json ;
	}
	
	public String getJsonString(){
		return json ;
	}
	
	public boolean isNeedJump() {
		return needJump == 2;
	}
	
	public int getAid() {
		return aid;
	}

	public void setAid(int aid) {
		this.aid = aid;
	}

	public void createInfoString(){
		
//		switch (this.getCid()) {
//		case  Album.Channel.TYPE_MOVIE:  //电影
//		case  Album.Channel.TYPE_TV://电视剧
//		case  Album.Channel.TYPE_LETV_PRODUCT://乐视出品
//			if(this.getType() == Album.Type.VRS_ONE || this.getType() == Album.Type.PTV){
//				this.listInfo01 = LetvApplication.getInstance().getString(R.string.channellist_albumdetailTime,this.getTimeLengthString());
//				this.listInfo02 = "";
//			}else{
//				this.listInfo01 = LetvApplication.getInstance().getString(R.string.channellist_director, this.getDirector());
//				this.listInfo02 = LetvApplication.getInstance().getString(R.string.channellist_actor, this.getActor());
//			}
//			break;
//		case  Album.Channel.TYPE_CARTOON://动漫
//		case  Album.Channel.TYPE_DOCUMENT_FILM://纪录片
//			if(this.getType() == Album.Type.VRS_ONE || this.getType() == Album.Type.PTV){
//				this.listInfo01 = LetvApplication.getInstance().getString(R.string.channellist_albumdetailTime,this.getTimeLengthString());
//				this.listInfo02 = "";
//			}else{
//				this.listInfo01 = LetvApplication.getInstance().getString(R.string.channellist_area, this.getArea());
//				this.listInfo02 = LetvApplication.getInstance().getString(R.string.channellist_type, this.getSubcate());
//			}
//		    break;
//		case  Album.Channel.TYPE_TVSHOW://综艺
//			if(this.getType() == Album.Type.VRS_ONE || this.getType() == Album.Type.PTV){
//				this.listInfo01 = LetvApplication.getInstance().getString(R.string.channellist_albumdetailTime,this.getTimeLengthString());
//				this.listInfo02 = "";
//			}else{
//				this.listInfo01 = LetvApplication.getInstance().getString(R.string.channellist_countUpdate, this.getCount());
//				this.listInfo02 = LetvApplication.getInstance().getString(R.string.channellist_type, this.getSubcate());
//			}
//			break;
//		case  Album.Channel.TYPE_LETV_MAKE://乐视制造
//			if(this.getType() == Album.Type.VRS_ONE || this.getType() == Album.Type.PTV){
//				this.listInfo01 = LetvApplication.getInstance().getString(R.string.channellist_albumdetailTime,this.getTimeLengthString());
//				this.listInfo02 = "";
//			}else{
//				this.listInfo01 = LetvApplication.getInstance().getString(R.string.channellist_programtype, this.getSubcate());
//				this.listInfo02 = LetvApplication.getInstance().getString(R.string.channellist_albumtype, this.getAlbumtype());
//			}
//		    break;
//		case  Album.Channel.TYPE_MUSIC://音乐
//			this.listInfo01 = LetvApplication.getInstance().getString(R.string.channellist_singer, this.getActor());
//			this.listInfo02 = LetvApplication.getInstance().getString(R.string.channellist_style, this.getSubcate());
//		    break;
//		case  Album.Channel.TYPE_OPEN_CLASS://公开课
//			this.listInfo01 = LetvApplication.getInstance().getString(R.string.channellist_school, this.getRcompany());
//			this.listInfo02 = LetvApplication.getInstance().getString(R.string.channellist_className, this.getSubcate());
//		    break;
//		case Album.Channel.TYPE_JOY:
//			if(this.getType() == Album.Type.VRS_MANG){
//				this.listInfo01 = LetvApplication.getInstance().getString(R.string.channellist_area, this.getArea());
//				this.listInfo02 = LetvApplication.getInstance().getString(R.string.channellist_style, this.getAlbumtype());
//			}else{
//				this.listInfo01 = LetvApplication.getInstance().getString(R.string.channellist_albumdetailTime,this.getTimeLengthString());
//				this.listInfo02 = "";
//				if(this.getCtime() != null && this.getCtime().length() > 0){
//					String ct = this.getCtime() ;
//					if(ct != null && ct.length() > 0){
//						String [] ss = ct.split(" ");
//						if(ss != null && ss.length > 0){
//							ct = ss[0];
//						}
//					}
//					this.listInfo02 = LetvApplication.getInstance().getString(R.string.channellist_timeUpdate, ct);
//				}
//			}
//			break;
//		case Album.Channel.TYPE_FASHION:
//		case Album.Channel.TYPE_PE:
//			this.listInfo01 = LetvApplication.getInstance().getString(R.string.channellist_albumdetailTime,this.getTimeLengthString());
//			this.listInfo02 = "";
//			if(this.getCtime() != null && this.getCtime().length() > 0){
//				String ct = this.getCtime() ;
//				if(ct != null && ct.length() > 0){
//					String [] ss = ct.split(" ");
//					if(ss != null && ss.length > 0){
//						ct = ss[0];
//					}
//				}
//				this.listInfo02 = LetvApplication.getInstance().getString(R.string.channellist_timeUpdate, ct);
//			}
//			break;
//		}
	}
}
