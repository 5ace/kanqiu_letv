package com.letv.watchball.http.api;

import java.util.ArrayList;

import android.util.Log;
import com.letv.watchball.bean.AdJoiningBean;
import com.letv.watchball.parser.AdJoiningParser;
import com.letv.watchball.utils.LetvUtil;
import org.apache.http.message.BasicNameValuePair;

import android.os.Bundle;

import com.letv.http.bean.LetvBaseBean;
import com.letv.http.bean.LetvDataHull;
import com.letv.http.impl.LetvHttpBaseParameter;
import com.letv.http.impl.LetvHttpParameter;
import com.letv.http.impl.LetvHttpStaticParameter;
import com.letv.http.impl.LetvHttpTool;
import com.letv.http.parse.LetvBaseParser;
import com.letv.http.parse.LetvMainParser;
import com.letv.watchball.db.PreferencesManager;
import com.letv.watchball.utils.MD5;

/**
 * 所有接口API
 * */
public class LetvHttpApi {

	private static String PCODE;
	private static String VERSION;

	private static String DEVICEID;
	/************************************************* 正式地址 ********************************************************/

	/**
	 * 动态请求BaseUrl
	 * */
	// private static final String DYNAMIC_APP_BASE_URL =
	// "http://dynamic.app.m.letv.com/android/dynamic.php";
	private static final String DYNAMIC_APP_BASE_URL = "http://sports.app.m.letv.com/dynamic.php";
	/**
	 * 静态请求head
	 * */
	// private static final String STATIC_APP_BASE_HEAD =
	// "http://static.app.m.letv.com/android";
	private static final String STATIC_APP_BASE_HEAD = "http://sports.app.m.letv.com";
	/**
	 * 静态请求end
	 * */
	private static final String STATIC_APP_BASE_END = ".mindex.html";

	/************************************************* 测试地址 ********************************************************/

	/**
	 * 动态请求BaseUrl http://test.sports.app.m.letv.com/
	 * */
	public static final String DYNAMIC_TEST_BASE_URL = "http://test.ports.app.m.letv.com/dynamic.php";
	/**
	 * 静态请求head
	 * */
	public static final String STATIC_TEST_BASE_HEAD = "http://test.sports.app.m.letv.com";
	/**
	 * 静态请求end
	 * */
	public static final String STATIC_TEST_BASE_END = ".mindex.html";

	/****************************************************************************************************************/

	/**
	 * 海外IP地址
	 * */
	private static final String IP_BASE_URL = "http://hot.vrs.letv.com/ip";

	/************************************************* 看球 正式地址 ********************************************************/
	// public static final String BASE_URL =
	// "http://220.181.153.236:8080/sports/api/";
	/**
	 * 看球1.0正式接口
	 */
	public static final String WB_BASE_API10_URL = "http://sports.app.m.letv.com/sports/api/";
	/**
	 * 动态请求BaseUrl
	 * */
	public static final String WB_DYNAMIC_BASE_URL = "http://sports.app.m.letv.com/dynamic.php";
	/**
	 * 静态请求head
	 * */
	// private static final String WB_STATIC_BASE_HEAD =
	// "http://static.sports.m.letv.com";
	private static final String WB_STATIC_BASE_HEAD = "http://sports.app.m.letv.com";
	/**
	 * 静态请求end
	 * */
	private static final String WB_STATIC_APP_BASE_END = ".mindex.html";
	/************************************************* 看球 测试地址 ********************************************************/
	/**
	 * 看球1.0正式接口
	 */
	public static final String WB_BASE_API10_URL_TEST = "http://test.sports.app.m.letv.com/sports/api/";
	/**
	 * 动态请求BaseUrl
	 * */
	public static final String WB_DYNAMIC_BASE_URL_TEST = "http://test.sports.app.m.letv.com/dynamic.php";
	/**
	 * 静态请求head
	 * */
	private static final String WB_STATIC_BASE_HEAD_TEST = "http://test.sports.app.m.letv.com";
	/**
	 * 静态请求end
	 * */
	private static final String WB_STATIC_APP_BASE_END_TEST = ".mindex.html";
	/**
	 * 请求开机广告图url
	 * */
	private static final String WB_STATIC_PUSH_AD_IMAGE = "http://www.letv.com/cmsdata/block/883.json";

	/**
	 * 世界杯开关
	 */

	private static final String WORLD_CUP = "http://sports.app.m.letv.com/sports/api/apistatus?version=%1s&pcode=%2s";
	private static final String WORLD_CUP_TEST = "http://test.sports.app.m.letv.com/sports/api/apistatus?version=%1s&pcode=%2s";

	/**
	 * 得到动态地址
	 **/
	private static String getDynamicUrl() {
		if (PreferencesManager.getInstance().isTestApi()) {
			return DYNAMIC_TEST_BASE_URL;
		} else {
			return DYNAMIC_APP_BASE_URL;
		}
	}

	/**
	 * 得到静态头
	 * */
	private static String getStaticHead() {
		if (PreferencesManager.getInstance().isTestApi()) {
			return STATIC_TEST_BASE_HEAD;
		} else {
			return STATIC_APP_BASE_HEAD;
		}
	}

	private static String getWorldCupUrl() {
		if (PreferencesManager.getInstance().isTestApi()) {
			return WORLD_CUP_TEST;
		} else {
			return WORLD_CUP;
		}
	}

	/**
	 * 得到静态尾
	 * */
	private static String getStaticEnd() {
		if (PreferencesManager.getInstance().isTestApi()) {
			return STATIC_TEST_BASE_END;
		} else {
			return STATIC_APP_BASE_END;
		}
	}

	/**
	 * 看球 得到动态地址
	 * */
	private static String getWbDynamicWBUrl() {
		if (PreferencesManager.getInstance().isTestApi()) {
			return WB_DYNAMIC_BASE_URL_TEST;
		} else {
			return WB_DYNAMIC_BASE_URL;
		}
	}

	/**
	 * 看球 得到静态头
	 * */
	private static String getWbStaticHead() {
		if (PreferencesManager.getInstance().isTestApi()) {
			return WB_STATIC_BASE_HEAD_TEST;
		} else {
			return WB_STATIC_BASE_HEAD;
		}
	}

	/**
	 * 看球 得到静态尾
	 * */
	private static String getWbStaticEnd() {
		if (PreferencesManager.getInstance().isTestApi()) {
			return WB_STATIC_APP_BASE_END_TEST;
		} else {
			return WB_STATIC_APP_BASE_END;
		}
	}

	/**
	 * 看球1.0 base url
	 * */
	private static String getWbAPI10() {
		if (PreferencesManager.getInstance().isTestApi()) {
			return WB_BASE_API10_URL_TEST;
		} else {
			return WB_BASE_API10_URL;
		}
	}

	public static LetvDataHull<AdJoiningBean> requestAdJoining(int i,
			String ahl, String vl, String atl, AdJoiningParser adJoiningParser) {
		return null;
	}

	public static interface REQUEST_URL {
		public String allliveinfos = getWbAPI10() + "all_live";
		public String liveinfos = getWbAPI10() + "liveinfos";
		public String match_list = getWbAPI10() + "match_list";
		public String original_video = getWbAPI10() + "original_video";
		public String video_types = getWbAPI10() + "video_types";
		public String subscribe_match = getWbAPI10() + "subscribe_match";
		public String unsubscribe_match = getWbAPI10() + "unsubscribe_match";
		public String match_schedule = getWbAPI10() + "match_schedule";
		public String table = getWbAPI10() + "table";
		public String subscribe_list = getWbAPI10() + "subscribe_list";
		public String getFocusTeam = getWbAPI10() + "getFocusTeam";
		public String focus = getWbAPI10() + "focus";
		public String unfocus = getWbAPI10() + "unfocus";
		public String my_team_match = getWbAPI10() + "my_team_match";
		public String my_all_matches = getWbAPI10() + "my_all_matches";
		public String vrsVideos_match = getWbAPI10()
				+ "/android/mod/minfo/ctl/videolist/act/index";
		public String match_info = getWbAPI10() + "match_info";
		public String my_teams = getWbAPI10() + "my_teams";
	}

	/**
	 * 公共参数
	 * */
	private static interface PUBLIC_PARAMETERS {
		public String MOD_KEY = "mod";
		public String CTL_KEY = "ctl";
		public String ACT_KEY = "act";
		public String MARKID_KEY = "markid";
		public String PCODE_KEY = "pcode";
		public String VERSION_KEY = "version";
		public String DEVICE_ID = "devId";
		public String CT_KEY = "ct";
		public String ID_KEY = "id";
	}

	/**
	 * 请求PTV视频详情参数
	 * */
	private static interface VIDEO_FILE_PARAMETERS {
		public String MOD_VALUE = "mz";
		public String CTL_VALUE = "videofile";
		public String ACT_VALUE = "index";

		public String MMSID_KEY = "mmsid";
		public String PLAYID_KEY = "playid";
		public String TSS_KEY = "tss";
		public String KEY_KEY = "key";
		public String TM_KEY = "tm";
	}

	/**
	 * 视频直播列表参数
	 * */
	private static interface LIVE_INFOS {
		public String page_index = "page_index";
	}

	private static interface MATCH_LIST {
		public String original_colunm = "original_colunm";
	}

	private static interface ORIGINAL_VIDEO {
		public String columnId = "columnId";
		public String offset = "offset";
		public String max = "max";
	}

	private static interface SUBSCRIBE_MATCH {
		public String matchId = "matchId";
		public String token = "token";
	}

	private static interface MATCH_SCHEDULE {
		public String match_type = "match_type";
		public String round_key = "round_key";
		public String group = "group";
	}

	private static interface TABLE {
		public String match_type = "match_type";
		public String level = "level";
		public String group = "group";
	}

	private static interface FOCUS {
		public String teamId = "teamId";
		public String level = "level";
	}

	private static interface MATCH_INFO {
		public String id = "id";
	}

	private static interface SUBMITPLAYTRACE_PARAMETERS {

		public String MOD_VALUE = "mz";
		public String CTL_VALUE = "cloud";
		public String ACT_VALUE = "add";

		public String CID_KEY = "cid";
		public String PID_KEY = "pid";
		public String VID_KEY = "vid";
		public String NVID_KEY = "nvid";
		public String UID_KEY = "uid";
		public String VTYPE_KEY = "vtype";
		public String FROM_KEY = "from";
		public String HTIME_KEY = "htime";
		public String SSO_TK_KEY = "sso_tk";
	}

	private static interface SUBMITPLAYTRACES_PARAMETERS {

		public String MOD_VALUE = "mz";
		public String CTL_VALUE = "cloud";
		public String ACT_VALUE = "import";

		public String UID_KEY = "uid";
		public String DATA_KEY = "data";
		public String SSO_TK_KEY = "sso_tk";
	}

	private static interface DELETEPLAYTRACES_PARAMETERS {

		public String MOD_VALUE = "mz";
		public String CTL_VALUE = "cloud";
		public String ACT_VALUE = "del";

		public String PID_KEY = "pid";
		public String VID_KEY = "vid";
		public String UID_KEY = "uid";
		public String IDSTR_KEY = "idstr";
		public String FLUSH_KEY = "flush";
		public String BACKDATA_KEY = "backdata";
		public String SSO_TK_KEY = "sso_tk";
	}

	private static interface GETPLAYTRACES_PARAMETERS {

		public String MOD_VALUE = "mz";
		public String CTL_VALUE = "cloud";
		public String ACT_VALUE = "get";

		public String UID_KEY = "uid";
		public String PAGE_KEY = "page";
		public String PAGESIZE_KEY = "pagesize";
		public String SSO_TK_KEY = "sso_tk";
	}

	private static interface GETPLAYTRACE_PARAMETERS {

		public String MOD_VALUE = "mz";
		public String CTL_VALUE = "cloud";
		public String ACT_VALUE = "getPoint";

		public String UID_KEY = "uid";
		public String VID_KEY = "vid";
		public String SSO_TK_KEY = "sso_tk";
	}

	private static interface SEARCHPLAYTRACES_PARAMETERS {

		public String MOD_VALUE = "mz";
		public String CTL_VALUE = "cloud";
		public String ACT_VALUE = "search";

		public String PIDS_KEY = "pids";
		public String VIDS_KEY = "vids";
		public String SSO_TK_KEY = "sso_tk";
	}

	/**
	 * 请求专辑视频列表参数
	 * */
	private static interface VIDEOS_LIST_PARAMETERS {
		public String MOD_VALUE = "mz";
		public String CTL_VALUE = "videolist";
		public String ACT_VALUE = "index";

		public String ID_KEY = "id";// 专辑id
		public String VID_KEY = "vid";// 视频id
										// 若使用此参数，则只返回当前vid所在页数的数据，已经所在页码和索引位置，不使用请不要传此参数
		public String B_KEY = "pn";// 页数--默认为1
		public String S_KEY = "ps";// 每页数量--默认为30
		public String O_KEY = "or";// 排序方式--1:按着集数升序; 1:按着集数降序.默认为:-1
		public String M_KEY = "m";// 合并输出--1:合并;
									// 0:不合并.默认为:1合并videoinfo，yugaopininfo，huaxuinfo，zixuninfo，otherinfo
									// 这些视频列表，统一在videoinfo中输出
	}

	/**
	 * 请求视频详情参数
	 * */
	private static interface VIDEO_INFO_PARAMETERS {
		public String MOD_VALUE = "mz";
		public String CTL_VALUE = "video";
		public String ACT_VALUE = "detail";
		public String ID_KEY = "id";
	}

	/**
	 * 直播防盗链参数
	 * */
	private static interface ANTI_LEECH_PARAMETERS {
		public String FORMAT = "format";
		public String EXPECT = "expect";
	}

	/**
	 * 点播防盗链参数
	 * */
	private static interface TIMESTAMP_PARAMETERS {
		public String MOD_VALUE = "mz";
		public String CTL_VALUE = "timestamp";
		public String ACT_VALUE = "timestamp";
	}

	/**
	 * 直播防盗链参数
	 * */
	private static interface EXPIRE_TIMESTAMP_PARAMETERS {
		public String MOD_VALUE = "mz";
		public String CTL_VALUE = "timeexpirestamp";
		public String ACT_VALUE = "timeExpireStamp";
	}

	/**
	 * 请求分享地址数据参数
	 * http://static.app.m.letv.com/android/mod/minfo/ctl/linkshare/act
	 * /index/pcode/{$pcode}/version/{$version}.mindex.html
	 * */
	private static interface SHARE_PARAMETERS {
		public String MOD_VALUE = "mz";
		public String CTL_VALUE = "linkshare";
		public String ACT_VALUE = "index";

	}

	/**
	 * 请求用户信息参数
	 * */
	private static interface GETUSERBYTK_PARAMETERS {
		public String MOD_VALUE = "passport";
		public String CTL_VALUE = "index";
		public String ACT_VALUE = "getUserByTk";

		public String TK_KEY = "tk";
	}

	/**
	 * 请求个性化推荐数据参数
	 * http://static.app.m.letv.com/android/mod/minfo/ctl/message/act
	 * /index/pcode/{$pcode}/version/{$version}.mindex.html
	 * */
	private static interface DIALOG_PARAMETERS {
		public String MOD_VALUE = "mz";
		public String CTL_VALUE = "message";
		public String ACT_VALUE = "index";
		public String MARKID = "markid";
	}

	/**
	 * 请求用户信息参数
	 * */
	private static interface GETUSERBYID_PARAMETERS {
		public String MOD_VALUE = "passport";
		public String CTL_VALUE = "index";
		public String ACT_VALUE = "getUserByID";

		public String UID_KEY = "uid";
	}

	/**
	 * 用户登录参数
	 * */
	private static interface CLIENTLOGIN_PARAMETERS {
		public String MOD_VALUE = "passport";
		public String CTL_VALUE = "index";
		public String ACT_VALUE = "clientLogin";

		public String LOGINNAME_KEY = "loginname";
		public String PASSWORD_KEY = "password";
		public String REGISTSERVICE_KEY = "registService";
		public String PROFILE_KEY = "profile";
		public String PLAT_KEY = "plat";
	}

	/**
	 * 用户注册参数
	 * */
	private static interface ADDUSER_PARAMETERS {
		public String MOD_VALUE = "passport";
		public String CTL_VALUE = "index";
		public String ACT_VALUE = "addUser";

		public String EMAIL_KEY = "email";
		public String MOBILE_KEY = "mobile";
		public String PASSWORD_KEY = "password";
		public String NICKNAME_KEY = "nickname";
		public String GENDER_KEY = "gender";
		public String REGISTSERVICE_KEY = "registService";
		public String VCODE_KEY = "vcode";
	}

	/**
	 * 邮箱激活邮件下发
	 * */
	private static interface SENDACTIVEEMAIL_PARAMETERS {
		public String MOD_VALUE = "passport";
		public String CTL_VALUE = "index";
		public String ACT_VALUE = "sendActiveEmail";

		public String EMAIL_KEY = "email";
	}

	/**
	 * 检查手机号是否存在
	 * */
	private static interface CHECKMOBILEEXISTS_PARAMETERS {
		public String MOD_VALUE = "passport";
		public String CTL_VALUE = "index";
		public String ACT_VALUE = "checkMobileExists";

		public String MOBILE_KEY = "mobile";
	}

	/**
	 * 检查邮箱是否存在
	 * */
	private static interface CHECKEMAILEXISTS_PARAMETERS {
		public String MOD_VALUE = "passport";
		public String CTL_VALUE = "index";
		public String ACT_VALUE = "checkEmailExists";

		public String EMAIL_KEY = "email";
	}

	/**
	 * 邮箱找回密码邮件下发
	 * */
	private static interface SENDBACKPWDEMAIL_PARAMETERS {
		public String MOD_VALUE = "passport";
		public String CTL_VALUE = "index";
		public String ACT_VALUE = "sendBackPwdEmail";

		public String EMAIL_KEY = "email";
	}

	/**
	 * 修改密码
	 * */
	private static interface MODIFYPWD_PARAMETERS {
		public String MOD_VALUE = "passport";
		public String CTL_VALUE = "index";
		public String ACT_VALUE = "modifyPwd";

		public String TK_KEY = "tk";
		public String OLDPWD_KEY = "oldpwd";
		public String NEWPWD_KEY = "newpwd";
	}

	/**
	 * 手机短信下行接口
	 * */
	private static interface S_SENDMOBILE_PARAMETERS {
		public String MOD_VALUE = "passport";
		public String CTL_VALUE = "index";
		public String ACT_VALUE = "s_sendMobile";

		public String MOBILE_KEY = "mobile";
	}

	/**
	 * @author haitian 通过视频id获取视频或专辑详情
	 */
	private static interface GETALBUMBYID_PARAMETERS {
		public String MOD_VALUE = "mob";
		public String CTL_VALUE = "getalbumbyid";
		public String ACT_VALUE = "detail";
		public String ID_KEY = "id";
	}

	/**
	 * 获取直播券参数
	 */
	private static interface USETICKET_PARAMETERS {
		public String MOD_VALUE = "api20";
		public String CTL_VALUE = "liveuseticket";
		public String ACT_VALUE = "index";

		public String TICKET_TYPE = "tickettype";
		public String USER_ID = "userid";
		public String CHANNEL = "channel";
		public String CATEGORY = "category";
		public String SEASON = "season";
		public String TURN = "turn";
		public String GAME = "game";
		public String APISIGN = "apisign";
	}

	/**
	 * 获取直播券参数
	 */
	private static interface TICKETCOUNT_PARAMETERS {
		public String MOD_VALUE = "api20";
		public String CTL_VALUE = "livegetticket";
		public String ACT_VALUE = "index";

		public String USER_ID = "userid";
		public String CHANNEL = "channel";
		public String CATEGORY = "category";
		public String SEASON = "season";
		public String TURN = "turn";
		public String GAME = "game";
		public String APISIGN = "apisign";
	}

	/**
	 * 鉴权参数
	 */

	private static interface DYNAMICCHECK_PARAMETERS {
		public String MOD_VALUE = "api20";
		public String CTL_VALUE = "livevalidate";
		public String ACT_VALUE = "index";

		public String PID = "pid";
		public String LIVE_ID = "liveid";
		public String FROM = "from";
		public String STREAM_ID = "streamId";
		public String SPLAT_ID = "splatId";
		public String USER_ID = "userId";
		public String LSSTART = "lsstart";
		public String API_SIGN = "apisign";
	}

	/**
	 * 初始化化pcode 和 version
	 * 
	 * @param pcode
	 *            渠道号
	 * @param version
	 *            app版本
	 * */
	public static void initialize(String pcode, String version, String deviceId) {
		PCODE = pcode;
		VERSION = version;
		Log.e("gongmeng", "version:"+VERSION);
		DEVICEID = deviceId;
	}

	public static void setTest(boolean isTest) {
		PreferencesManager.getInstance().setTestApi(isTest);
	}

	/**
	 * 根据参数，调起请求
	 * */
	private static <T extends LetvBaseBean, D> LetvDataHull<T> request(
			LetvHttpBaseParameter<T, D, ?> httpParameter) {
		LetvHttpTool<T> handler = new LetvHttpTool<T>();
		return handler.requsetData(httpParameter);
	}

	// /**
	// * 根据参数，调起请求
	// * */
	// private static <T extends LetvBaseBean, D> LetvDataHull<T>
	// request(LetvHttpParameter<T, D> httpParameter) {
	// LetvHttpTool<T> handler = new LetvHttpTool<T>();
	//
	// return handler.requsetData(httpParameter);
	// }

	/**
	 * 2.3.1.视频所有直播列表接口
	 * 
	 * */
	public static <T extends LetvBaseBean, D> LetvDataHull<T> requestAllLiveinfos(
			LetvBaseParser<T, D> parser) {

		Bundle params = new Bundle();
		params.putString(PUBLIC_PARAMETERS.PCODE_KEY, PCODE);
		params.putString(PUBLIC_PARAMETERS.VERSION_KEY, VERSION);
		LetvHttpParameter<T, D> httpParameter = new LetvHttpParameter<T, D>(
				REQUEST_URL.allliveinfos, params, LetvHttpParameter.Type.GET,
				parser, 0);

		return request(httpParameter);
	}

	/**
	 * 视频直播列表接口
	 * 
	 * */
	public static <T extends LetvBaseBean, D> LetvDataHull<T> requestLiveinfos(
			String page_index, LetvBaseParser<T, D> parser) {

		Bundle params = new Bundle();
		params.putString(PUBLIC_PARAMETERS.PCODE_KEY, PCODE);
		params.putString(PUBLIC_PARAMETERS.VERSION_KEY, VERSION);
		params.putString(LIVE_INFOS.page_index, page_index);
		LetvHttpParameter<T, D> httpParameter = new LetvHttpParameter<T, D>(
				REQUEST_URL.liveinfos, params, LetvHttpParameter.Type.GET,
				parser, 0);

		return request(httpParameter);
	}

	/**
	 * 2.3.3.取得赛事列表接口
	 * 
	 * @param <T>
	 * @param <D>
	 * @param respOriginalProgram
	 *            是否包含原创栏目（默认true）
	 * @param parser
	 * @return
	 */
	public static <T extends LetvBaseBean, D> LetvDataHull<T> requestMatchlist(
			boolean respOriginalProgram, LetvBaseParser<T, D> parser) {

		Bundle params = new Bundle();
		params.putString(PUBLIC_PARAMETERS.PCODE_KEY, PCODE);
		params.putString(PUBLIC_PARAMETERS.VERSION_KEY, VERSION);
		params.putString(MATCH_LIST.original_colunm, (respOriginalProgram ? 1
				: 0) + "");
		LetvHttpParameter<T, D> httpParameter = new LetvHttpParameter<T, D>(
				REQUEST_URL.match_list, params, LetvHttpParameter.Type.GET,
				parser, 0);

		return request(httpParameter);
	}

	/**
	 * 2.3.4. 取得原创节目列表接口
	 * 
	 * @param <T>
	 * @param <D>
	 * @param columnId
	 *            原创栏目id
	 * @param offset
	 *            开始位置下标值，从0开始
	 * @param max
	 *            每页最大值
	 * @param parser
	 * @return
	 */
	public static <T extends LetvBaseBean, D> LetvDataHull<T> requestOriginalVideo(
			String columnId, int offset, int max, LetvBaseParser<T, D> parser) {
		Bundle params = new Bundle();
		params.putString(PUBLIC_PARAMETERS.PCODE_KEY, PCODE);
		params.putString(PUBLIC_PARAMETERS.VERSION_KEY, VERSION);
		params.putString(ORIGINAL_VIDEO.columnId, columnId);
		params.putString(ORIGINAL_VIDEO.offset, String.valueOf(offset));
		params.putString(ORIGINAL_VIDEO.max, String.valueOf(max));
		LetvHttpParameter<T, D> httpParameter = new LetvHttpParameter<T, D>(
				REQUEST_URL.original_video, params, LetvHttpParameter.Type.GET,
				parser, 0);

		return request(httpParameter);
	}

	@Deprecated
	/**
	 * 2.3.6. 视频新闻刷选类型数据接口
	 * 
	 * @param <T>
	 * @param <D>
	 * @param parser
	 * @return
	 */
	public static <T extends LetvBaseBean, D> LetvDataHull<T> requestVideoTypes(
			LetvBaseParser<T, D> parser) {
		Bundle params = new Bundle();
		params.putString(PUBLIC_PARAMETERS.PCODE_KEY, PCODE);
		params.putString(PUBLIC_PARAMETERS.VERSION_KEY, VERSION);
		LetvHttpParameter<T, D> httpParameter = new LetvHttpParameter<T, D>(
				REQUEST_URL.video_types, params, LetvHttpParameter.Type.GET,
				parser, 0);

		return request(httpParameter);
	}

	/**
	 * 2.3.7. 直播比赛订阅数据接口
	 * 
	 * @param <T>
	 * @param <D>
	 * @param matchId
	 *            比赛id
	 * @param deviceId
	 *            设备id
	 * @param parser
	 * @return
	 */
	public static <T extends LetvBaseBean, D> LetvDataHull<T> requestSubscribeMatch(
			String matchId, LetvBaseParser<T, D> parser) {
		Bundle params = new Bundle();
		params.putString(PUBLIC_PARAMETERS.PCODE_KEY, PCODE);
		params.putString(PUBLIC_PARAMETERS.VERSION_KEY, VERSION);
		params.putString(PUBLIC_PARAMETERS.DEVICE_ID, DEVICEID);
		params.putString(SUBSCRIBE_MATCH.matchId, matchId);
		// params.putString(SUBSCRIBE_MATCH.token, "");
		LetvHttpParameter<T, D> httpParameter = new LetvHttpParameter<T, D>(
				REQUEST_URL.subscribe_match, params,
				LetvHttpParameter.Type.GET, parser, 0);

		return request(httpParameter);
	}

	/**
	 * 2.3.8. 取消直播比赛订阅数据接口
	 * 
	 * @param <T>
	 * @param <D>
	 * @param matchId
	 *            比赛id
	 * @param deviceId
	 *            设备id
	 * @param parser
	 * @return
	 */
	public static <T extends LetvBaseBean, D> LetvDataHull<T> requestUnsubscribeMatch(
			String matchId, LetvBaseParser<T, D> parser) {
		Bundle params = new Bundle();
		params.putString(PUBLIC_PARAMETERS.PCODE_KEY, PCODE);
		params.putString(PUBLIC_PARAMETERS.VERSION_KEY, VERSION);
		params.putString(PUBLIC_PARAMETERS.DEVICE_ID, DEVICEID);
		params.putString(SUBSCRIBE_MATCH.matchId, matchId);
		LetvHttpParameter<T, D> httpParameter = new LetvHttpParameter<T, D>(
				REQUEST_URL.unsubscribe_match, params,
				LetvHttpParameter.Type.GET, parser, 0);

		return request(httpParameter);
	}

	/**
	 * 2.3.10. 赛程列表接口
	 * 
	 * @param <T>
	 * @param <D>
	 * @param match_type
	 *            赛事type
	 * @param round_key
	 *            赛事轮次
	 * @param parser
	 * @return
	 */
	public static <T extends LetvBaseBean, D> LetvDataHull<T> requestMatchSchedule(
			String match_type, String round_key, LetvBaseParser<T, D> parser) {
		Bundle params = new Bundle();
		params.putString(PUBLIC_PARAMETERS.PCODE_KEY, PCODE);
		params.putString(PUBLIC_PARAMETERS.VERSION_KEY, VERSION);
		params.putString(MATCH_SCHEDULE.match_type, match_type);
		params.putString(MATCH_SCHEDULE.round_key, round_key);
		params.putString(MATCH_SCHEDULE.group, "1");
		LetvHttpParameter<T, D> httpParameter = new LetvHttpParameter<T, D>(
				REQUEST_URL.match_schedule, params, LetvHttpParameter.Type.GET,
				parser, 0);

		return request(httpParameter);
	}

	/**
	 * 2.3.11. 积分榜接口
	 * 
	 * @param <T>
	 * @param <D>
	 * @param match_type
	 *            赛事type
	 * @param level
	 *            赛事级别，1：足球，2：篮球
	 * @param parser
	 * @return
	 */
	public static <T extends LetvBaseBean, D> LetvDataHull<T> requestTable(
			String match_type, String level, LetvBaseParser<T, D> parser) {
		Bundle params = new Bundle();
		params.putString(PUBLIC_PARAMETERS.PCODE_KEY, PCODE);
		params.putString(PUBLIC_PARAMETERS.VERSION_KEY, VERSION);
		params.putString(TABLE.match_type, match_type + "");
		params.putString(TABLE.level, level + "");
		params.putString(TABLE.group, "1");
		LetvHttpParameter<T, D> httpParameter = new LetvHttpParameter<T, D>(
				REQUEST_URL.table, params, LetvHttpParameter.Type.GET, parser,
				0);

		return request(httpParameter);
	}

	/**
	 * 2.3.14. 所有可关注球队列表接口
	 * 
	 * @param <T>
	 * @param <D>
	 * @param parser
	 * @return
	 */
	public static <T extends LetvBaseBean, D> LetvDataHull<T> requestGetFocusTeam(
			LetvBaseParser<T, D> parser) {
		Bundle params = new Bundle();
		params.putString(PUBLIC_PARAMETERS.PCODE_KEY, PCODE);
		params.putString(PUBLIC_PARAMETERS.VERSION_KEY, VERSION);
		params.putString(PUBLIC_PARAMETERS.DEVICE_ID, DEVICEID);
		LetvHttpParameter<T, D> httpParameter = new LetvHttpParameter<T, D>(
				REQUEST_URL.getFocusTeam, params, LetvHttpParameter.Type.GET,
				parser, 0);

		return request(httpParameter);
	}

	/**
	 * 2.3.15. 关注球队接口
	 * 
	 * @param <T>
	 * @param <D>
	 * @param teamId
	 *            球队id
	 * @param level
	 *            赛事类型，1：足球；2：篮球
	 * @param parser
	 * @return
	 */
	public static <T extends LetvBaseBean, D> LetvDataHull<T> requestFocus(
			String teamId, String level, LetvBaseParser<T, D> parser) {
		Bundle params = new Bundle();
		params.putString(PUBLIC_PARAMETERS.PCODE_KEY, PCODE);
		params.putString(PUBLIC_PARAMETERS.VERSION_KEY, VERSION);
		params.putString(PUBLIC_PARAMETERS.DEVICE_ID, DEVICEID);
		params.putString(FOCUS.teamId, teamId);
		params.putString(FOCUS.level, level);
		LetvHttpParameter<T, D> httpParameter = new LetvHttpParameter<T, D>(
				REQUEST_URL.focus, params, LetvHttpParameter.Type.GET, parser,
				0);

		return request(httpParameter);
	}

	/**
	 * 2.3.16. 取消关注球队接口
	 * 
	 * @param <T>
	 * @param <D>
	 * @param teamId
	 *            球队id
	 * @param level
	 *            赛事类型，1：足球；2：篮球
	 * @param parser
	 * @return
	 */
	public static <T extends LetvBaseBean, D> LetvDataHull<T> requestUnfocus(
			String teamId, String level, LetvBaseParser<T, D> parser) {
		Bundle params = new Bundle();
		params.putString(PUBLIC_PARAMETERS.PCODE_KEY, PCODE);
		params.putString(PUBLIC_PARAMETERS.VERSION_KEY, VERSION);
		params.putString(PUBLIC_PARAMETERS.DEVICE_ID, DEVICEID);
		params.putString(FOCUS.teamId, teamId);
		params.putString(FOCUS.level, level);
		LetvHttpParameter<T, D> httpParameter = new LetvHttpParameter<T, D>(
				REQUEST_URL.unfocus, params, LetvHttpParameter.Type.GET,
				parser, 0);

		return request(httpParameter);
	}

	/**
	 * 2.3.17. 我的球队比赛信息列表接口
	 * 
	 * @param <T>
	 * @param <D>
	 * @param teamId
	 *            球队id
	 * @param level
	 *            赛事类型，1：足球；2：篮球
	 * @param parser
	 * @return
	 */
	public static <T extends LetvBaseBean, D> LetvDataHull<T> requestMyTeamMatch(
			String teamId, String level, LetvBaseParser<T, D> parser) {
		Bundle params = new Bundle();
		params.putString(PUBLIC_PARAMETERS.PCODE_KEY, PCODE);
		params.putString(PUBLIC_PARAMETERS.VERSION_KEY, VERSION);
		params.putString(PUBLIC_PARAMETERS.DEVICE_ID, DEVICEID);
		params.putString(FOCUS.teamId, teamId);
		params.putString(FOCUS.level, level);
		LetvHttpParameter<T, D> httpParameter = new LetvHttpParameter<T, D>(
				REQUEST_URL.my_team_match, params, LetvHttpParameter.Type.GET,
				parser, 0);

		return request(httpParameter);
	}

	@Deprecated
	/**
	 *2.3.18.我的球队全部比赛信息列表接口
	 * 
	 * @param <T>
	 * @param <D>
	 * @param teamId
	 *            球队id
	 * @param level
	 *            赛事类型，1：足球；2：篮球
	 * @param parser
	 * @return
	 */
	public static <T extends LetvBaseBean, D> LetvDataHull<T> requestAllMyTeamMatchs(
			LetvBaseParser<T, D> parser) {
		Bundle params = new Bundle();
		params.putString(PUBLIC_PARAMETERS.PCODE_KEY, PCODE);
		params.putString(PUBLIC_PARAMETERS.VERSION_KEY, VERSION);
		params.putString(PUBLIC_PARAMETERS.DEVICE_ID, DEVICEID);
		LetvHttpParameter<T, D> httpParameter = new LetvHttpParameter<T, D>(
				REQUEST_URL.my_all_matches, params, LetvHttpParameter.Type.GET,
				parser, 0);

		return request(httpParameter);
	}

	/**
	 * 2.3.19.我的球队列表接口
	 * 
	 * @param <T>
	 * @param <D>
	 * @param parser
	 * @return
	 */
	public static <T extends LetvBaseBean, D> LetvDataHull<T> requestMyTeams(
			LetvBaseParser<T, D> parser) {
		Bundle params = new Bundle();
		params.putString(PUBLIC_PARAMETERS.PCODE_KEY, PCODE);
		params.putString(PUBLIC_PARAMETERS.VERSION_KEY, VERSION);
		params.putString(PUBLIC_PARAMETERS.DEVICE_ID, DEVICEID);
		LetvHttpParameter<T, D> httpParameter = new LetvHttpParameter<T, D>(
				REQUEST_URL.my_teams, params, LetvHttpParameter.Type.GET,
				parser, 0);
		return request(httpParameter);
	}

	/**
	 * 2.3.20. Android比分信息轮询接口
	 * 
	 * @return
	 */
	public static <T extends LetvBaseBean, D> LetvDataHull<T> requestMatchInfo(
			String id, LetvBaseParser<T, D> parser) {
		Bundle params = new Bundle();
		params.putString(PUBLIC_PARAMETERS.PCODE_KEY, PCODE);
		params.putString(PUBLIC_PARAMETERS.VERSION_KEY, VERSION);
		params.putString(MATCH_INFO.id, id);
		LetvHttpParameter<T, D> httpParameter = new LetvHttpParameter<T, D>(
				REQUEST_URL.match_info, params, LetvHttpParameter.Type.GET,
				parser, 0);
		Log.d("smy", "requestMatchInfo");
		return request(httpParameter);
	}

	/**
	 * 请求验证海外IP接口
	 * 
	 * @param updataId
	 *            刷新UI或数据的ID
	 * @param parser
	 *            解析器
	 * @return XDataHull<T> 壳数据 1.大陆 0.海外
	 * */
	public static <T extends LetvBaseBean, D> LetvDataHull<T> requestIP(
			int updataId, LetvMainParser<T, D> parser) {
		String baseUrl = IP_BASE_URL;

		LetvHttpParameter<T, D> httpParameter = new LetvHttpParameter<T, D>(
				baseUrl, null, LetvHttpParameter.Type.GET, parser, updataId);

		return request(httpParameter);
	}

	/**
	 * 提交单条播放记录（动态）
	 * 
	 * @param updataId
	 *            刷新id
	 * @param parser
	 * @return
	 */
	public static <T extends LetvBaseBean, D> LetvDataHull<T> submitPlayTrace(
			int updataId, String cid, String pid, String vid, String nvid,
			String uid, String vtype, String from, String htime, String sso_tk,
			LetvMainParser<T, D> parser) {
		String baseUrl = getDynamicUrl();

		Bundle params = new Bundle();
		params.putString(PUBLIC_PARAMETERS.MOD_KEY,
				SUBMITPLAYTRACE_PARAMETERS.MOD_VALUE);
		params.putString(PUBLIC_PARAMETERS.CTL_KEY,
				SUBMITPLAYTRACE_PARAMETERS.CTL_VALUE);
		params.putString(PUBLIC_PARAMETERS.ACT_KEY,
				SUBMITPLAYTRACE_PARAMETERS.ACT_VALUE);
		params.putString(SUBMITPLAYTRACE_PARAMETERS.CID_KEY, cid);
		params.putString(SUBMITPLAYTRACE_PARAMETERS.PID_KEY, pid);
		params.putString(SUBMITPLAYTRACE_PARAMETERS.VID_KEY, vid);
		params.putString(SUBMITPLAYTRACE_PARAMETERS.NVID_KEY, nvid);
		params.putString(SUBMITPLAYTRACE_PARAMETERS.UID_KEY, uid);
		params.putString(SUBMITPLAYTRACE_PARAMETERS.VTYPE_KEY, vtype);
		params.putString(SUBMITPLAYTRACE_PARAMETERS.FROM_KEY, from);
		params.putString(SUBMITPLAYTRACE_PARAMETERS.HTIME_KEY, htime);
		params.putString(SUBMITPLAYTRACE_PARAMETERS.SSO_TK_KEY, sso_tk);
		params.putString(PUBLIC_PARAMETERS.PCODE_KEY, PCODE);
		params.putString(PUBLIC_PARAMETERS.VERSION_KEY, VERSION);
		LetvHttpParameter<T, D> httpParameter = new LetvHttpParameter<T, D>(
				baseUrl, params, LetvHttpParameter.Type.GET, parser, updataId);

		return request(httpParameter);
	}

	/**
	 * 提交多条播放记录（动态）
	 * 
	 * @param updataId
	 *            刷新id
	 * @param parser
	 * @return
	 */
	public static <T extends LetvBaseBean, D> LetvDataHull<T> submitPlayTraces(
			int updataId, String uid, String data, String sso_tk,
			LetvMainParser<T, D> parser) {
		String baseUrl = getDynamicUrl() + "?" + PUBLIC_PARAMETERS.MOD_KEY
				+ "=" + SUBMITPLAYTRACES_PARAMETERS.MOD_VALUE + "&"
				+ PUBLIC_PARAMETERS.CTL_KEY + "="
				+ SUBMITPLAYTRACES_PARAMETERS.CTL_VALUE + "&"
				+ PUBLIC_PARAMETERS.ACT_KEY + "="
				+ SUBMITPLAYTRACES_PARAMETERS.ACT_VALUE + "&"
				+ PUBLIC_PARAMETERS.PCODE_KEY + "=" + PCODE + "&"
				+ PUBLIC_PARAMETERS.VERSION_KEY + "=" + VERSION;

		Bundle params = new Bundle();
		params.putString(SUBMITPLAYTRACES_PARAMETERS.UID_KEY, uid);
		params.putString(SUBMITPLAYTRACES_PARAMETERS.DATA_KEY, data);
		params.putString(SUBMITPLAYTRACES_PARAMETERS.SSO_TK_KEY, sso_tk);
		params.putString(PUBLIC_PARAMETERS.PCODE_KEY, PCODE);
		params.putString(PUBLIC_PARAMETERS.VERSION_KEY, VERSION);
		LetvHttpParameter<T, D> httpParameter = new LetvHttpParameter<T, D>(
				baseUrl, params, LetvHttpParameter.Type.POST, parser, updataId);

		return request(httpParameter);
	}

	/**
	 * 删除播放记录（可以多条）（动态）
	 * 
	 * @param updataId
	 *            刷新id
	 * @param parser
	 * @return
	 */
	public static <T extends LetvBaseBean, D> LetvDataHull<T> deletePlayTraces(
			int updataId, String pid, String vid, String uid, String idstr,
			String flush, String backdata, String sso_tk,
			LetvMainParser<T, D> parser) {
		String baseUrl = getDynamicUrl();

		Bundle params = new Bundle();
		params.putString(PUBLIC_PARAMETERS.MOD_KEY,
				DELETEPLAYTRACES_PARAMETERS.MOD_VALUE);
		params.putString(PUBLIC_PARAMETERS.CTL_KEY,
				DELETEPLAYTRACES_PARAMETERS.CTL_VALUE);
		params.putString(PUBLIC_PARAMETERS.ACT_KEY,
				DELETEPLAYTRACES_PARAMETERS.ACT_VALUE);
		params.putString(DELETEPLAYTRACES_PARAMETERS.PID_KEY, pid);
		params.putString(DELETEPLAYTRACES_PARAMETERS.VID_KEY, vid);
		params.putString(DELETEPLAYTRACES_PARAMETERS.UID_KEY, uid);
		params.putString(DELETEPLAYTRACES_PARAMETERS.IDSTR_KEY, idstr);
		params.putString(DELETEPLAYTRACES_PARAMETERS.FLUSH_KEY, flush);
		params.putString(DELETEPLAYTRACES_PARAMETERS.BACKDATA_KEY, backdata);
		params.putString(DELETEPLAYTRACES_PARAMETERS.SSO_TK_KEY, sso_tk);
		params.putString(PUBLIC_PARAMETERS.PCODE_KEY, PCODE);
		params.putString(PUBLIC_PARAMETERS.VERSION_KEY, VERSION);
		LetvHttpParameter<T, D> httpParameter = new LetvHttpParameter<T, D>(
				baseUrl, params, LetvHttpParameter.Type.GET, parser, updataId);

		return request(httpParameter);
	}

	/**
	 * 获取多条播放记录（动态）
	 * 
	 * @param updataId
	 *            刷新id
	 * @param parser
	 * @return
	 */
	public static <T extends LetvBaseBean, D> LetvDataHull<T> getPlayTraces(
			int updataId, String uid, String page, String pagesize,
			String sso_tk, LetvMainParser<T, D> parser) {
		String baseUrl = getDynamicUrl();

		Bundle params = new Bundle();
		params.putString(PUBLIC_PARAMETERS.MOD_KEY,
				GETPLAYTRACES_PARAMETERS.MOD_VALUE);
		params.putString(PUBLIC_PARAMETERS.CTL_KEY,
				GETPLAYTRACES_PARAMETERS.CTL_VALUE);
		params.putString(PUBLIC_PARAMETERS.ACT_KEY,
				GETPLAYTRACES_PARAMETERS.ACT_VALUE);
		params.putString(GETPLAYTRACES_PARAMETERS.UID_KEY, uid);
		params.putString(GETPLAYTRACES_PARAMETERS.PAGE_KEY, page);
		params.putString(GETPLAYTRACES_PARAMETERS.PAGESIZE_KEY, pagesize);
		params.putString(GETPLAYTRACES_PARAMETERS.SSO_TK_KEY, sso_tk);
		params.putString(PUBLIC_PARAMETERS.PCODE_KEY, PCODE);
		params.putString(PUBLIC_PARAMETERS.VERSION_KEY, VERSION);
		LetvHttpParameter<T, D> httpParameter = new LetvHttpParameter<T, D>(
				baseUrl, params, LetvHttpParameter.Type.GET, parser, updataId);

		return request(httpParameter);
	}

	/**
	 * 获取一条播放记录（动态）
	 * 
	 * @param updataId
	 *            刷新id
	 * @param parser
	 * @return
	 */
	public static <T extends LetvBaseBean, D> LetvDataHull<T> getPlayTrace(
			int updataId, String uid, String vid, String sso_tk,
			LetvMainParser<T, D> parser) {
		String baseUrl = getDynamicUrl();

		Bundle params = new Bundle();
		params.putString(PUBLIC_PARAMETERS.MOD_KEY,
				GETPLAYTRACE_PARAMETERS.MOD_VALUE);
		params.putString(PUBLIC_PARAMETERS.CTL_KEY,
				GETPLAYTRACE_PARAMETERS.CTL_VALUE);
		params.putString(PUBLIC_PARAMETERS.ACT_KEY,
				GETPLAYTRACE_PARAMETERS.ACT_VALUE);
		params.putString(GETPLAYTRACE_PARAMETERS.UID_KEY, uid);
		params.putString(GETPLAYTRACE_PARAMETERS.VID_KEY, vid);
		params.putString(GETPLAYTRACE_PARAMETERS.SSO_TK_KEY, sso_tk);
		params.putString(PUBLIC_PARAMETERS.PCODE_KEY, PCODE);
		params.putString(PUBLIC_PARAMETERS.VERSION_KEY, VERSION);
		LetvHttpParameter<T, D> httpParameter = new LetvHttpParameter<T, D>(
				baseUrl, params, LetvHttpParameter.Type.GET, parser, updataId);

		return request(httpParameter);
	}

	/**
	 * 搜索播放记录（动态）
	 * 
	 * @param updataId
	 *            刷新id
	 * @param parser
	 * @return
	 */
	public static <T extends LetvBaseBean, D> LetvDataHull<T> searchPlayTraces(
			int updataId, String pids, String vids, String sso_tk,
			LetvMainParser<T, D> parser) {
		String baseUrl = getDynamicUrl();

		Bundle params = new Bundle();
		params.putString(PUBLIC_PARAMETERS.MOD_KEY,
				SEARCHPLAYTRACES_PARAMETERS.MOD_VALUE);
		params.putString(PUBLIC_PARAMETERS.CTL_KEY,
				SEARCHPLAYTRACES_PARAMETERS.CTL_VALUE);
		params.putString(PUBLIC_PARAMETERS.ACT_KEY,
				SEARCHPLAYTRACES_PARAMETERS.ACT_VALUE);
		params.putString(SEARCHPLAYTRACES_PARAMETERS.PIDS_KEY, pids);
		params.putString(SEARCHPLAYTRACES_PARAMETERS.VIDS_KEY, vids);
		params.putString(SEARCHPLAYTRACES_PARAMETERS.SSO_TK_KEY, sso_tk);
		params.putString(PUBLIC_PARAMETERS.PCODE_KEY, PCODE);
		params.putString(PUBLIC_PARAMETERS.VERSION_KEY, VERSION);
		LetvHttpParameter<T, D> httpParameter = new LetvHttpParameter<T, D>(
				baseUrl, params, LetvHttpParameter.Type.GET, parser, updataId);

		return request(httpParameter);
	}

	/**
	 * 请求专辑视频详情（静态）
	 * 
	 * @param updataId
	 *            刷新UI或数据的ID
	 * @param id
	 *            专辑ID
	 * @param vtype
	 *            视频格式 mp4,flv,m3u8
	 * @param parser
	 *            解析器
	 * @return XDataHull<T> 壳数据
	 * */
	public static <T extends LetvBaseBean, D> LetvDataHull<T> requestAlbumVideoInfo(
			int updataId, String id, String type, String markId,
			LetvMainParser<T, D> parser) {

		String head = getStaticHead();
		String end = getStaticEnd();

		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();

		params.add(new BasicNameValuePair(PUBLIC_PARAMETERS.MOD_KEY,
				VIDEO_INFO_PARAMETERS.MOD_VALUE));
		params.add(new BasicNameValuePair(PUBLIC_PARAMETERS.CTL_KEY, type));
		params.add(new BasicNameValuePair(PUBLIC_PARAMETERS.ACT_KEY,
				VIDEO_INFO_PARAMETERS.ACT_VALUE));
		params.add(new BasicNameValuePair(VIDEO_INFO_PARAMETERS.ID_KEY, id));
		params.add(new BasicNameValuePair(PUBLIC_PARAMETERS.PCODE_KEY, PCODE));
		params.add(new BasicNameValuePair(PUBLIC_PARAMETERS.VERSION_KEY,
				VERSION));
		params.add(new BasicNameValuePair(PUBLIC_PARAMETERS.MARKID_KEY, markId));

		LetvHttpStaticParameter<T, D> httpParameter = new LetvHttpStaticParameter<T, D>(
				head, end, params, parser, updataId);

		return request(httpParameter);
	}

	/**
	 * 专辑视频列表
	 * 
	 * @param updataId
	 * @param id
	 * @param vid
	 * @param page
	 * @param count
	 * @param merge
	 * @param parser
	 * @param filter
	 * @return
	 */
	public static <T extends LetvBaseBean, D> LetvDataHull<T> requestAlbumVideoList(
			int updataId, String id, String vid, String page, String count,
			String o, String merge, String markId, LetvMainParser<T, D> parser) {
		String head = getStaticHead();// STATIC_TEST_BASE_HEAD2;
		String end = getStaticEnd();// STATIC_TEST_BASE_END2;

		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();

		params.add(new BasicNameValuePair(PUBLIC_PARAMETERS.MOD_KEY,
				VIDEOS_LIST_PARAMETERS.MOD_VALUE));
		params.add(new BasicNameValuePair(PUBLIC_PARAMETERS.CTL_KEY,
				VIDEOS_LIST_PARAMETERS.CTL_VALUE));
		params.add(new BasicNameValuePair(PUBLIC_PARAMETERS.ACT_KEY,
				VIDEOS_LIST_PARAMETERS.ACT_VALUE));
		params.add(new BasicNameValuePair(VIDEOS_LIST_PARAMETERS.ID_KEY, id));
		params.add(new BasicNameValuePair(VIDEOS_LIST_PARAMETERS.VID_KEY, vid));
		params.add(new BasicNameValuePair(VIDEOS_LIST_PARAMETERS.B_KEY, page));
		params.add(new BasicNameValuePair(VIDEOS_LIST_PARAMETERS.S_KEY, count));
		params.add(new BasicNameValuePair(VIDEOS_LIST_PARAMETERS.M_KEY, merge));
		params.add(new BasicNameValuePair(PUBLIC_PARAMETERS.PCODE_KEY, PCODE));
		params.add(new BasicNameValuePair(PUBLIC_PARAMETERS.VERSION_KEY,
				VERSION));
		params.add(new BasicNameValuePair(PUBLIC_PARAMETERS.MARKID_KEY, markId));

		LetvHttpStaticParameter<T, D> httpParameter = new LetvHttpStaticParameter<T, D>(
				head, end, params, parser, updataId);

		return request(httpParameter);
	}

	/**
	 * 请求视频文件信息（动态）
	 * 
	 * @param updataId
	 *            刷新UI或数据的ID
	 * @param mmsid
	 *            媒体资源id,支持多个，用半角逗号隔开
	 * @param playid
	 *            0:点播 1:直播 2:下载
	 * @param tss
	 *            no MP4 , ios ts流
	 * @param parser
	 *            解析器
	 * @return XDataHull<T> 壳数据
	 * */
	public static <T extends LetvBaseBean, D> LetvDataHull<T> requestVideoFile(
			int updataId, String mmsid, String playid, String tss, String tm,
			String key, LetvMainParser<T, D> parser) {
		String baseUrl = getDynamicUrl();

		Bundle params = new Bundle();
		params.putString(PUBLIC_PARAMETERS.MOD_KEY,
				VIDEO_FILE_PARAMETERS.MOD_VALUE);
		params.putString(PUBLIC_PARAMETERS.CTL_KEY,
				VIDEO_FILE_PARAMETERS.CTL_VALUE);
		params.putString(PUBLIC_PARAMETERS.ACT_KEY,
				VIDEO_FILE_PARAMETERS.ACT_VALUE);
		params.putString(VIDEO_FILE_PARAMETERS.MMSID_KEY, mmsid);
		params.putString(VIDEO_FILE_PARAMETERS.PLAYID_KEY, playid);
		params.putString(VIDEO_FILE_PARAMETERS.TSS_KEY, tss);
		params.putString(VIDEO_FILE_PARAMETERS.KEY_KEY, key);
		params.putString(VIDEO_FILE_PARAMETERS.TM_KEY, tm);
		params.putString(PUBLIC_PARAMETERS.PCODE_KEY, PCODE);
		params.putString(PUBLIC_PARAMETERS.VERSION_KEY, VERSION);

		LetvHttpParameter<T, D> httpParameter = new LetvHttpParameter<T, D>(
				baseUrl, params, LetvHttpParameter.Type.GET, parser, updataId);

		return request(httpParameter);
	}

	/**
	 * 得到时间戳
	 * */
	public static <T extends LetvBaseBean, D> LetvDataHull<T> getTimestamp(
			int updataId, LetvMainParser<T, D> parser) {

		String baseUrl = getDynamicUrl();

		Bundle params = new Bundle();
		params.putString(PUBLIC_PARAMETERS.MOD_KEY,
				TIMESTAMP_PARAMETERS.MOD_VALUE);
		params.putString(PUBLIC_PARAMETERS.CTL_KEY,
				TIMESTAMP_PARAMETERS.CTL_VALUE);
		params.putString(PUBLIC_PARAMETERS.ACT_KEY,
				TIMESTAMP_PARAMETERS.ACT_VALUE);
		params.putString(PUBLIC_PARAMETERS.PCODE_KEY, PCODE);
		params.putString(PUBLIC_PARAMETERS.VERSION_KEY, VERSION);

		LetvHttpParameter<T, D> httpParameter = new LetvHttpParameter<T, D>(
				baseUrl, params, LetvHttpParameter.Type.GET, parser, updataId);

		return request(httpParameter);
	}

	/**
	 * 使用直播券
	 */
	public static <T extends LetvBaseBean, D> LetvDataHull<T> useTicket(
			int updataId, String userId, String channel, String category,
			String season, String turn, String game, String type,
			String apisign, LetvMainParser<T, D> parser) {
		String baseUrl = getDynamicUrl();
		Bundle params = new Bundle();
		params.putString(PUBLIC_PARAMETERS.MOD_KEY,
				USETICKET_PARAMETERS.MOD_VALUE);
		params.putString(PUBLIC_PARAMETERS.CTL_KEY,
				USETICKET_PARAMETERS.CTL_VALUE);
		params.putString(PUBLIC_PARAMETERS.ACT_KEY,
				USETICKET_PARAMETERS.ACT_VALUE);
		params.putString(USETICKET_PARAMETERS.TICKET_TYPE, type);
		params.putString(USETICKET_PARAMETERS.USER_ID, userId);
		params.putString(USETICKET_PARAMETERS.CHANNEL, channel);
		params.putString(USETICKET_PARAMETERS.CATEGORY, category);
		params.putString(USETICKET_PARAMETERS.SEASON, season);
		params.putString(USETICKET_PARAMETERS.TURN, turn);
		params.putString(USETICKET_PARAMETERS.GAME, game);
		params.putString(USETICKET_PARAMETERS.APISIGN, apisign);
		params.putString(PUBLIC_PARAMETERS.PCODE_KEY, PCODE);
		params.putString(PUBLIC_PARAMETERS.VERSION_KEY, VERSION);

		LetvHttpParameter<T, D> httpParameter = new LetvHttpParameter<T, D>(
				baseUrl, params, LetvHttpParameter.Type.GET, parser, updataId);

		return request(httpParameter);

	}

	/**
	 * 直播券数量
	 */
	public static <T extends LetvBaseBean, D> LetvDataHull<T> getTicketCount(
			int updataId, String userId, String channel, String category,
			String season, String turn, String game, String apisign,
			LetvMainParser<T, D> parser) {
		String baseUrl = getDynamicUrl();
		Bundle params = new Bundle();
		params.putString(PUBLIC_PARAMETERS.MOD_KEY,
				TICKETCOUNT_PARAMETERS.MOD_VALUE);
		params.putString(PUBLIC_PARAMETERS.CTL_KEY,
				TICKETCOUNT_PARAMETERS.CTL_VALUE);
		params.putString(PUBLIC_PARAMETERS.ACT_KEY,
				TICKETCOUNT_PARAMETERS.ACT_VALUE);

		params.putString(TICKETCOUNT_PARAMETERS.USER_ID, userId);
		params.putString(TICKETCOUNT_PARAMETERS.CHANNEL, channel);
		params.putString(TICKETCOUNT_PARAMETERS.CATEGORY, category);
		params.putString(TICKETCOUNT_PARAMETERS.SEASON, season);
		params.putString(TICKETCOUNT_PARAMETERS.TURN, turn);
		params.putString(TICKETCOUNT_PARAMETERS.GAME, game);
		params.putString(TICKETCOUNT_PARAMETERS.APISIGN, apisign);
		params.putString(PUBLIC_PARAMETERS.PCODE_KEY, PCODE);
		params.putString(PUBLIC_PARAMETERS.VERSION_KEY, VERSION);

		LetvHttpParameter<T, D> httpParameter = new LetvHttpParameter<T, D>(
				baseUrl, params, LetvHttpParameter.Type.GET, parser, updataId);

		return request(httpParameter);

	}

	/**
	 * 鉴权
	 */
	public static <T extends LetvBaseBean, D> LetvDataHull<T> dynamiccheck(
			int updataId, String pid, String liveid, String from,
			String streamId, String splatId, String userId, String lsstart,
			String apisign, LetvMainParser<T, D> parser) {

		String baseUrl = getDynamicUrl();

		Bundle params = new Bundle();
		params.putString(PUBLIC_PARAMETERS.MOD_KEY,
				DYNAMICCHECK_PARAMETERS.MOD_VALUE);
		params.putString(PUBLIC_PARAMETERS.CTL_KEY,
				DYNAMICCHECK_PARAMETERS.CTL_VALUE);
		params.putString(PUBLIC_PARAMETERS.ACT_KEY,
				DYNAMICCHECK_PARAMETERS.ACT_VALUE);
		params.putString(DYNAMICCHECK_PARAMETERS.PID, pid);
		params.putString(DYNAMICCHECK_PARAMETERS.LIVE_ID, liveid);
		params.putString(DYNAMICCHECK_PARAMETERS.FROM, from);
		params.putString(DYNAMICCHECK_PARAMETERS.STREAM_ID, streamId);
		params.putString(DYNAMICCHECK_PARAMETERS.SPLAT_ID, splatId);
		params.putString(DYNAMICCHECK_PARAMETERS.USER_ID, userId);
		params.putString(DYNAMICCHECK_PARAMETERS.LSSTART, lsstart);
		params.putString(DYNAMICCHECK_PARAMETERS.API_SIGN, apisign);
		params.putString(PUBLIC_PARAMETERS.PCODE_KEY, PCODE);
		params.putString(PUBLIC_PARAMETERS.VERSION_KEY, VERSION);

		LetvHttpParameter<T, D> httpParameter = new LetvHttpParameter<T, D>(
				baseUrl, params, LetvHttpParameter.Type.GET, parser, updataId);

		return request(httpParameter);
	}

	/**
	 * 得到过期时间戳
	 * */
	public static <T extends LetvBaseBean, D> LetvDataHull<T> getExpireTimestamp(
			int updataId, LetvMainParser<T, D> parser) {

		String baseUrl = getDynamicUrl();

		Bundle params = new Bundle();
		params.putString(PUBLIC_PARAMETERS.MOD_KEY,
				EXPIRE_TIMESTAMP_PARAMETERS.MOD_VALUE);
		params.putString(PUBLIC_PARAMETERS.CTL_KEY,
				EXPIRE_TIMESTAMP_PARAMETERS.CTL_VALUE);
		params.putString(PUBLIC_PARAMETERS.ACT_KEY,
				EXPIRE_TIMESTAMP_PARAMETERS.ACT_VALUE);
		params.putString(PUBLIC_PARAMETERS.PCODE_KEY, PCODE);
		params.putString(PUBLIC_PARAMETERS.VERSION_KEY, VERSION);

		LetvHttpParameter<T, D> httpParameter = new LetvHttpParameter<T, D>(
				baseUrl, params, LetvHttpParameter.Type.GET, parser, updataId);

		return request(httpParameter);
	}

	/**
	 * 请求真实的播放地址，防盗链
	 * 
	 * @param updataId
	 * @param url
	 * @param parser
	 * @return
	 */
	public static <T extends LetvBaseBean, D> LetvDataHull<T> requestRealLink(
			int updataId, String url, LetvMainParser<T, D> parser) {
		/**
		 * 为兼容老版本，修改如下： format=1(1:json;2:xml) (json格式) expect=3(一次返回3个地址)
		 */
		StringBuilder builder = new StringBuilder(url);
		builder.append("&");
		builder.append(ANTI_LEECH_PARAMETERS.EXPECT);
		builder.append("=");
		builder.append("3");
		builder.append("&");
		builder.append(ANTI_LEECH_PARAMETERS.FORMAT);
		builder.append("=");
		builder.append("1");

		LetvHttpParameter<T, D> httpParameter = new LetvHttpParameter<T, D>(
				builder.toString(), null, LetvHttpParameter.Type.GET, parser,
				updataId);
		return request(httpParameter);
	}

	/**
	 * 请求分享链接规则接口（动态，专有接口）
	 * 
	 * @param updataId
	 *            刷新UI或数据的ID
	 * @param parser
	 *            解析器
	 * @return
	 * */
	public static <T extends LetvBaseBean, D> LetvDataHull<T> requestShareLink(
			int updataId, LetvMainParser<T, D> parser) {
		// http://static.app.m.letv.com/android/mod/minfo/ctl/linkshare/act/index/pcode/{$pcode}/version/{$version}.mindex.html
		String head = getStaticHead();
		String end = getStaticEnd();

		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();

		params.add(new BasicNameValuePair(PUBLIC_PARAMETERS.MOD_KEY,
				SHARE_PARAMETERS.MOD_VALUE));
		params.add(new BasicNameValuePair(PUBLIC_PARAMETERS.CTL_KEY,
				SHARE_PARAMETERS.CTL_VALUE));
		params.add(new BasicNameValuePair(PUBLIC_PARAMETERS.ACT_KEY,
				SHARE_PARAMETERS.ACT_VALUE));
		params.add(new BasicNameValuePair(PUBLIC_PARAMETERS.PCODE_KEY, PCODE));
		params.add(new BasicNameValuePair(PUBLIC_PARAMETERS.VERSION_KEY,
				VERSION));

		LetvHttpStaticParameter<T, D> httpParameter = new LetvHttpStaticParameter<T, D>(
				head, end, params, parser, updataId);

		return request(httpParameter);

	}

	/**
	 * 得到新浪第三方登录地址
	 * */
	public static String getSinaLoginUrl() {
		return "http://dynamic.app.m.letv.com/android/dynamic.php?mod=passport&ctl=index&act=appsina&pcode="
				+ PCODE + "&version=" + VERSION;
	}

	/**
	 * 得到QQ第三方登录地址
	 * */
	public static String getQQLoginUrl() {
		return "http://dynamic.app.m.letv.com/android/dynamic.php?mod=passport&ctl=index&act=appqq&pcode="
				+ PCODE + "&version=" + VERSION;
	}

	/**
	 * 第三方登录
	 * 
	 * @param updataId
	 * @param baseUrl
	 *            登录成功的数据地址
	 * @param parser
	 * @return
	 */
	public static <T extends LetvBaseBean, D> LetvDataHull<T> openIDOAuthLogin(
			int updataId, String baseUrl, LetvMainParser<T, D> parser) {
		LetvHttpParameter<T, D> httpParameter = new LetvHttpParameter<T, D>(
				baseUrl, null, LetvHttpParameter.Type.GET, parser, updataId);
		return request(httpParameter);
	}

	/**
	 * 客户端提示语接口（静态，专有接口）
	 * 
	 * @param
	 * @param parser
	 *            解析器
	 * @return
	 * */
	public static <T extends LetvBaseBean, D> LetvDataHull<T> requestDialogMsgInfo(
			int updataId, String markid, LetvMainParser<T, D> parser) {
		// http://static.app.m.letv.com/android/mod/minfo/ctl/message/act/index/pcode/{$pcode}/version/{$version}.mindex.html
		String head = getStaticHead();
		String end = getStaticEnd();
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();

		params.add(new BasicNameValuePair(PUBLIC_PARAMETERS.MOD_KEY,
				DIALOG_PARAMETERS.MOD_VALUE));
		params.add(new BasicNameValuePair(PUBLIC_PARAMETERS.CTL_KEY,
				DIALOG_PARAMETERS.CTL_VALUE));
		params.add(new BasicNameValuePair(PUBLIC_PARAMETERS.ACT_KEY,
				DIALOG_PARAMETERS.ACT_VALUE));

		params.add(new BasicNameValuePair(DIALOG_PARAMETERS.MARKID, markid));

		params.add(new BasicNameValuePair(PUBLIC_PARAMETERS.PCODE_KEY, PCODE));
		params.add(new BasicNameValuePair(PUBLIC_PARAMETERS.VERSION_KEY,
				VERSION));

		LetvHttpStaticParameter<T, D> httpParameter = new LetvHttpStaticParameter<T, D>(
				head, end, params, parser, updataId);

		return request(httpParameter);

	}

	/**
	 * 请求用户信息接口（动态）
	 * 
	 * @param updataId
	 *            刷新UI或数据的ID
	 * @param uid
	 *            用户ID
	 * @param parser
	 *            解析器
	 * @return XDataHull<T> 壳数据
	 * */
	public static <T extends LetvBaseBean, D> LetvDataHull<T> requestUserInfo(
			int updataId, String uid, LetvMainParser<T, D> parser) {
		String baseUrl = getDynamicUrl();

		Bundle params = new Bundle();
		params.putString(PUBLIC_PARAMETERS.MOD_KEY,
				GETUSERBYID_PARAMETERS.MOD_VALUE);
		params.putString(PUBLIC_PARAMETERS.CTL_KEY,
				GETUSERBYID_PARAMETERS.CTL_VALUE);
		params.putString(PUBLIC_PARAMETERS.ACT_KEY,
				GETUSERBYID_PARAMETERS.ACT_VALUE);
		params.putString(GETUSERBYID_PARAMETERS.UID_KEY, uid);
		params.putString(PUBLIC_PARAMETERS.PCODE_KEY, PCODE);
		params.putString(PUBLIC_PARAMETERS.VERSION_KEY, VERSION);

		LetvHttpParameter<T, D> httpParameter = new LetvHttpParameter<T, D>(
				baseUrl, params, LetvHttpParameter.Type.GET, parser, updataId);

		return request(httpParameter);
	}

	/**
	 * 请求用户信息接口（动态）
	 * 
	 * @param updataId
	 *            刷新UI或数据的ID
	 * @param tk
	 *            用户ID
	 * @param parser
	 *            解析器
	 * @return XDataHull<T> 壳数据
	 * */
	public static <T extends LetvBaseBean, D> LetvDataHull<T> requestUserInfoByTk(
			int updataId, String tk, LetvMainParser<T, D> parser) {
		String baseUrl = getDynamicUrl();

		Bundle params = new Bundle();
		params.putString(PUBLIC_PARAMETERS.MOD_KEY,
				GETUSERBYTK_PARAMETERS.MOD_VALUE);
		params.putString(PUBLIC_PARAMETERS.CTL_KEY,
				GETUSERBYTK_PARAMETERS.CTL_VALUE);
		params.putString(PUBLIC_PARAMETERS.ACT_KEY,
				GETUSERBYTK_PARAMETERS.ACT_VALUE);
		params.putString(GETUSERBYTK_PARAMETERS.TK_KEY, tk);
		params.putString(PUBLIC_PARAMETERS.PCODE_KEY, PCODE);
		params.putString(PUBLIC_PARAMETERS.VERSION_KEY, VERSION);

		LetvHttpParameter<T, D> httpParameter = new LetvHttpParameter<T, D>(
				baseUrl, params, LetvHttpParameter.Type.GET, parser, updataId);

		return request(httpParameter);
	}

	/**
	 * 用户登录接口（动态）
	 * 
	 * @param updataId
	 *            刷新UI或数据的ID
	 * @param loginname
	 *            登录名（支持邮箱/手机/用户名）
	 * @param password
	 *            密码
	 * @param registService
	 *            默认为mapp
	 * @param profile
	 *            当值为1时，获取全部的用户信息
	 * @param parser
	 *            解析器
	 * @return XDataHull<T> 壳数据
	 * */
	public static <T extends LetvBaseBean, D> LetvDataHull<T> login(
			int updataId, String loginname, String password,
			String registService, String profile, LetvMainParser<T, D> parser) {

		String baseUrl = getDynamicUrl() + "?" + PUBLIC_PARAMETERS.MOD_KEY
				+ "=" + CLIENTLOGIN_PARAMETERS.MOD_VALUE + "&"
				+ PUBLIC_PARAMETERS.CTL_KEY + "="
				+ CLIENTLOGIN_PARAMETERS.CTL_VALUE + "&"
				+ PUBLIC_PARAMETERS.ACT_KEY + "="
				+ CLIENTLOGIN_PARAMETERS.ACT_VALUE + "&"
				+ PUBLIC_PARAMETERS.PCODE_KEY + "=" + PCODE + "&"
				+ PUBLIC_PARAMETERS.VERSION_KEY + "=" + VERSION;

		Bundle params = new Bundle();
		// params.putString(PUBLIC_PARAMETERS.MOD_KEY,
		// CLIENTLOGIN_PARAMETERS.MOD_VALUE);
		// params.putString(PUBLIC_PARAMETERS.CTL_KEY,
		// CLIENTLOGIN_PARAMETERS.CTL_VALUE);
		// params.putString(PUBLIC_PARAMETERS.ACT_KEY,
		// CLIENTLOGIN_PARAMETERS.ACT_VALUE);
		params.putString(CLIENTLOGIN_PARAMETERS.LOGINNAME_KEY, loginname);
		params.putString(CLIENTLOGIN_PARAMETERS.PASSWORD_KEY, password);
		params.putString(CLIENTLOGIN_PARAMETERS.REGISTSERVICE_KEY,
				registService);
		params.putString(CLIENTLOGIN_PARAMETERS.PROFILE_KEY, profile);
		params.putString(CLIENTLOGIN_PARAMETERS.PLAT_KEY, "mobile_tv");
		// params.putString(PUBLIC_PARAMETERS.PCODE_KEY, PCODE);
		// params.putString(PUBLIC_PARAMETERS.VERSION_KEY, VERSION);

		LetvHttpParameter<T, D> httpParameter = new LetvHttpParameter<T, D>(
				baseUrl, params, LetvHttpParameter.Type.POST, parser, updataId);

		return request(httpParameter);
	}

	/**
	 * 用户注册接口（动态）
	 * 
	 * @param updataId
	 *            刷新UI或数据的ID
	 * @param email
	 *            用户名
	 * @param mobile
	 *            手机号
	 * @param password
	 *            密码
	 * @param nickname
	 *            昵称
	 * @param gender
	 *            性别:0保密,1=>男,2=>女
	 * @param registService
	 *            默认为mapp
	 * @param vcode
	 *            手机验证码
	 * @param parser
	 *            解析器
	 * @return XDataHull<T> 壳数据
	 * */
	public static <T extends LetvBaseBean, D> LetvDataHull<T> register(
			int updataId, String email, String mobile, String password,
			String nickname, String gender, String registService, String vcode,
			LetvMainParser<T, D> parser) {
		String baseUrl = getDynamicUrl() + "?" + PUBLIC_PARAMETERS.MOD_KEY
				+ "=" + ADDUSER_PARAMETERS.MOD_VALUE + "&"
				+ PUBLIC_PARAMETERS.CTL_KEY + "="
				+ ADDUSER_PARAMETERS.CTL_VALUE + "&"
				+ PUBLIC_PARAMETERS.ACT_KEY + "="
				+ ADDUSER_PARAMETERS.ACT_VALUE + "&"
				+ PUBLIC_PARAMETERS.PCODE_KEY + "=" + PCODE + "&"
				+ PUBLIC_PARAMETERS.VERSION_KEY + "=" + VERSION;

		Bundle params = new Bundle();
		params.putString(ADDUSER_PARAMETERS.EMAIL_KEY, email);
		params.putString(ADDUSER_PARAMETERS.MOBILE_KEY, mobile);
		params.putString(ADDUSER_PARAMETERS.PASSWORD_KEY, password);
		params.putString(ADDUSER_PARAMETERS.NICKNAME_KEY, nickname);
		params.putString(ADDUSER_PARAMETERS.GENDER_KEY, gender);
		params.putString(ADDUSER_PARAMETERS.REGISTSERVICE_KEY, registService);
		params.putString(ADDUSER_PARAMETERS.VCODE_KEY, vcode);

		LetvHttpParameter<T, D> httpParameter = new LetvHttpParameter<T, D>(
				baseUrl, params, LetvHttpParameter.Type.POST, parser, updataId);

		return request(httpParameter);
	}

	/**
	 * 邮箱激活邮件下发（动态）
	 * 
	 * @param updataId
	 *            刷新UI或数据的ID
	 * @param email
	 *            邮箱
	 * @param parser
	 *            解析器
	 * @return XDataHull<T> 壳数据
	 * */
	public static <T extends LetvBaseBean, D> LetvDataHull<T> sendActiveEmail(
			int updataId, String email, LetvMainParser<T, D> parser) {
		String baseUrl = getDynamicUrl();

		Bundle params = new Bundle();
		params.putString(PUBLIC_PARAMETERS.MOD_KEY,
				SENDACTIVEEMAIL_PARAMETERS.MOD_VALUE);
		params.putString(PUBLIC_PARAMETERS.CTL_KEY,
				SENDACTIVEEMAIL_PARAMETERS.CTL_VALUE);
		params.putString(PUBLIC_PARAMETERS.ACT_KEY,
				SENDACTIVEEMAIL_PARAMETERS.ACT_VALUE);
		params.putString(SENDACTIVEEMAIL_PARAMETERS.EMAIL_KEY, email);
		params.putString(PUBLIC_PARAMETERS.PCODE_KEY, PCODE);
		params.putString(PUBLIC_PARAMETERS.VERSION_KEY, VERSION);

		LetvHttpParameter<T, D> httpParameter = new LetvHttpParameter<T, D>(
				baseUrl, params, LetvHttpParameter.Type.GET, parser, updataId);

		return request(httpParameter);
	}

	/**
	 * 检查手机号码是否存在（动态）
	 * 
	 * @param updataId
	 *            刷新UI或数据的ID
	 * @param mobile
	 *            手机号
	 * @param parser
	 *            解析器
	 * @return XDataHull<T> 壳数据
	 * */
	public static <T extends LetvBaseBean, D> LetvDataHull<T> checkMobileExists(
			int updataId, String mobile, LetvMainParser<T, D> parser) {
		String baseUrl = getDynamicUrl();

		Bundle params = new Bundle();
		params.putString(PUBLIC_PARAMETERS.MOD_KEY,
				CHECKMOBILEEXISTS_PARAMETERS.MOD_VALUE);
		params.putString(PUBLIC_PARAMETERS.CTL_KEY,
				CHECKMOBILEEXISTS_PARAMETERS.CTL_VALUE);
		params.putString(PUBLIC_PARAMETERS.ACT_KEY,
				CHECKMOBILEEXISTS_PARAMETERS.ACT_VALUE);
		params.putString(CHECKMOBILEEXISTS_PARAMETERS.MOBILE_KEY, mobile);
		params.putString(PUBLIC_PARAMETERS.PCODE_KEY, PCODE);
		params.putString(PUBLIC_PARAMETERS.VERSION_KEY, VERSION);

		LetvHttpParameter<T, D> httpParameter = new LetvHttpParameter<T, D>(
				baseUrl, params, LetvHttpParameter.Type.GET, parser, updataId);

		return request(httpParameter);
	}

	/**
	 * 检查邮箱是否存在（动态）
	 * 
	 * @param updataId
	 *            刷新UI或数据的ID
	 * @param email
	 *            邮箱
	 * @param parser
	 *            解析器
	 * @return XDataHull<T> 壳数据
	 * */
	public static <T extends LetvBaseBean, D> LetvDataHull<T> checkEmailExists(
			int updataId, String email, LetvMainParser<T, D> parser) {
		String baseUrl = getDynamicUrl();

		Bundle params = new Bundle();
		params.putString(PUBLIC_PARAMETERS.MOD_KEY,
				CHECKEMAILEXISTS_PARAMETERS.MOD_VALUE);
		params.putString(PUBLIC_PARAMETERS.CTL_KEY,
				CHECKEMAILEXISTS_PARAMETERS.CTL_VALUE);
		params.putString(PUBLIC_PARAMETERS.ACT_KEY,
				CHECKEMAILEXISTS_PARAMETERS.ACT_VALUE);
		params.putString(CHECKEMAILEXISTS_PARAMETERS.EMAIL_KEY, email);
		params.putString(PUBLIC_PARAMETERS.PCODE_KEY, PCODE);
		params.putString(PUBLIC_PARAMETERS.VERSION_KEY, VERSION);

		LetvHttpParameter<T, D> httpParameter = new LetvHttpParameter<T, D>(
				baseUrl, params, LetvHttpParameter.Type.GET, parser, updataId);

		return request(httpParameter);
	}

	/**
	 * 邮箱找回密码邮件下发（动态）
	 * 
	 * @param updataId
	 *            刷新UI或数据的ID
	 * @param email
	 *            邮箱
	 * @param parser
	 *            解析器
	 * @return XDataHull<T> 壳数据
	 * */
	public static <T extends LetvBaseBean, D> LetvDataHull<T> sendBackPwdEmail(
			int updataId, String email, LetvMainParser<T, D> parser) {
		String baseUrl = getDynamicUrl();

		Bundle params = new Bundle();
		params.putString(PUBLIC_PARAMETERS.MOD_KEY,
				SENDBACKPWDEMAIL_PARAMETERS.MOD_VALUE);
		params.putString(PUBLIC_PARAMETERS.CTL_KEY,
				SENDBACKPWDEMAIL_PARAMETERS.CTL_VALUE);
		params.putString(PUBLIC_PARAMETERS.ACT_KEY,
				SENDBACKPWDEMAIL_PARAMETERS.ACT_VALUE);
		params.putString(SENDBACKPWDEMAIL_PARAMETERS.EMAIL_KEY, email);
		params.putString(PUBLIC_PARAMETERS.PCODE_KEY, PCODE);
		params.putString(PUBLIC_PARAMETERS.VERSION_KEY, VERSION);

		LetvHttpParameter<T, D> httpParameter = new LetvHttpParameter<T, D>(
				baseUrl, params, LetvHttpParameter.Type.GET, parser, updataId);

		return request(httpParameter);
	}

	/**
	 * 修改密码（动态）
	 * 
	 * @param updataId
	 *            刷新UI或数据的ID
	 * @param uid
	 *            用户ID
	 * @param oldpwd
	 *            旧密码
	 * @param newpwd
	 *            新密码
	 * @param parser
	 *            解析器
	 * @return XDataHull<T> 壳数据
	 * */
	public static <T extends LetvBaseBean, D> LetvDataHull<T> modifyPwd(
			int updataId, String tk, String oldpwd, String newpwd,
			LetvMainParser<T, D> parser) {
		String baseUrl = getDynamicUrl() + "?" + PUBLIC_PARAMETERS.MOD_KEY
				+ "=" + MODIFYPWD_PARAMETERS.MOD_VALUE + "&"
				+ PUBLIC_PARAMETERS.CTL_KEY + "="
				+ MODIFYPWD_PARAMETERS.CTL_VALUE + "&"
				+ PUBLIC_PARAMETERS.ACT_KEY + "="
				+ MODIFYPWD_PARAMETERS.ACT_VALUE + "&"
				+ PUBLIC_PARAMETERS.PCODE_KEY + "=" + PCODE + "&"
				+ PUBLIC_PARAMETERS.VERSION_KEY + "=" + VERSION;

		Bundle params = new Bundle();
		params.putString(MODIFYPWD_PARAMETERS.TK_KEY, tk);
		params.putString(MODIFYPWD_PARAMETERS.OLDPWD_KEY, oldpwd);
		params.putString(MODIFYPWD_PARAMETERS.NEWPWD_KEY, newpwd);

		// version>5.1请求类型变更为Post
		LetvHttpParameter<T, D> httpParameter = new LetvHttpParameter<T, D>(
				baseUrl, params, LetvHttpParameter.Type.POST, parser, updataId);

		return request(httpParameter);
	}

	/**
	 * 手机短信下行接口
	 * 
	 * @param updataId
	 *            刷新UI或数据的ID
	 * @param mobile
	 *            手机号
	 * @param parser
	 *            解析器
	 * @return XDataHull<T> 壳数据
	 * */
	public static <T extends LetvBaseBean, D> LetvDataHull<T> s_sendMobile(
			int updataId, String mobile, LetvMainParser<T, D> parser) {
		String baseUrl = getDynamicUrl();

		Bundle params = new Bundle();
		params.putString(PUBLIC_PARAMETERS.MOD_KEY,
				S_SENDMOBILE_PARAMETERS.MOD_VALUE);
		params.putString(PUBLIC_PARAMETERS.CTL_KEY,
				S_SENDMOBILE_PARAMETERS.CTL_VALUE);
		params.putString(PUBLIC_PARAMETERS.ACT_KEY,
				S_SENDMOBILE_PARAMETERS.ACT_VALUE);
		params.putString(S_SENDMOBILE_PARAMETERS.MOBILE_KEY, mobile);
		params.putString(PUBLIC_PARAMETERS.PCODE_KEY, PCODE);
		params.putString(PUBLIC_PARAMETERS.VERSION_KEY, VERSION);

		LetvHttpParameter<T, D> httpParameter = new LetvHttpParameter<T, D>(
				baseUrl, params, LetvHttpParameter.Type.GET, parser, updataId);

		return request(httpParameter);
	}

	/**
	 * 关注赛事所有球队
	 */

	private static interface FOCUS_MATCH {
		public String MOD_VALUE = "api20";
		public String CTL_VALUE = "index";
		public String ACT_VALUE = "focus_match";
		public String MATCHID = "matchId";
		/**
		 * 1表示足球，2表示篮球
		 */
		public String LEVEL = "level";
		/**
		 * 1表示关注，0表示取消关注
		 */
		public String FLAG = "flag";
	}

	/**
	 * 焦点图
	 * */
	private static interface FOCUS_PIC {
		public String MOD_VALUE = "mz";
		public String CTL_VALUE = "index";
		public String ACT_VALUE = "focusPic";

	}

	/**
	 * 视频新闻列表
	 */
	private static interface LIST_VIDEO {
		public String MOD_VALUE = "mz";
		public String CTL_VALUE = "listvideo";
		public String ACT_VALUE = "index";
		public String SC = "sc";
		public String PN = "pn";
		public String PS = "ps";
		public String PH = "ph";
		public String OR = "or";
	}

	/**
	 * 视频新闻筛选类型
	 */
	private static interface VIDEO_TYPES {
		public String MOD_VALUE = "mz";
		public String CTL_VALUE = "index";
		public String ACT_VALUE = "videoTypes";
	}

	/**
	 * 视频相关新闻
	 * */
	private static interface VIDEO_LIST {
		public String MOD_VALUE = "mz";
		public String CTL_VALUE = "videolist";
		public String ACT_VALUE = "detail";
		public String ID = "id";
		public String VID = "vid";
		public String PN = "pn";
		public String PS = "ps";
		public String M = "m";
	}

	/**
	 * 用户提醒赛事列表
	 */
	private static interface MATCHES_REMIND {
		public String MOD_VALUE = "api20";
		public String CTL_VALUE = "index";
		public String ACT_VALUE = "matches_remind";
	}

	/**
	 * 发表评论
	 */
	private static interface ADD_COMMENT {
		public String MOD_VALUE = "api20";
		public String CTL_VALUE = "comment";
		public String ACT_VALUE = "addComment";
		public String TOKEN = "token";
		public String VID = "vid";
		public String CONTENT = "content";
	}

	/**
	 * 获取评论列表
	 */
	private static interface GET_COMMENT {
		public String MOD_VALUE = "api20";
		public String CTL_VALUE = "comment";
		public String ACT_VALUE = "getComment";
		public String TOKEN = "token";
		public String UID = "uid";
		public String VID = "vid";
		public String PN = "pn";
		public String PS = "ps";
	}

	/**
	 * 请求意见反馈接口参数
	 * */
	private static interface FEEDBACK_PARAMETERS {
		public String MOD_VALUE = "mz";
		public String CTL_VALUE = "feedback";
		public String ACT_VALUE = "post";

		public String DEVID_KEY = "devid";
		public String NAME_KEY = "name";
		public String SYSNAME_KEY = "sysname";
		public String SYSVER_KEY = "sysver";
		public String MODEL_KEY = "model";
		public String LMODEL_KEY = "lmodel";
		public String TYPE_KEY = "type";
		public String FEEDBACK_KEY = "feedback";
		public String SEX_KEY = "sex";
		public String AGEGROUP_KEY = "agegroup";
		public String SIGN_KEY = "sign";
	}

	/**
	 * 赛事关注接口
	 */

	public static <T extends LetvBaseBean, D> LetvDataHull<T> requestFocusMatch(
			int updataId, String matchId, String level, String flag,
			LetvBaseParser<T, D> parser) {

		String baseUrl = getWbDynamicWBUrl();

		Bundle params = new Bundle();

		params.putString(PUBLIC_PARAMETERS.MOD_KEY, FOCUS_MATCH.MOD_VALUE);
		params.putString(PUBLIC_PARAMETERS.CTL_KEY, FOCUS_MATCH.CTL_VALUE);
		params.putString(PUBLIC_PARAMETERS.ACT_KEY, FOCUS_MATCH.ACT_VALUE);
		params.putString(FOCUS_MATCH.MATCHID, matchId);
		params.putString(FOCUS_MATCH.LEVEL, level);
		params.putString(FOCUS_MATCH.FLAG, flag);
		params.putString(PUBLIC_PARAMETERS.DEVICE_ID, DEVICEID);
		params.putString(PUBLIC_PARAMETERS.PCODE_KEY, PCODE);
		params.putString(PUBLIC_PARAMETERS.VERSION_KEY, VERSION);

		LetvHttpParameter<T, D> httpParameter = new LetvHttpParameter<T, D>(
				baseUrl, params, LetvHttpParameter.Type.GET, parser, updataId);

		return request(httpParameter);
	}

	/**
	 * 焦点图
	 * 
	 * @param <T>
	 * @param <D>
	 * @param updataId
	 * @return
	 */
	public static <T extends LetvBaseBean, D> LetvDataHull<T> requestFocusPic(
			int updataId, LetvBaseParser<T, D> parser) {

		String head = getWbStaticHead();
		String end = getWbStaticEnd();

		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();

		params.add(new BasicNameValuePair(PUBLIC_PARAMETERS.MOD_KEY,
				FOCUS_PIC.MOD_VALUE));
		params.add(new BasicNameValuePair(PUBLIC_PARAMETERS.CTL_KEY,
				FOCUS_PIC.CTL_VALUE));
		params.add(new BasicNameValuePair(PUBLIC_PARAMETERS.ACT_KEY,
				FOCUS_PIC.ACT_VALUE));
		params.add(new BasicNameValuePair(PUBLIC_PARAMETERS.PCODE_KEY, PCODE));
		params.add(new BasicNameValuePair(PUBLIC_PARAMETERS.VERSION_KEY,
				VERSION));

		LetvHttpStaticParameter<T, D> httpParameter = new LetvHttpStaticParameter<T, D>(
				head, end, params, parser, updataId);

		return request(httpParameter);
	}

	/**
	 * 视频新闻列表
	 * 
	 * @return
	 */
	public static <T extends LetvBaseBean, D> LetvDataHull<T> requestListVideo(
			int updataId, String sc, String pn, String ps, String or,
			LetvBaseParser<T, D> parser) {

		String head = getWbStaticHead();
		String end = getWbStaticEnd();

		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();

		params.add(new BasicNameValuePair(PUBLIC_PARAMETERS.MOD_KEY,
				LIST_VIDEO.MOD_VALUE));
		params.add(new BasicNameValuePair(PUBLIC_PARAMETERS.CTL_KEY,
				LIST_VIDEO.CTL_VALUE));
		params.add(new BasicNameValuePair(PUBLIC_PARAMETERS.ACT_KEY,
				LIST_VIDEO.ACT_VALUE));
		params.add(new BasicNameValuePair(LIST_VIDEO.SC, sc));
		params.add(new BasicNameValuePair(LIST_VIDEO.PN, pn));
		params.add(new BasicNameValuePair(LIST_VIDEO.PS, ps));
		params.add(new BasicNameValuePair(LIST_VIDEO.PH, "420003"));
		params.add(new BasicNameValuePair(LIST_VIDEO.OR, or));
		params.add(new BasicNameValuePair(PUBLIC_PARAMETERS.PCODE_KEY, PCODE));
		params.add(new BasicNameValuePair(PUBLIC_PARAMETERS.VERSION_KEY,
				VERSION));

		LetvHttpStaticParameter<T, D> httpParameter = new LetvHttpStaticParameter<T, D>(
				head, end, params, parser, updataId);

		return request(httpParameter);
	}

	/**
	 * 视频新闻筛选类型
	 */
	public static <T extends LetvBaseBean, D> LetvDataHull<T> requestVideoTypes(
			int updataId, LetvBaseParser<T, D> parser) {

		String head = getWbStaticHead();
		String end = getWbStaticEnd();

		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();

		params.add(new BasicNameValuePair(PUBLIC_PARAMETERS.MOD_KEY,
				VIDEO_TYPES.MOD_VALUE));
		params.add(new BasicNameValuePair(PUBLIC_PARAMETERS.CTL_KEY,
				VIDEO_TYPES.CTL_VALUE));
		params.add(new BasicNameValuePair(PUBLIC_PARAMETERS.ACT_KEY,
				VIDEO_TYPES.ACT_VALUE));
		params.add(new BasicNameValuePair(PUBLIC_PARAMETERS.PCODE_KEY, PCODE));
		params.add(new BasicNameValuePair(PUBLIC_PARAMETERS.VERSION_KEY,
				VERSION));

		LetvHttpStaticParameter<T, D> httpParameter = new LetvHttpStaticParameter<T, D>(
				head, end, params, parser, updataId);

		return request(httpParameter);
	}

	/**
	 * 视频相关新闻
	 */
	public static <T extends LetvBaseBean, D> LetvDataHull<T> requestVideoList(
			int updataId, String id, String vid, String pn, String ps,
			LetvBaseParser<T, D> parser) {

		String head = getWbStaticHead();
		String end = getWbStaticEnd();

		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();

		params.add(new BasicNameValuePair(PUBLIC_PARAMETERS.MOD_KEY,
				VIDEO_LIST.MOD_VALUE));
		params.add(new BasicNameValuePair(PUBLIC_PARAMETERS.CTL_KEY,
				VIDEO_LIST.CTL_VALUE));
		params.add(new BasicNameValuePair(PUBLIC_PARAMETERS.ACT_KEY,
				VIDEO_LIST.ACT_VALUE));
		params.add(new BasicNameValuePair(VIDEO_LIST.ID, id));
		params.add(new BasicNameValuePair(VIDEO_LIST.VID, vid));
		params.add(new BasicNameValuePair(VIDEO_LIST.PN, pn));
		params.add(new BasicNameValuePair(VIDEO_LIST.PS, ps));
		params.add(new BasicNameValuePair(VIDEO_LIST.M, 1 + ""));
		params.add(new BasicNameValuePair(PUBLIC_PARAMETERS.PCODE_KEY, PCODE));
		params.add(new BasicNameValuePair(PUBLIC_PARAMETERS.VERSION_KEY,
				VERSION));

		LetvHttpStaticParameter<T, D> httpParameter = new LetvHttpStaticParameter<T, D>(
				head, end, params, parser, updataId);

		return request(httpParameter);
	}

	/**
	 * 用户提醒赛事列表
	 */

	public static <T extends LetvBaseBean, D> LetvDataHull<T> requestMatchesRemind(
			int updataId, LetvBaseParser<T, D> parser) {

		String baseUrl = getWbDynamicWBUrl();

		Bundle params = new Bundle();
		params.putString(PUBLIC_PARAMETERS.MOD_KEY, MATCHES_REMIND.MOD_VALUE);
		params.putString(PUBLIC_PARAMETERS.CTL_KEY, MATCHES_REMIND.CTL_VALUE);
		params.putString(PUBLIC_PARAMETERS.ACT_KEY, MATCHES_REMIND.ACT_VALUE);
		params.putString(PUBLIC_PARAMETERS.DEVICE_ID, DEVICEID);
		params.putString(PUBLIC_PARAMETERS.PCODE_KEY, PCODE);
		params.putString(PUBLIC_PARAMETERS.VERSION_KEY, VERSION);

		LetvHttpParameter<T, D> httpParameter = new LetvHttpParameter<T, D>(
				baseUrl, params, LetvHttpParameter.Type.GET, parser, updataId);

		return request(httpParameter);
	}

	/**
	 * 发表评论
	 */
	public static <T extends LetvBaseBean, D> LetvDataHull<T> requestAddComment(
			int updataId, LetvBaseParser<T, D> parser, String vid,
			String content) {

		String baseUrl = getWbDynamicWBUrl();

		Bundle params = new Bundle();

		params.putString(PUBLIC_PARAMETERS.MOD_KEY, ADD_COMMENT.MOD_VALUE);
		params.putString(PUBLIC_PARAMETERS.CTL_KEY, ADD_COMMENT.CTL_VALUE);
		params.putString(PUBLIC_PARAMETERS.ACT_KEY, ADD_COMMENT.ACT_VALUE);
		params.putString(ADD_COMMENT.TOKEN, PreferencesManager.getInstance()
				.getSso_tk());
		params.putString(ADD_COMMENT.VID, vid);
		params.putString(ADD_COMMENT.CONTENT, content);
		params.putString(PUBLIC_PARAMETERS.PCODE_KEY, PCODE);
		params.putString(PUBLIC_PARAMETERS.VERSION_KEY, VERSION);

		LetvHttpParameter<T, D> httpParameter = new LetvHttpParameter<T, D>(
				baseUrl, params, LetvHttpParameter.Type.GET, parser, updataId);

		return request(httpParameter);
	}

	/**
	 * 获取评论列表
	 */

	public static <T extends LetvBaseBean, D> LetvDataHull<T> requestGetComment(
			int updataId, LetvBaseParser<T, D> parser, String vid, String pn,
			String ps) {

		String baseUrl = getWbDynamicWBUrl();

		Bundle params = new Bundle();

		params.putString(PUBLIC_PARAMETERS.MOD_KEY, GET_COMMENT.MOD_VALUE);
		params.putString(PUBLIC_PARAMETERS.CTL_KEY, GET_COMMENT.CTL_VALUE);
		params.putString(PUBLIC_PARAMETERS.ACT_KEY, GET_COMMENT.ACT_VALUE);
		params.putString(GET_COMMENT.TOKEN, PreferencesManager.getInstance()
				.getSso_tk());
		params.putString(GET_COMMENT.UID, PreferencesManager.getInstance()
				.getUserId());
		params.putString(GET_COMMENT.VID, vid);
		params.putString(GET_COMMENT.PN, pn);
		params.putString(GET_COMMENT.PS, ps);
		params.putString(PUBLIC_PARAMETERS.PCODE_KEY, PCODE);
		params.putString(PUBLIC_PARAMETERS.VERSION_KEY, VERSION);
		LetvHttpParameter<T, D> httpParameter = new LetvHttpParameter<T, D>(
				baseUrl, params, LetvHttpParameter.Type.GET, parser, updataId);

		return request(httpParameter);
	}

	/**
	 * 请求反馈接口（动态）
	 * 
	 * @param updataId
	 *            刷新UI或数据的ID
	 * @param devid
	 *            设备ID
	 * @param name
	 *            名称
	 * @param sysname
	 *            系统名称
	 * @param sysver
	 *            系统版本
	 * @param model
	 *            model
	 * @param lmodel
	 *            lmodel
	 * @param type
	 *            反馈类型 0.故障投诉 1.改善建议 2.内容需求 3.新手咨询 4.其他
	 * @param feedback
	 *            反馈意见
	 * @param email
	 *            邮箱
	 * @param mobile
	 *            手机
	 * @param parser
	 *            解析器
	 * @return XDataHull<T> 壳数据
	 * */
	public static <T extends LetvBaseBean, D> LetvDataHull<T> requestFeedBack(
			int updataId, String devid, String name, String sysname,
			String sysver, String model, String lmodel, String feedback,
			String sex, String agegroup, LetvMainParser<T, D> parser) {
		// http://dynamic.app.m.letv.com/android/dynamic.php?mod=mob&ctl=feedback&act=index&pcode={$pcode}&version={$version}
		/**
		 * devid 设备id 字符串 是 POST提交 name 设备名称 字符串 是 POST提交 sysname 系统名称 字符串 是
		 * POST提交 sysver 系统版本 字符串 是 POST提交 model model 字符串 是 POST提交 lmodel
		 * lmodel 字符串 是 POST提交 feedback 反馈内容 字符串 是 POST提交 sex 性别：1-男，2-女 字符串 是
		 * POST提交 agegroup 年龄段 字符串 是
		 * POST提交,年龄段取值对应区间：1:18岁以下,2:18岁-24岁,3:25岁-30岁
		 * ,4:31岁-35岁,5:36岁-40岁,6:41岁-45岁,7:46岁-50岁,8:60岁以上 sign 签名 字符串 是
		 * POST提交, 生成规则（拼接参数不可少，顺序不可变）：
		 * md5(devid={$devid}&name={$name}&sysname={$sysname}&sysver={$sysver}&
		 * model
		 * ={$model}&lmodel={$lmodel}&feedback={$feedback}&sex={$sex}&agegroup
		 * ={$agegroup}&{$sign_key}) sign_key取值为：letvmobile2013
		 * 
		 * pcode 产品代码 字符串 是 GET传参：pcode version 版本号 字符串 是 GET传参：version
		 * 
		 */
		String baseUrl = getDynamicUrl() + "?" + PUBLIC_PARAMETERS.MOD_KEY
				+ "=" + FEEDBACK_PARAMETERS.MOD_VALUE + "&"
				+ PUBLIC_PARAMETERS.CTL_KEY + "="
				+ FEEDBACK_PARAMETERS.CTL_VALUE + "&"
				+ PUBLIC_PARAMETERS.ACT_KEY + "="
				+ FEEDBACK_PARAMETERS.ACT_VALUE + "&"
				+ PUBLIC_PARAMETERS.PCODE_KEY + "=" + PCODE + "&"
				+ PUBLIC_PARAMETERS.VERSION_KEY + "=" + VERSION;
		Bundle params = new Bundle();
		params.putString(FEEDBACK_PARAMETERS.DEVID_KEY, devid);
		params.putString(FEEDBACK_PARAMETERS.NAME_KEY, name);
		params.putString(FEEDBACK_PARAMETERS.SYSNAME_KEY, sysname);
		params.putString(FEEDBACK_PARAMETERS.SYSVER_KEY, sysver);
		params.putString(FEEDBACK_PARAMETERS.MODEL_KEY, model);
		params.putString(FEEDBACK_PARAMETERS.LMODEL_KEY, lmodel);
		params.putString(FEEDBACK_PARAMETERS.FEEDBACK_KEY, feedback);
		params.putString(FEEDBACK_PARAMETERS.SEX_KEY, sex);
		params.putString(FEEDBACK_PARAMETERS.AGEGROUP_KEY, agegroup);

		{// 加密字段
			ArrayList<String> list = new ArrayList<String>();
			list.add(FEEDBACK_PARAMETERS.DEVID_KEY);
			list.add(FEEDBACK_PARAMETERS.NAME_KEY);
			list.add(FEEDBACK_PARAMETERS.SYSNAME_KEY);
			list.add(FEEDBACK_PARAMETERS.SYSVER_KEY);
			list.add(FEEDBACK_PARAMETERS.MODEL_KEY);
			list.add(FEEDBACK_PARAMETERS.LMODEL_KEY);
			list.add(FEEDBACK_PARAMETERS.FEEDBACK_KEY);
			list.add(FEEDBACK_PARAMETERS.SEX_KEY);
			list.add(FEEDBACK_PARAMETERS.AGEGROUP_KEY);
			StringBuilder sb = new StringBuilder();
			for (String s : list) {
				sb.append(s);
				sb.append("=");
				sb.append(params.get(s));
				sb.append("&");
			}
			sb.append("letvmobile2013");
			params.putString(FEEDBACK_PARAMETERS.SIGN_KEY,
					MD5.toMd5(sb.toString()));
		}

		params.putString(PUBLIC_PARAMETERS.PCODE_KEY, PCODE);
		params.putString(PUBLIC_PARAMETERS.VERSION_KEY, VERSION);

		LetvHttpParameter<T, D> httpParameter = new LetvHttpParameter<T, D>(
				baseUrl, params, LetvHttpParameter.Type.POST, parser, updataId);
		return request(httpParameter);
	}

	/**
	 * 通过视频id获取视频或专辑详情
	 * */
	public static <T extends LetvBaseBean, D> LetvDataHull<T> requestGetAlbumById(
			int updataId, String id, LetvMainParser<T, D> parser) {
		String head = getStaticHead();
		String end = getStaticEnd();

		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair(PUBLIC_PARAMETERS.MOD_KEY,
				GETALBUMBYID_PARAMETERS.MOD_VALUE));
		params.add(new BasicNameValuePair(PUBLIC_PARAMETERS.CTL_KEY,
				GETALBUMBYID_PARAMETERS.CTL_VALUE));
		params.add(new BasicNameValuePair(PUBLIC_PARAMETERS.ACT_KEY,
				GETALBUMBYID_PARAMETERS.ACT_VALUE));
		params.add(new BasicNameValuePair(GETALBUMBYID_PARAMETERS.ID_KEY, id));
		params.add(new BasicNameValuePair(PUBLIC_PARAMETERS.PCODE_KEY, PCODE));
		params.add(new BasicNameValuePair(PUBLIC_PARAMETERS.VERSION_KEY,
				VERSION));
		LetvHttpStaticParameter<T, D> httpParameter = new LetvHttpStaticParameter<T, D>(
				head, end, params, parser, updataId);
		return request(httpParameter);
	}

	/**
	 * 请求开机广告图接口
	 * */
	public static <T extends LetvBaseBean, D> LetvDataHull<T> requestGetPushAd(
			int updataId, LetvBaseParser<T, D> parser) {

		LetvHttpParameter<T, D> httpParameter = new LetvHttpParameter<T, D>(
				WB_STATIC_PUSH_AD_IMAGE, null, LetvHttpParameter.Type.GET,
				parser, updataId);

		return request(httpParameter);
	}

	public static <T extends LetvBaseBean, D> LetvDataHull<T> requestShowWorldCup(
			int updataId, LetvBaseParser<T, D> parser) {
		String url = String.format(getWorldCupUrl(),
				LetvUtil.getClientVersionName(), LetvUtil.getPcode());
		LetvHttpParameter<T, D> httpParameter = new LetvHttpParameter<T, D>(
				url, null, LetvHttpParameter.Type.GET, parser, updataId);
		return request(httpParameter);
	}

}
