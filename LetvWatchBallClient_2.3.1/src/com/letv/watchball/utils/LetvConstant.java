package com.letv.watchball.utils;

import android.util.DisplayMetrics;

import com.letv.watchball.LetvApplication;

public class LetvConstant {
	/**
	 * 播放手机平台
	 */
	public static final String MOBILE_SYSTEM_PHONE = "420003";
	/**
	 * 播放手机外跳平台
	 */
	public static final String MOBILE_SYSTEM_PHONE_JUMP = "420004";
	/**
	 * 付费手机付费
	 */
	public static final String MOBILE_SYSTEM_PHONE_PAY = "141003";
	/**
	 * 常量信息,每次请求频时的条数
	 */
	public static final int PAGE_SIZE = 20;

	public static final int THREADPOOL_SIZE = 3;
	/**
	 * 发送短信找回密码 手机号
	 */
	public static final String retrievePwdPhoneNum = "95131292121";// "10690159292121";
	/**
	 * webactivity加载网页的type
	 */
	public final static String retrieve_pwd_byemail_url = "http://sso.letv.com/user/backpwdemail";
	/**
	 * 乐视协议url
	 */
	public final static String USER_PROTOCOL_URL = "http://sso.letv.com/user/protocol";
	/**
	 * about us url
	 */
	public final static String ABLUT_US_URL = "http://m.letv.com/";
	/**
	 * 乐视商城url
	 */
	public static String LETV_SHOP_URL = "http://shop.letv.com/";
	/**
	 * 频道列表每页请求数据
	 */
	public static final int CHANNEL_LIST_PAGE_SIZE = 30;
	/**
	 * 精品推荐列表每页请求数据
	 */
	public static final int RECOMMEND_LIST_PAGE_SIZE = 30;
	/**
	 * 用来记录播放记录播放完成
	 */
	public static final int PLAY_FINISH = -1000;

	public static final int WIDGET_UPDATE_UI_TIME = 15 * 1000;

	public static final boolean USE_SDCARD = true;

	public static final boolean USE_MEMORY = true;

	/**
	 * 艾瑞的AppKey
	 */
	public static final String MAPPTRACKERKEY = "440e9707b1c3669a";

	/**
	 * 没有网络
	 */
	public static final int NET_NO = 0;
	/**
	 * 请求网络数据返回状态
	 */
	public static final String STATUS_OK = "1";

	/**
	 * 网络连接错误
	 */
	public static final int NET_ERROR = 1;
	/**
	 * 数据为空
	 */
	public static final int DATA_NULL = 2;

	/**
	 * 下载页面分为正在下载，完成下载。若为true，则为完成下载页面,默认为完成下载界面
	 */
	// public static boolean DOWNLOAD_FINISH_PAGE = true;

	/**
	 * FLURRY
	 */
	// public static final String FLURRY_API_KEY = "TFLG6X757KI8QBRICMPM";
	// public static final String FLURRY_API_KEY = "C6HTNMTYKPNH5VB2YBJ2";

	/**
	 * 艾德聚合
	 */
	public static final String AD_PublishId = "e084144fd78c42c5878e3277606f8b6e";

	public static final int LETV_LIVEBOOK_CODE = 250250250;
	/**
	 * 每2分钟刷新数据
	 */
	public static final long REFRESH_TIME = 120 * 1000;
	/**
	 * 直播加密密钥
	 */
	// public static final String MIYUE = "a2915e518ba60169f77";
	public static final String MIYUE = "c75653c78894c0be38b59db89f02e6b7";

	/**
	 * 应用程序用到的意图
	 */

	public static class Intent {

		public static class Bundle {
			/**
			 * 进入播放页时，intent中传递参数的bundle键
			 */
			public static final String PLAY = "play_parameter";
		}

	}

	/**
	 * 整个应用程序运行过程中用到的数据
	 */
	public static class Global {

		public static final String DEVICEID = LetvTools.generateDeviceId();

		public static final String PCODE = LetvTools.getPcode();

		public static final String VERSION = LetvTools.getClientVersionName();

		public static final String ASIGN_KEY = "Tl34Ees0S9tsKY213";

		public static final int VERSION_CODE = LetvTools.getClientVersionCode();

		public static final DisplayMetrics displayMetrics = LetvApplication
				.getInstance().getResources().getDisplayMetrics();

		public static final int screenWidth = UIs.getScreenWidth();

		public static final int screenHeight = UIs.getScreenHeight();
	}

	/**
	 * 应用程序数据库中的表名，字段名等信息
	 * 
	 * @author ddf
	 * 
	 */
	public static class DataBase {
		/**
		 * 本地视频预约
		 * 
		 * @author liuheyuan
		 * 
		 */
		public static class SubscribeGameTrace {

			public static final String TABLE_NAME = "SubscribeGameTrace";

			public static class Field {

				/**
				 * 比赛id
				 */
				public static final String id = "id";
				/**
				 * 赛事名称
				 */
				public static final String level = "level";
				/**
				 * 主队
				 */
				public static final String home = "home";
				/**
				 * 客队
				 */
				public static final String guest = "guest";
				/**
				 * 比赛日期
				 */
				public static final String playDate = "playDate";
				/**
				 * 开赛时间
				 */
				public static final String playTime = "playTime";
				/**
				 * 格式化后的时间
				 */
				public static final String playTimeMillisecond = "playTimeMillisecond";
				/**
				 * 比赛状态 0：未开始；1：直播中；2：已结束
				 */
				public static final String status = "status";
				/**
				 * 是否已经发出开赛通知 0：未通知，1：已通知
				 */
				public static final String isNotify = "isNotify";

				/**
				 * 是否已经赛果提醒 0：未提醒，1：已提醒
				 */
				public static final String isPushResult = "isPushResult";

			}

		}

		/**
		 * 搜索记录表
		 * 
		 * @author ddf
		 * 
		 */
		public static class SearchTrace {

			/**
			 * 表名
			 */
			public static final String TABLE_NAME = "searchtable";

			public static class Field {

				/**
				 * 搜索关键字
				 */
				public static final String NAME = "name";
				/**
				 * 搜索时间戳
				 */
				public static final String TIMESTAMP = "timestamp";

			}
		}

		/**
		 * 初始化时节日祝福表
		 * 
		 * @author ddf
		 * 
		 */
		public static class FestivalImageTrace {

			/**
			 * 表名
			 */
			public static final String TABLE_NAME = "festivalimagetrace";

			public static class Field {

				/**
				 * string ,name
				 */
				public static final String NAME = "name";

				/**
				 * string ,name
				 */
				public static final String PIC = "pic";
				/**
				 * long,start time
				 */
				public static final String START_TIME = "starttime";
				/**
				 * long ,end time
				 */
				public static final String END_TIME = "endtime";

				/**
				 * boolean , is top
				 */
				public static final String ORDER = "orderk";

			}

		}

		/**
		 * 下载记录表，包括未完成和已完成
		 * 
		 * @author ddf
		 * 
		 */
		public static class DownloadTrace {

			/**
			 * 表名
			 */
			public static final String TABLE_NAME = "downlaodTrace";

			public static class Field {

				public static final String ALBUM_ID = "albumId";

				public static final String CID = "cid";

				public static final String ORDER = "ord";

				public static final String ICON = "icon";

				public static final String TYPE = "type";

				public static final String EPISODE_ID = "episodeid";

				public static final String EPISODE_TITLE = "episodetitle";

				public static final String ALBUM_TITLE = "albumtitle";

				public static final String TOTAL_SIZE = "totalsize";

				public static final String FINISH = "finish";

				public static final String TIMESTAMP = "timestamp";

				public static final String LENGTH = "length";

				/**
				 * 3.7 add 下载路径
				 */
				public static final String FILE_PATH = "file_path";
				/**
				 * 4.1 add 是否是高清
				 * */
				public static final String ISHD = "isHd";
				/**
				 * 5.0添加 区分是 之前版本数据 还是 最新数据 旧数据 默认为 0， 新数据为 1
				 */
				public static final String IS_NEW = "isNew";

				/**
				 * 片头时间
				 */
				public static final String B_TIME = "btime";
				/**
				 * 片尾时间
				 */
				public static final String E_TIME = "etime";
			}
		}

		/**
		 * 播放记录表
		 * 
		 * @author ddf
		 */

		public static class PlayRecord {
			/**
			 * 表名
			 */
			public static final String TABLE_NAME = "playtable";

			public static class Field {
				/**
				 * 频道ID
				 * */
				public static final String CID = "cid";
				/**
				 * 专辑ID
				 * */
				public static final String PID = "pid";
				/**
				 * 视频ID
				 * */
				public static final String VID = "vid";
				/**
				 * 下一集视频ID
				 * */
				public static final String NVID = "nvid";
				/**
				 * 用户ID
				 * */
				public static final String UID = "uid";
				/**
				 * 视频类型
				 * */
				public static final String VTYPE = "vtype";
				/**
				 * 来源
				 * */
				public static final String FROM = "playtracefrom";
				/**
				 * 视频总时长
				 * */
				public static final String VTIME = "vtime";
				/**
				 * 播放时间点
				 * */
				public static final String HTIME = "htime";
				/**
				 * 最后更新时间
				 * */
				public static final String UTIME = "utime";
				/**
				 * 视频标题
				 * */
				public static final String TITLE = "title";
				/**
				 * 视频封面图
				 * */
				public static final String IMG = "img";
				/**
				 * 当前集数
				 */
				public static final String NC = "nc";
				/**
				 * 该条信息状态 0 默认状态，无修改，1更新待上传，2删除待上传
				 * */
				public static final String STATE = "state";
				/**
				 * 视频类型
				 * */
				public static final String TYPE = "type";
				/**
				 * 视频封面图300
				 * */
				public static final String IMG300 = "img300";
			}
		}

		/**
		 * 收藏记录表
		 * 
		 * @author ddf
		 * 
		 */
		public static class FavoriteRecord {

			/**
			 * 表名
			 */
			public static final String TABLE_NAME = "Favoritetable";

			public static class Field {

				/**
				 * 搜索时间戳
				 */
				public static final String TIMESTAMP = "timestamp";

				public static final String ID = "id";

				public static final String AID = "aid";

				public static final String TITLE = "title";

				public static final String SUBTITLE = "subTitle";

				public static final String ICON = "icon";

				public static final String SCORE = "score";

				public static final String CID = "cid";

				public static final String YEAR = "year";

				public static final String COUNT = "count";

				public static final String TIMELENGTH = "timeLength";

				public static final String DIRECTOR = "director";

				public static final String ACTOR = "actor";

				public static final String AREA = "area";

				public static final String SUBCATE = "subcate";

				public static final String RCOMPANY = "rcompany";

				public static final String CTIME = "ctime";

				public static final String TYPE = "type";

				public static final String ALBUMTYPE = "albumtype";

				public static final String AT = "at";

				public static final String SINGLEPRICE = "singleprice";

				public static final String ALLOWMONTH = "allowmonth";

				public static final String PAYDATE = "paydate";

				public static final String NEEDJUMP = "needJump";

				public static final String PAY = "pay";

				public static final String ISDOLBY = "isDolby";
				public static final String ISEND = "isEnd";
				public static final String EPISODE = "episode";
			}
		}

		/**
		 * 本地视频
		 * 
		 * @author liuheyuan
		 * 
		 */
		public static class LocalVideoTrace {

			public static final String TABLE_NAME = "LocalVideoTable";

			public static class Field {

				public static final String PATH = "c_path";

				public static final String TITLE = "c_title";

				public static final String POSITION = "c_position";

				public static final String FILESIZE = "c_filesize";

				public static final String TIMELENGTH = "c_timelength";

				public static final String CREATETIME = "c_createtime";

				public static final String VIDEO_W_H = "c_video_w_h";

				public static final String VIDEO_TYPE = "c_video_type";

			}
		}

		/**
		 * 本地视频
		 * 
		 * @author liuheyuan
		 * 
		 */
		public static class LiveBookTrace {

			public static final String TABLE_NAME = "LiveBookTrace";

			public static class Field {

				/**
				 * 直播节目名称
				 */
				public static final String MD5_ID = "md5";

				/**
				 * 直播节目名称
				 */
				public static final String PROGRAMNAME = "programName";

				/**
				 * 直播频道名称
				 */
				public static final String CHANNELNAME = "channelName";

				/**
				 * 直播频道编码
				 */
				public static final String CODE = "code";

				/**
				 * 直播节目开播时间
				 */
				public static final String PLAY_TIME = "play_time";

				/**
				 * 直播节目开播时间
				 */
				public static final String PLAY_TIME_LONG = "play_time_long";

				/**
				 * 是否推送过
				 */
				public static final String IS_NOTIFY = "is_notify";

				public static final String LAUNCH_MODE = "launch_mode";

			}
		}

		/**
		 * 客户端提示语服务端化
		 * 
		 * @author haitian
		 * 
		 */
		public static class DialogMsgTrace {

			public static final String TABLE_NAME = "DialogMsgTrace";

			public static class Field {
				public static final String MSGID = "msgId";
				public static final String MAGTITLE = "msgTitle";
				public static final String MESSAGE = "message";

			}
		}

		/**
		 * 客户端提示语服务端化
		 * 
		 * @author haitian
		 * 
		 */
		public static class LocalCacheTrace {

			public static final String TABLE_NAME = "LocalCacherace";

			public static class Field {
				public static final String CACHEID = "cacheId";
				public static final String ASSISTKEY = "assistkey";
				public static final String MARKID = "markid";
				public static final String CACHEDATA = "cachedata";
				public static final String CACHETIME = "cachetime";

			}
		}

		/**
		 * 顶级频道记录
		 */
		public static class TopChannelsTrace {

			public static final String TABLE_NAME = "TopChannelsTrace";

			public static class Field {
				public static final String CHANNELID = "channelId";
				public static final String CHANNELNAME = "channelName";
				public static final String CHANNELTYPE = "channelType";
				public static final String ORDER = "orderk";
			}
		}

		/**
		 * 顶级频道记录
		 */
		public static class PushAdImageTrace {

			public static final String TABLE_NAME = "PushAdImageTrace";

			public static class Field {
				public static final String ID = "adId";
				public static final String IMAGEURL = "imageUrl";
				public static final String CREATETIME = "createTime";
				public static final String MTIME = "Time";
			}
		}
	}

	public interface Menu {

		public static final int PLAY = 0;

		public static final int DELETE = 1;

		public static final int CLEAR = 2;

		public static final int START_ALL = 3;

		public static final int PAUSE_ALL = 4;

		public static final int ATTRIBUTE = 5;

	}

	public interface TextColor {

		public static final int ORDER_TEXT_SELECED_COLOR = 0xfffaf0ed;

		public static final int ORDER_TEXT_NORMAL_COLOR = 0xffb1b1b1;

		public static final int SEARCH_SORT_SELECTED = 0xff3f3f3f;

		public static final int SEARCH_SORT_NORMAL = 0x00000000;
	}

	public class DialogMsgConstantId {
		public static final String TWO_ZERO_ONE_CONSTANT = "201";
		public static final String TWO_ZERO_TWO_CONSTANT = "202";
		public static final String TWO_ZERO_THREE_CONSTANT = "203";
		public static final String TWO_ZERO_FIVE_CONSTANT = "205";
		public static final String TWO_ZERO_SIX_CONSTANT = "206";
		public static final String TWO_ZERO_SEVEN_CONSTANT = "207";

		public static final String FOUR_ZERO_ONE_CONSTANT = "401";
		public static final String FIVE_ZERO_ONE_CONSTANT = "501";
		public static final String SIX_ZERO_TWO_CONSTANT = "602";

		public static final String SEVEN_ZERO_TWO_CONSTANT = "702";
		public static final String SEVEN_ZERO_FOUR_CONSTANT = "704";
		public static final String SEVEN_ZERO_FIVE_CONSTANT = "705";
		public static final String SEVEN_ZERO_SEVEN_CONSTANT = "707";

		public static final String NINE_ZERO_TWO_CONSTANT = "902";
		public static final String TEN_ZERO_ONE_CONSTANT = "1001";
		public static final String TEN_ZERO_TWO_CONSTANT = "1002";

		public static final String ELEVEN_ZERO_ONE_CONSTANT = "1101";
		public static final String TWELVE_ZERO_ONE_CONSTANT = "1201";
		public static final String THIRTEEN_ZERO_ONE_CONSTANT = "1301";

		public static final String FOURTEEN_ZERO_ONE_CONSTANT = "1401";
		public static final String FOURTEEN_ZERO_THREE_CONSTANT = "1403";
		public static final String FOURTEEN_ZERO_FOUR_CONSTANT = "1404";
		public static final String FOURTEEN_ZERO_FIVE_CONSTANT = "1405";
		public static final String FOURTEEN_ZERO_SIX_CONSTANT = "1406";
		public static final String FOURTEEN_ZERO_SEVEN_CONSTANT = "1407";
		public static final String FOURTEEN_ZERO_EIGHT_CONSTANT = "1408";
		public static final String FOURTEEN_ZERO_NINE_CONSTANT = "1409";
		public static final String FOURTEEN_TEN_CONSTANT = "1410";

		public static final String FIFTEEN_ZERO_ONE_CONSTANT = "1501";
		public static final String FIFTEEN_ZERO_TWO_CONSTANT = "1502";
		public static final String FIFTEEN_ZERO_FIVE_CONSTANT = "1505";
		public static final String FIFTEEN_ZERO_SIX_CONSTANT = "1506";
		public static final String FIFTEEN_ELEVEN_CONSTANT = "1511";

		public static final String SIXTEEN_ZERO_ONE_CONSTANT = "1601";
		public static final String SIXTEEN_ZERO_TWO_CONSTANT = "1602";

		public static final String TWENTYONE_ZERO_ONE_CONSTANT = "2101";
		public static final String TWENTYONE_ZERO_TWO_CONSTANT = "2102";
		public static final String TWENTYTWO_ZERO_ONE_CONSTANT = "2201";

		public static final String TWENTYTHREE_ZERO_THREE_CONSTANT = "2303";
		public static final String TWENTYFOUR_ZERO_ONE_CONSTANT = "2401";
		public static final String TWENTYFOUR_ZERO_TWO_CONSTANT = "2402";
		public static final String TWENTYFOUR_ZERO_FIVE_CONSTANT = "2405";

		public static final String TWENTYFIVE_ZERO_ONE_CONSTANT = "2501";

		public static final String CONSTANT_2601 = "2601";
		public static final String CONSTANT_2701 = "2701";
		public static final String CONSTANT_2702 = "2702";
		public static final String CONSTANT_2703 = "2703";
		public static final String CONSTANT_2704 = "2704";
		public static final String CONSTANT_2705 = "2705";

		public static final String CONSTANT_50102 = "50102";
		public static final String CONSTANT_50208 = "50208";
		public static final String CONSTANT_50301 = "50301";
		public static final String CONSTANT_50601 = "50601";
		public static final String CONSTANT_50602 = "50602";
		public static final String CONSTANT_50902 = "50902";

	}

	public static class BrName {

		/**
		 * 播放低码流名称
		 * */
		public static String playLowName = "流畅";

		/**
		 * 播放中码流名称
		 * */
		public static String playNormalName = "高清";

		/**
		 * 播放高码流名称
		 * */
		public static String playHighName = "超清";

		/**
		 * 下载低码流名称
		 * */
		public static String downloadLowName = "流畅";

		/**
		 * 下载中码流名称
		 * */
		public static String downloadNormalName = "高清";

		/**
		 * 下载高码流名称
		 * */
		public static String downloadHighName = "超清";
	}

	/**
	 * @author Liuheyuan 视频新闻排序类型
	 */
	public interface VideoNewsOrderBy {
		/**
		 * 最新
		 */
		public static final String DATE = "1";

		/**
		 * 最热
		 */
		public static final String PLAYCOUNT = "9";
	}
}
