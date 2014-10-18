package com.letv.watchball.parser;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.letv.watchball.R;
import com.letv.watchball.bean.Album;
import com.letv.watchball.bean.AlbumList;
import com.letv.watchball.bean.Episode;
import com.letv.watchball.utils.TextUtil;

/**
 * 专辑对象解析器
 * */
public class AlbumParse extends LetvMobileParser<Album>{
	
	public interface FROM{
		/**
		 * 首页焦点图
		 * */
		int HOME_FUCUS_PIC = 0x101;
		/**
		 * 首页区块
		 * */
		int HOME_BLOCK = 0x102;
		/**
		 * 普通专辑，视频列表
		 * */
		int COMMON_CHANNEL_LIST = 0x103;
		/**
		 * vip专辑，视频列表
		 * */
		int VIP_CHANNEL_LIST = 0x104;
		/**
		 * 普通详情
		 * */
		int COMMON_DETAILS = 0x105;
		/**
		 * vip详情
		 * */
//		int VIP_DETAILS = 0x106;
		/**
		 * 热门排行
		 * */
		int TOP_LIST = 0x107;
		/**
		 * 详情关联
		 * */
		int DETAILS_CORRELATION = 0x108;
		/**
		 * 专题列表
		 * */
		int SPECIAL_LIST = 0x109;
	}
	
	/**
	 * 专辑ID字段或视频ID
	 * */
	private final String ID = "id" ;
	
	/**
	 * 专辑ID字段或视频ID
	 * */
	private final String AID = "aid" ;
	
	/**
	 * 专辑名字或视频名字
	 * */
	private final String TITLE = "title" ;
	/**
	 * 视频别名、看点字段
	 * */
	private final String SUBTITLE = "subtitle" ;
	/**
	 * 图片地址字段
	 * */
	private final String ICON = "icon" ;
	/**
	 * 焦点图:尺寸-800*407，android phone、iphone使用；与at结合使用：当at是1、2使用字段
	 * */
	private final String ICON_2 = "icon_2" ;
	/**
	 * 焦点图:尺寸-800*407,android phone、iphone使用；与at结合使用：当at是3、4、5、6、7使用字段
	 * */
	private final String URL_PIC_2 = "url_pic_2" ;
	/**
	 * 字段
	 * */
	private final String REC_ICON_1 = "rec_icon_1" ;
	/**
	 * 评分字段
	 * */
	private final String SCORE = "score" ;
	/**
	 * 分类id字段
	 * */
	private final String CID = "cid" ;
	/**
	 * 影片来源标示：1-vrs专辑,2-ptv视频,3-vrs视频字段
	 * */
	private final String TYPE = "type" ;
	/**
	 * 点击展示方式：1-进详情，2-直接播放，3-跳出页面,4-跳到客户端内专题,5-跳到客户端WebView,6-直播,7-跳到客户端精品推荐页,8-电视台直播；非以上方式，默认为直接播放字段
	 * */
	private final String AT = "at" ;
	/**
	 * 年份：(支持格式：2011 | 2011-06 | 2011-06-13)字段
	 * */
	private final String YEAR = "year" ;
	/**
	 * 专辑的集数信息,和isend结合使用，完结时为总集数、未完结为当前集数字段
	 * */
	private final String COUNT = "count" ;
	/**
	 * isend 是否完结 1为完结 ， 0为未完结
	 * */
	private final String ISEND = "isend" ;
	/**
	 * 与at结合使用：at是3-为跳出页面的地址、at是4-跳到客户端内专题的id、at是5-为跳到WebView地址、at是6、8-为高清直播地址字段
	 * */
	private final String URL = "url" ;
	/**
	 * 与at结合使用：at是6、8-为350码流直播地址字段
	 * */
	private final String URL_350 = "url_350" ;
	/**
	 * 焦点图简介，与at结合使用：当at是3、4、5、6、7使用字段
	 * */
	private final String URL_INTRO = "url_intro" ;
	/**
	 * 电视台代码:与at结合使用：当at是8使用字段
	 * */
	private final String URL_TV_CODE = "url_tv_code" ;
	/**
	 * 影片时长：单位（秒）字段
	 * */
	private final String TIME_LENGTH = "time_length" ;
	/**
	 * 导演,讲师（不同频道有不同定义）字段
	 * */
	private final String DIRECTOR = "director" ;
	/**
	 * 演员,主持人（不同频道有不同定义）字段
	 * */
	private final String ACTOR = "actor" ;
	/**
	 * 影片简介字段
	 * */
	private final String INTRO = "intro" ;
	/**
	 * 地区字段
	 * */
	private final String AREA = "area" ;
	/**
	 * 影片类型，节目类型（不同频道有不同定义）字段
	 * */
	private final String SUBCATE = "subcate" ;
	/**
	 * 详情页面类型：1-剧集形式,2-列表形式,3-单片形式；非以上类型，默认为进列表形式字段
	 * */
	private final String STYLE = "style" ;
	/**
	 * 电视台字段
	 * */
	private final String TV = "tv" ;
	/**
	 * 学校字段
	 * */
	private final String RCOMPANY = "rcompany" ;
	/**
	 * 视频创建时间字段
	 * */
	private final String CTIME = "ctime" ;
	/**
	 * 专辑类型字段
	 * */
	private final String ALBUMTYPE = "albumtype" ;
	/**
	 * 专辑属性：23-电影；24-电视剧字段
	 * */
	private final String ALBUMSTYLE = "albumstyle" ;
	/**
	 * 是否有海外播放、下载版权：0-有海外播放和下载版权 1-无海外播放版权 2-无海外下载版权 3-无海外播放和下载版权字段
	 * */
	private final String ALLOWFOREIGN = "allowforeign" ;
	/**
	 * 字段
	 * */
	private final String URL_TITLE = "url_title" ;
	/**
	 * 焦点图标题，与at结合使用：当at是3、4、5、6、7使用字段
	 * */
	private final String ALBUMTYPE_STAMP = "albumtype_stamp" ;
	/**
	 * 单片价格字段
	 * */
	private final String SINGLEPRICE = "singleprice" ;
	/**
	 * 是否支持包月: 0-仅单点、1-单点且支持包月、2-仅支持包月字段
	 * */
	private final String ALLOWMONTH = "allowmonth" ;
	/**
	 * 服务期限:(单位-天)字段
	 * */
	private final String PAYDATE = "paydate" ;
	/**
	 * 字段 热门排行的 序号
	 * */
	private final String ORDER = "order" ;
	/**
	 * 盖章类型：new-最新，hot-最热，exclusive-独播，final-大结局，titbits-花絮，prevue-预告，clear-高清，end-完结字段
	 * */
	private final String STAMP = "stamp" ;
	/**
	 * 直播图片蒙版文字字段
	 * */
	private final String TAGS = "tags" ;
	/**
	 * 播放次数字段
	 * */
	private final String PLAYCOUNT = "playcount" ;
	/**
	 * 语言，语种字段
	 * */
	private final String LANGUAGE = "language" ;
	/**
	 * 歌手类型字段
	 * */
	private final String STARRINGTYPE = "starringtype" ;
	/**
	 * 专辑属性名称、体育频道的项目类型字段
	 * */
	private final String ALBUMSTYLENAME = "albumstyleName" ;
	/**
	 * 是否需要外跳浏览器:1-否，2-是 字段
	 * */
	private final String NEEDJUMP = "needJump" ;
	/**
	 * 是否需要支付:1-否，2-是 字段
	 * */
	private final String PAY = "pay" ;
	/**
	 * 视频列表字段
	 * */
	private final String VL = "vl" ;
	/**
	 * 你可能喜欢的影片字段
	 * */
	private final String L_LIST = "l_list" ;
	/**
	 * 导演相关的影片字段
	 * */
	private final String D_LIST = "d_list" ;
	/**
	 * 演员相关的影片字段
	 * */
	private final String A_LIST = "a_list" ;
	
	public AlbumParse(int from){
		super(from);
	}

	@Override
	public Album parse(JSONObject data) throws JSONException {
		
		int form = getFrom();//区别解析
		
		Album album = new Album();
		JSONArray array ;
		//TODO 专辑对象不同页面的区别解析
		switch (form) {
		case FROM.HOME_FUCUS_PIC:
			album.setId(getInt(data,ID));
			album.setTitle(getString(data,TITLE));
			album.setSubTitle(getString(data,SUBTITLE));
			album.setScore(getFloat(data,SCORE));
			album.setCid(getInt(data, CID));
			album.setType(getInt(data,TYPE));
			album.setAt(getInt(data, AT));
			album.setYear(getString(data,YEAR));
			album.setCount(getInt(data,COUNT));
			album.setTimeLength(getLong(data, TIME_LENGTH));
			album.setDirector(getString(data, DIRECTOR));
			album.setActor(getString(data,ACTOR));
			album.setIntro(getString(data,INTRO));
			album.setArea(getString(data,AREA));
			album.setSubcate(getString(data,SUBCATE));
			album.setStyle(getString(data,STYLE));
			album.setTv(getString(data,TV));
			album.setRcompany(getString(data,RCOMPANY));
			album.setCtime(getString(data,CTIME));
			album.setUrlIntro(getString(data,URL_INTRO));
			album.setUrl(getString(data,URL));
			album.setUrl_350(getString(data,URL_350));
			album.setUrlTitle(getString(data, URL_TITLE));
			album.setUrl_pic_2(getString(data, URL_PIC_2));
			album.setIcon_2(getString(data, ICON_2));
			album.setUrlTvCode(getString(data,URL_TV_CODE));
			if(has(data, PAY)){
				album.setPay(getInt(data,PAY));
				if(album.getPay() == 2){
					album.setSingleprice(getInt(data,SINGLEPRICE));
					album.setAllowmonth(getInt(data,ALLOWMONTH));
					album.setPaydate(getInt(data,PAYDATE) + TextUtil.text(R.string.public_day));
				}
			}else{
				album.setPay(1);//默认不付费
			}
			
			if(has(data, NEEDJUMP)){
				album.setNeedJump(getInt(data, NEEDJUMP));
			}else{
				album.setNeedJump(1);//默认不外跳
			}
			
			if(has(data , AID)){
				int aid = getInt(data, AID) ;
				if(aid <= 0){
					album.setAid(album.getId());
				}else{
					album.setAid(aid);
				}
			}else{
				album.setAid(album.getId());
			}
			break;
		case FROM.HOME_BLOCK:
			
			album.setId(getInt(data,ID));
			album.setTitle(getString(data,TITLE));
			album.setSubTitle(getString(data,SUBTITLE));
			album.setScore(getFloat(data,SCORE));
			album.setCid(getInt(data, CID));
			album.setType(getInt(data,TYPE));
			album.setAt(getInt(data, AT));
			album.setYear(getString(data,YEAR));
			album.setCount(getInt(data,COUNT));
			album.setEnd(getInt(data, ISEND) == 1 ? true : false);
			album.setTimeLength(getLong(data, TIME_LENGTH));
			album.setDirector(getString(data, DIRECTOR));
			album.setActor(getString(data,ACTOR));
			album.setIntro(getString(data,INTRO));
			album.setArea(getString(data,AREA));
			album.setSubcate(getString(data,SUBCATE));
			album.setStyle(getString(data,STYLE));
			album.setTv(getString(data,TV));
			album.setRcompany(getString(data,RCOMPANY));
			album.setCtime(getString(data,CTIME));
			if(has(data, PAY)){
				album.setPay(getInt(data,PAY));
				if(album.getPay() == 2){
					album.setSingleprice(getInt(data,SINGLEPRICE));
					album.setAllowmonth(getInt(data,ALLOWMONTH));
					album.setPaydate(getInt(data,PAYDATE) + TextUtil.text(R.string.public_day));
				}
			}else{
				album.setPay(1);//默认不付费
			}
			
			if(has(data, NEEDJUMP)){
				album.setNeedJump(getInt(data, NEEDJUMP));
			}else{
				album.setNeedJump(1);//默认不外跳
			}
			
			String stamp = getString(data, STAMP);
			if("new".equals(stamp)){
				album.setStamp(Album.Stamp.NEW);
			}else if("hot".equals(stamp)){
				album.setStamp(Album.Stamp.HOT);
			}else if("exclusive".equals(stamp)){
				album.setStamp(Album.Stamp.EXCLUSIVE);
			}else if("final".equals(stamp)){
				album.setStamp(Album.Stamp.FINAL);
			}else if("titbits".equals(stamp)){
				album.setStamp(Album.Stamp.TITBITS);
			}else if("prevue".equals(stamp)){
				album.setStamp(Album.Stamp.PREVUE);
			}else if("clear".equals(stamp)){
				album.setStamp(Album.Stamp.CLEAR);
			}else if("end".equals(stamp)){
				album.setStamp(Album.Stamp.END);
			}else if("classic".equals(stamp)){
				album.setStamp(Album.Stamp.CLASSIC);
			}
			
			album.setTags(getString(data, TAGS));
			album.setRec_icon_1(getString(data, REC_ICON_1));
			
			if(has(data , AID)){
				int aid = getInt(data, AID) ;
				if(aid <= 0){
					album.setAid(album.getId());
				}else{
					album.setAid(aid);
				}
			}else{
				album.setAid(album.getId());
			}
			break;
		case FROM.COMMON_CHANNEL_LIST:
			
			album.setId(getInt(data,ID));
			album.setTitle(getString(data,TITLE));
			album.setSubTitle(getString(data,SUBTITLE));
			album.setIcon(getString(data, ICON));
			album.setScore(getFloat(data,SCORE));
			album.setCid(getInt(data, CID));
			album.setType(getInt(data,TYPE));
			album.setAt(getInt(data, AT));
			album.setYear(getString(data,YEAR));
			album.setCount(getInt(data,COUNT));
			album.setTimeLength(getLong(data, TIME_LENGTH));
			album.setDirector(getString(data, DIRECTOR));
			album.setActor(getString(data,ACTOR));
			album.setIntro(getString(data,INTRO));
			album.setArea(getString(data,AREA));
			album.setSubcate(getString(data,SUBCATE));
			album.setStyle(getString(data,STYLE));
			album.setTv(getString(data,TV));
			album.setRcompany(getString(data,RCOMPANY));
			album.setCtime(getString(data,CTIME));
			album.setAlbumtype(getString(data,ALBUMTYPE));
			album.setAlbumtype_stamp(getInt(data,ALBUMTYPE_STAMP));
			if(has(data, PAY)){
				album.setPay(getInt(data,PAY));
				if(album.getPay() == 2){
					album.setSingleprice(getInt(data,SINGLEPRICE));
					album.setAllowmonth(getInt(data,ALLOWMONTH));
					album.setPaydate(getInt(data,PAYDATE) + TextUtil.text(R.string.public_day));
				}
			}else{
				album.setPay(1);//默认不付费
			}
			
			if(has(data, NEEDJUMP)){
				album.setNeedJump(getInt(data, NEEDJUMP));
			}else{
				album.setNeedJump(1);//默认不外跳
			}
			
			if(has(data , AID)){
				int aid = getInt(data, AID) ;
				if(aid <= 0){
					album.setAid(album.getId());
				}else{
					album.setAid(aid);
				}
			}else{
				album.setAid(album.getId());
			}
			
			album.createInfoString();
			break;
		case FROM.VIP_CHANNEL_LIST:
			
			album.setId(getInt(data,ID));
			album.setTitle(getString(data,TITLE));
			album.setSubTitle(getString(data,SUBTITLE));
			album.setIcon(getString(data, ICON));
			album.setScore(getFloat(data,SCORE));
			album.setCid(getInt(data, CID));
			album.setType(getInt(data,TYPE));
			album.setAt(getInt(data, AT));
			album.setYear(getString(data,YEAR));
			album.setCount(getInt(data,COUNT));
			album.setTimeLength(getLong(data, TIME_LENGTH));
			album.setDirector(getString(data, DIRECTOR));
			album.setActor(getString(data,ACTOR));
			album.setIntro(getString(data,INTRO));
			album.setArea(getString(data,AREA));
			album.setSubcate(getString(data,SUBCATE));
			album.setStyle(getString(data,STYLE));
			album.setTv(getString(data,TV));
			album.setRcompany(getString(data,RCOMPANY));
			album.setCtime(getString(data,CTIME));
			album.setAlbumtype(getString(data,ALBUMTYPE));
			album.setAlbumtype_stamp(getInt(data,ALBUMTYPE_STAMP));
			if(has(data, PAY)){
				album.setPay(getInt(data,PAY));
				if(album.getPay() == 2){
					album.setSingleprice(getInt(data,SINGLEPRICE));
					album.setAllowmonth(getInt(data,ALLOWMONTH));
					album.setPaydate(getInt(data,PAYDATE) + TextUtil.text(R.string.public_day));
				}
			}else{
				album.setPay(1);//默认不付费
			}
			
			if(has(data, NEEDJUMP)){
				album.setNeedJump(getInt(data, NEEDJUMP));
			}else{
				album.setNeedJump(1);//默认不外跳
			}
			
			if(has(data , AID)){
				int aid = getInt(data, AID) ;
				if(aid <= 0){
					album.setAid(album.getId());
				}else{
					album.setAid(aid);
				}
			}else{
				album.setAid(album.getId());
			}
			
			album.createInfoString();
			break;
			
		case FROM.COMMON_DETAILS:
			
			album.setId(getInt(data,ID));
			album.setTitle(getString(data,TITLE));
			album.setSubTitle(getString(data,SUBTITLE));
			album.setIcon(getString(data, ICON));
			album.setScore(getFloat(data,SCORE));
			album.setCid(getInt(data, CID));
			album.setType(getInt(data,TYPE));
			album.setAt(getInt(data, AT));
			album.setYear(getString(data,YEAR));
			album.setCount(getInt(data,COUNT));
			album.setEnd(getInt(data, ISEND) == 1 ? true : false);
			album.setTimeLength(getLong(data, TIME_LENGTH));
			album.setDirector(getString(data, DIRECTOR));
			album.setActor(getString(data,ACTOR));
			album.setIntro(getString(data,INTRO));
			album.setArea(getString(data,AREA));
			album.setSubcate(getString(data,SUBCATE));
			album.setStyle(getString(data,STYLE));
			album.setTv(getString(data,TV));
			album.setRcompany(getString(data,RCOMPANY));
			album.setCtime(getString(data,CTIME));
			album.setLanguage(getString(data, LANGUAGE));
			album.setStarringtype(getString(data, STARRINGTYPE));
			if(has(data, PAY)){
				album.setPay(getInt(data,PAY));
				if(album.getPay() == 2){
					album.setSingleprice(getInt(data,SINGLEPRICE));
					album.setAllowmonth(getInt(data,ALLOWMONTH));
					album.setPaydate(getInt(data,PAYDATE) + TextUtil.text(R.string.public_day));
				}
			}else{
				album.setPay(1);//默认不付费
			}
			
			if(has(data, NEEDJUMP)){
				album.setNeedJump(getInt(data, NEEDJUMP));
			}else{
				album.setNeedJump(1);//默认不外跳
			}
			
			array = getJSONArray(data,VL);
			if(array != null && array.length() > 0){
				album.setEpsiodes(new ArrayList<Episode>());
				for(int i=0;i<array.length();i++){
					
					JSONObject episode_JsonObject = getJSONObject(array,i);
					
					Episode episode = new EpisodeParse().parse(episode_JsonObject);
					
					if(album.getPay() == 2 && episode.getPay() != 2){
						continue;
					}
					
					album.getEpsiodes().add(episode);
				}
			}
			
			array = getJSONArray(data,L_LIST);
			if(array != null && array.length() > 0){
				album.setCorrelationLoves(new AlbumList());
				for(int i=0;i<array.length();i++){
					
					JSONObject album_JsonObject = getJSONObject(array, i);
					
					Album loveAlbum = new AlbumParse(AlbumParse.FROM.DETAILS_CORRELATION).parse(album_JsonObject);
					
					album.getCorrelationLoves().add(loveAlbum);
				}
			}
			
			array = getJSONArray(data,D_LIST);
			if(array != null && array.length() > 0){
				album.setCorrelationDirectors(new AlbumList());
				for(int i=0;i<array.length();i++){
					
					JSONObject album_JsonObject = getJSONObject(array, i);
					
					Album directorAlbum = new AlbumParse(AlbumParse.FROM.DETAILS_CORRELATION).parse(album_JsonObject);
					
					album.getCorrelationDirectors().add(directorAlbum);
				}
			}
			
			array = getJSONArray(data,A_LIST);
			if(array != null && array.length() > 0){
				album.setCorrelationActors(new AlbumList());
				for(int i=0;i<array.length();i++){
					
					JSONObject actor_JsonObject = getJSONObject(array, i);
					
					Album actorAlbum = new AlbumParse(AlbumParse.FROM.DETAILS_CORRELATION).parse(actor_JsonObject);
					
					album.getCorrelationActors().add(actorAlbum);
				}
			}

			if(has(data , AID)){
				int aid = getInt(data, AID) ;
				if(aid <= 0){
					album.setAid(album.getId());
				}else{
					album.setAid(aid);
				}
			}else{
				album.setAid(album.getId());
			}
			
			album.setJsonString(data.toString());
			break;
			
//		case FROM.VIP_DETAILS:
//			
//			album.setId(getInt(data,ID));
//			album.setTitle(getString(data,TITLE));
//			album.setSubTitle(getString(data,SUBTITLE));
//			album.setIcon(getString(data, ICON));
//			album.setScore(getFloat(data,SCORE));
//			album.setCid(getInt(data, CID));
//			album.setType(getInt(data,TYPE));
//			album.setAt(getInt(data, AT));
//			album.setYear(getString(data,YEAR));
//			album.setCount(getInt(data,COUNT));
//			album.setEnd(getInt(data, ISEND) == 1 ? true : false);
//			album.setTimeLength(getLong(data, TIME_LENGTH));
//			album.setDirector(getString(data, DIRECTOR));
//			album.setActor(getString(data,ACTOR));
//			album.setIntro(getString(data,INTRO));
//			album.setArea(getString(data,AREA));
//			album.setSubcate(getString(data,SUBCATE));
//			album.setStyle(getString(data,STYLE));
//			album.setTv(getString(data,TV));
//			album.setRcompany(getString(data,RCOMPANY));
//			album.setCtime(getString(data,CTIME));
//			album.setAllowforeign(getString(data, ALLOWFOREIGN));
//			album.setLanguage(getString(data, LANGUAGE));
//			album.setStarringtype(getString(data, STARRINGTYPE));
//			album.setAlbumtype(getString(data,ALBUMTYPE));
//			album.setAlbumstyle(getInt(data, ALBUMSTYLE));
//			album.setAlbumstyleName(getString(data, ALBUMSTYLENAME));
//			album.setSingleprice(getInt(data,SINGLEPRICE));
//			album.setAllowmonth(getInt(data,ALLOWMONTH));
//			album.setPaydate(getInt(data,PAYDATE) + TextUtil.text(R.string.public_day));
//			if(has(data, PAY)){
//				album.setPay(getInt(data,PAY));
//			}else{
//				album.setPay(1);//默认不付费
//			}
//			
//			if(has(data, NEEDJUMP)){
//				album.setNeedJump(getInt(data, NEEDJUMP));
//			}else{
//				album.setNeedJump(1);//默认不外跳
//			}
//			
//			array = getJSONArray(data,VL);
//			if(array != null){
//				for(int i=0;i<array.length();i++){
//					
//					JSONObject episode_JsonObject = getJSONObject(array,i);
//					
//					Episode episode = new EpisodeParse().parse(episode_JsonObject);
//					
//					album.getEpsiodes().add(episode);
//				}
//			}
//			
//			array = getJSONArray(data,L_LIST);
//			if(array != null){
//				for(int i=0;i<array.length();i++){
//					
//					JSONObject album_JsonObject = getJSONObject(array, i);
//					
//					Album loveAlbum = new AlbumParse(AlbumParse.FROM.DETAILS_CORRELATION).parse(album_JsonObject);
//					
//					album.getCorrelationLoves().add(loveAlbum);
//				}
//			}
//			
//			array = getJSONArray(data,D_LIST);
//			if(array != null){
//				for(int i=0;i<array.length();i++){
//					
//					JSONObject album_JsonObject = getJSONObject(array, i);
//					
//					Album directorAlbum = new AlbumParse(AlbumParse.FROM.DETAILS_CORRELATION).parse(album_JsonObject);
//					
//					album.getCorrelationDirectors().add(directorAlbum);
//				}
//			}
//			
//			array = getJSONArray(data,A_LIST);
//			if(array != null){
//				for(int i=0;i<array.length();i++){
//					
//					JSONObject actor_JsonObject = getJSONObject(array, i);
//					
//					Album actorAlbum = new AlbumParse(AlbumParse.FROM.DETAILS_CORRELATION).parse(actor_JsonObject);
//					
//					album.getCorrelationActors().add(actorAlbum);
//				}
//			}
//			
//			album.setJsonString(data.toString());
//			break;
			
		case FROM.TOP_LIST:
			
			album.setId(getInt(data,ID));
			album.setTitle(getString(data,TITLE));
			album.setSubTitle(getString(data,SUBTITLE));
			album.setIcon(getString(data, ICON));
			album.setScore(getFloat(data,SCORE));
			album.setCid(getInt(data, CID));
			album.setType(getInt(data,TYPE));
			album.setAt(getInt(data, AT));
			album.setYear(getString(data,YEAR));
			album.setCount(getInt(data,COUNT));
			album.setTimeLength(getLong(data, TIME_LENGTH));
			album.setDirector(getString(data, DIRECTOR));
			album.setActor(getString(data,ACTOR));
			album.setIntro(getString(data,INTRO));
			album.setArea(getString(data,AREA));
			album.setSubcate(getString(data,SUBCATE));
			album.setStyle(getString(data,STYLE));
			album.setTv(getString(data,TV));
			album.setRcompany(getString(data,RCOMPANY));
			album.setCtime(getString(data,CTIME));
			album.setOrder(getInt(data, ORDER));
			album.setTags(getString(data, TAGS));
			album.setPlaycount(getString(data, PLAYCOUNT));
			if(has(data, PAY)){
				album.setPay(getInt(data,PAY));
				if(album.getPay() == 2){
					album.setSingleprice(getInt(data,SINGLEPRICE));
					album.setAllowmonth(getInt(data,ALLOWMONTH));
					album.setPaydate(getInt(data,PAYDATE) + TextUtil.text(R.string.public_day));
				}
			}else{
				album.setPay(1);//默认不付费
			}
			
			if(has(data, NEEDJUMP)){
				album.setNeedJump(getInt(data, NEEDJUMP));
			}else{
				album.setNeedJump(1);//默认不外跳
			}
			
			if(has(data , AID)){
				int aid = getInt(data, AID) ;
				if(aid <= 0){
					album.setAid(album.getId());
				}else{
					album.setAid(aid);
				}
			}else{
				album.setAid(album.getId());
			}
			
			album.createInfoString();
			break;
		case FROM.SPECIAL_LIST:
			album.setId(getInt(data,ID));
			album.setTitle(getString(data,TITLE));
			album.setSubTitle(getString(data,SUBTITLE));
			album.setIcon(getString(data, ICON));
			album.setScore(getFloat(data,SCORE));
			album.setCid(getInt(data, CID));
			album.setType(getInt(data,TYPE));
			album.setAt(getInt(data, AT));
			album.setYear(getString(data,YEAR));
			album.setCount(getInt(data,COUNT));
			album.setTimeLength(getLong(data, TIME_LENGTH));
			album.setDirector(getString(data, DIRECTOR));
			album.setActor(getString(data,ACTOR));
			album.setIntro(getString(data,INTRO));
			album.setArea(getString(data,AREA));
			album.setSubcate(getString(data,SUBCATE));
			album.setStyle(getString(data,STYLE));
			album.setTv(getString(data,TV));
			album.setRcompany(getString(data,RCOMPANY));
			album.setCtime(getString(data,CTIME));
			if(has(data, PAY)){
				album.setPay(getInt(data,PAY));
				if(album.getPay() == 2){
					album.setSingleprice(getInt(data,SINGLEPRICE));
					album.setAllowmonth(getInt(data,ALLOWMONTH));
					album.setPaydate(getInt(data,PAYDATE) + TextUtil.text(R.string.public_day));
				}
			}else{
				album.setPay(1);//默认不付费
			}
			
			if(has(data, NEEDJUMP)){
				album.setNeedJump(getInt(data, NEEDJUMP));
			}else{
				album.setNeedJump(1);//默认不外跳
			}
			
			if(has(data , AID)){
				int aid = getInt(data, AID) ;
				if(aid <= 0){
					album.setAid(album.getId());
				}else{
					album.setAid(aid);
				}
			}else{
				album.setAid(album.getId());
			}
			
			album.createInfoString();
			break;
		case FROM.DETAILS_CORRELATION:
			
			album.setId(getInt(data,ID));
			album.setIcon(getString(data, ICON));
			album.setTitle(getString(data,TITLE));
			album.setSubTitle(getString(data,SUBTITLE));
			album.setScore(getFloat(data,SCORE));
			album.setCid(getInt(data, CID));
			album.setType(getInt(data,TYPE));
			album.setAt(getInt(data, AT));
			album.setYear(getString(data,YEAR));
			album.setCount(getInt(data,COUNT));
			album.setTimeLength(getLong(data, TIME_LENGTH));
			album.setDirector(getString(data, DIRECTOR));
			album.setActor(getString(data,ACTOR));
			album.setIntro(getString(data,INTRO));
			album.setArea(getString(data,AREA));
			album.setSubcate(getString(data,SUBCATE));
			album.setStyle(getString(data,STYLE));
			album.setTv(getString(data,TV));
			album.setRcompany(getString(data,RCOMPANY));
			album.setCtime(getString(data,CTIME));
			album.setAlbumtype(getString(data,ALBUMTYPE));
			album.setAlbumtype_stamp(getInt(data,ALBUMTYPE_STAMP));
			if(has(data, PAY)){
				album.setPay(getInt(data,PAY));
				if(album.getPay() == 2){
					album.setSingleprice(getInt(data,SINGLEPRICE));
					album.setAllowmonth(getInt(data,ALLOWMONTH));
					album.setPaydate(getInt(data,PAYDATE) + TextUtil.text(R.string.public_day));
				}
			}else{
				album.setPay(1);//默认不付费
			}
			
			if(has(data, NEEDJUMP)){
				album.setNeedJump(getInt(data, NEEDJUMP));
			}else{
				album.setNeedJump(1);//默认不外跳
			}

			if(has(data , AID)){
				int aid = getInt(data, AID) ;
				if(aid <= 0){
					album.setAid(album.getId());
				}else{
					album.setAid(aid);
				}
			}else{
				album.setAid(album.getId());
			}
			
			break;
		}
		
		return album;
	}
}
