package com.letv.watchball.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.letv.watchball.LetvApplication;
import com.letv.watchball.utils.LetvConstant;

public class PreferencesManager {

	private static final String SETTINGS = "settings";
	private static final String PUSH = "push";
	private static final String API = "API";
	private static final String PERSONAL_CENTER_SP_NAME = "personal_center";
	private static final String USER_NICK_NAME = "nick_name";
	private static final String BR_CONTROL = "br_Control";
	private static final String SHARE = "share";
	private static final String DIALOG_MSG = "dialog_msg";

	private Context context;
      private boolean showAd;

      private PreferencesManager(Context context) {
		this.context = context;
	}

	private static PreferencesManager instance = new PreferencesManager(LetvApplication.getInstance());

	public static PreferencesManager getInstance() {
		return instance;
	}

	/**
	 * 获得 开赛提醒
	 * 
	 * @return
	 */
	public int getGameStartRemind() {

		SharedPreferences sp = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
		// 默认开赛提醒为 开赛前五分钟
		return sp.getInt("game_start_remind", 5);
	}

	/**
	 * 设置开赛提醒时间
	 * 
	 * @param remindAhead
	 *            提前5/10/30分钟，传入5、10、30，关闭提醒，传入 -1
	 */
	public void setGameStartRemind(int remindAhead) {

		SharedPreferences sp = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);

		sp.edit().putInt("game_start_remind", remindAhead).commit();

	}

	public boolean isGameResultRemind() {

		SharedPreferences sp = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);

		return sp.getBoolean("game_end_remind_open", true);
	}

	public void setGameResultRemind(boolean open) {

		SharedPreferences sp = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);

		sp.edit().putBoolean("game_end_remind_open", open).commit();

	}
	public boolean isSleepRemind() {

		SharedPreferences sp = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);

		return sp.getBoolean("sleep_remind_open", true);
	}

	public void setSleepRemind(boolean open) {

		SharedPreferences sp = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);

		sp.edit().putBoolean("sleep_remind_open", open).commit();

	}
	
	//是否开启免推送模式
	public boolean isPushservice() {
		SharedPreferences sp = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
		return sp.getBoolean("pushservice_open", true);
	}
	public void setPushservice(boolean open) {

		SharedPreferences sp = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);

		sp.edit().putBoolean("pushservice_open", open).commit();

	}
	
	
	public String getGameId() {

		SharedPreferences sp = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);

		return sp.getString("GameId", "");
	}

	public void setGameId(String gameId) {

		SharedPreferences sp = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);

		sp.edit().putString("GameId", gameId).commit();

	}

	/**
	 * 是否已经获取预约列表
	 * 
	 * @return
	 */
	public boolean isUpdateSubscribeGame() {
		SharedPreferences sp = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
		return sp.getBoolean("isUpdateSubscribeGame", true);
	}

	/**
	 * 设置已经获取预约列表
	 * 
	 * @return
	 */
	public void setIsUpdateSubscribeGame(boolean isUpdateSubscribeGame) {
		SharedPreferences sp = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
		sp.edit().putBoolean("isUpdateSubscribeGame", isUpdateSubscribeGame).commit();
	}

	public void setIsNeedUpdate(boolean isNeedUpdate) {

		SharedPreferences sp = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);

		sp.edit().putBoolean("isNeedUpdate", isNeedUpdate).commit();
	}

	public boolean isNeedUpdate() {

		SharedPreferences sp = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);

		boolean isNeedUpdate = sp.getBoolean("isNeedUpdate", false);

		return isNeedUpdate;
	}

	/**
	 * 是否使用测试接口
	 * */
	public boolean isTestApi() {
//		 return true ;
		SharedPreferences sp = context.getSharedPreferences(API, Context.MODE_PRIVATE);
		return sp.getBoolean("test", false);
	}

	/**
	 * 设置是否使用测试接口
	 * */
	public void setTestApi(boolean isTest) {
		SharedPreferences sp = context.getSharedPreferences(API, Context.MODE_PRIVATE);
		sp.edit().putBoolean("test", isTest).commit();
	}

	public void setFirstPlay(boolean isFirstEnter) {

		SharedPreferences sp = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);

		sp.edit().putBoolean("firstPlay", isFirstEnter).commit();
	}

	public boolean isFirstPlay() {

		SharedPreferences sp = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);

		boolean isFirstEnter = sp.getBoolean("firstPlay", true);

		return isFirstEnter;
	}

	public boolean isPlayHd() {

		SharedPreferences sp = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);

		boolean isPlayHd = sp.getBoolean("isPlayHd", false);

		return isPlayHd;

	}

	public void setIsPlayHd(boolean isPlayHd) {

		SharedPreferences sp = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);

		sp.edit().putBoolean("isPlayHd", isPlayHd).commit();
	}

	public boolean isAllowMobileNetwork() {

		SharedPreferences sp = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);

		boolean isAllow = sp.getBoolean("isAllow", true);

		return isAllow;
	}

	/**
	 * 是否跳过片头片尾
	 * 
	 * @return
	 */
	public boolean isSkip() {

		SharedPreferences sp = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);

		boolean isSkip = sp.getBoolean("isSkip", true);

		return isSkip;
	}

	public long getPushTime() {
		SharedPreferences sp = context.getSharedPreferences(PUSH, Context.MODE_PRIVATE);

		long time = sp.getLong("time", 0);

		return time;
	}

	public void savePushDistance(int time) {
		SharedPreferences sp = context.getSharedPreferences(PUSH, Context.MODE_PRIVATE);

		sp.edit().putInt("distance", time).commit();
	}

	public int getPushDistance() {
		SharedPreferences sp = context.getSharedPreferences(PUSH, Context.MODE_PRIVATE);

		int time = sp.getInt("distance", 6);

		return time;
	}

	public void savePushTime(long time) {
		SharedPreferences sp = context.getSharedPreferences(PUSH, Context.MODE_PRIVATE);

		sp.edit().putLong("time", time).commit();
	}

	public long getPushId() {
		SharedPreferences sp = context.getSharedPreferences(PUSH, Context.MODE_PRIVATE);

		long id = sp.getLong("id", 0);

		return id;
	}

	public void savePushId(long id) {
		SharedPreferences sp = context.getSharedPreferences(PUSH, Context.MODE_PRIVATE);

		sp.edit().putLong("id", id).commit();
	}

	/**
	 * 设置亮度
	 * */
	public void setPlayBrightness(float brightness) {
		SharedPreferences sp = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
		sp.edit().putFloat("Brightness", brightness).commit();
	}

	/**
	 * 得到亮度
	 * */
	public float getPlayBrightness() {
		SharedPreferences sp = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
		return sp.getFloat("Brightness", 0.5f);
	}
	
	/**
	 * 设置用户名
	 * */
	public void setUserName(String username) {
		SharedPreferences sp = context.getSharedPreferences(PERSONAL_CENTER_SP_NAME, Context.MODE_PRIVATE);
		sp.edit().putString("username", username).commit();
	}

	/**
	 * 得到用户名
	 * */
	public String getUserName() {
		SharedPreferences sp = context.getSharedPreferences(PERSONAL_CENTER_SP_NAME, Context.MODE_PRIVATE);
		return sp.getString("username", "");
	}
	/**
	 * 得到用户昵称
	 * */
	public String getNickUserName() {
		SharedPreferences sp = context.getSharedPreferences(USER_NICK_NAME, Context.MODE_PRIVATE);
		return sp.getString("nickusername", "");
	}
	
	/**
	 * 设置用户昵称
	 * */
	public void setNickUserName(String nickusername) {
		SharedPreferences sp = context.getSharedPreferences(USER_NICK_NAME, Context.MODE_PRIVATE);
		sp.edit().putString("nickusername", nickusername).commit();
	}
	/**
	 * 设置帐号
	 * */
	public void setLoginName(String username) {
		SharedPreferences sp = context.getSharedPreferences(PERSONAL_CENTER_SP_NAME, Context.MODE_PRIVATE);
		sp.edit().putString("loginname", username).commit();
	}

	/**
	 * 得到帐号
	 * */
	public String getLoginName() {
		SharedPreferences sp = context.getSharedPreferences(PERSONAL_CENTER_SP_NAME, Context.MODE_PRIVATE);
		return sp.getString("loginname", "");
	}

	/**
	 * 设置登录密码
	 * */
	public void setLoginPassword(String password) {
		SharedPreferences sp = context.getSharedPreferences(PERSONAL_CENTER_SP_NAME, Context.MODE_PRIVATE);
		sp.edit().putString("loginpassword", password).commit();
	}

	/**
	 * 得到登录密码
	 * */
	public String getLoginPassword() {
		SharedPreferences sp = context.getSharedPreferences(PERSONAL_CENTER_SP_NAME, Context.MODE_PRIVATE);
		return sp.getString("loginpassword", "");
	}

	/**
	 * 设置UserID
	 * */
	public void setUserId(String userId) {
		SharedPreferences sp = context.getSharedPreferences(PERSONAL_CENTER_SP_NAME, Context.MODE_PRIVATE);
		sp.edit().putString("userId", userId).commit();
	}

	/**
	 * 得到UserId
	 * */
	public String getUserId() {
		SharedPreferences sp = context.getSharedPreferences(PERSONAL_CENTER_SP_NAME, Context.MODE_PRIVATE);
		return sp.getString("userId", "");
	}

	/**
	 * 设置SSO_TOKEN
	 * */
	public void setSso_tk(String sso_tk) {
		SharedPreferences sp = context.getSharedPreferences(PERSONAL_CENTER_SP_NAME, Context.MODE_PRIVATE);
		sp.edit().putString("sso_tk", sso_tk).commit();
	}

	/**
	 * 得到SSO_TOKEN
	 * */
	public String getSso_tk() {
		SharedPreferences sp = context.getSharedPreferences(PERSONAL_CENTER_SP_NAME, Context.MODE_PRIVATE);
		return sp.getString("sso_tk", "");
	}
	public void setRemember_pwd(boolean isRemember_pwd) {
		SharedPreferences sp = context.getSharedPreferences(PERSONAL_CENTER_SP_NAME, Context.MODE_PRIVATE);
		sp.edit().putBoolean("isRemember_pwd", isRemember_pwd).commit();
	}

	public boolean isRemember_pwd() {
		SharedPreferences sp = context.getSharedPreferences(PERSONAL_CENTER_SP_NAME, Context.MODE_PRIVATE);
		return sp.getBoolean("isRemember_pwd", false);
	}

	public boolean isLogin() {
		// return isRemember_pwd() && !TextUtils.isEmpty(getUserName()) &&
		// !TextUtils.isEmpty(getUserId()) ;
		return !TextUtils.isEmpty(getUserName()) && !TextUtils.isEmpty(getUserId());
	}

	public void logoutUser() {
		SharedPreferences sp = context.getSharedPreferences(PERSONAL_CENTER_SP_NAME, Context.MODE_PRIVATE);
		sp.edit().clear().commit();
	}

	

	/**
	 * 设置播放350码流名称
	 * */
	public void setPlayLow_zh(String name) {
		SharedPreferences sp = context.getSharedPreferences(BR_CONTROL, Context.MODE_PRIVATE);
		sp.edit().putString("low_play_zh", name).commit();
	}

	/**
	 * 得到下载350码流名称
	 * */
	public String getPlayLow_zh() {
		String dn = "流畅";
		SharedPreferences sp = context.getSharedPreferences(BR_CONTROL, Context.MODE_PRIVATE);
		String name = sp.getString("low_play_zh", dn);
		if (TextUtils.isEmpty(name)) {
			name = dn;
		}
		return name;
	}

	/**
	 * 设置下载1000码流名称
	 * */
	public void setPlayNormal_zh(String name) {
		SharedPreferences sp = context.getSharedPreferences(BR_CONTROL, Context.MODE_PRIVATE);
		sp.edit().putString("normal_play_zh", name).commit();
	}

	/**
	 * 得到下载1000码流名称
	 * */
	public String getPlayNormal_zh() {
		String dn = "高清";
		SharedPreferences sp = context.getSharedPreferences(BR_CONTROL, Context.MODE_PRIVATE);
		String name = sp.getString("normal_play_zh", "高清");
		if (TextUtils.isEmpty(name)) {
			name = dn;
		}
		return name;
	}

	/**
	 * 设置下载1300码流名称
	 * */
	public void setPlayHigh_zh(String name) {
		SharedPreferences sp = context.getSharedPreferences(BR_CONTROL, Context.MODE_PRIVATE);
		sp.edit().putString("high_play_zh", name).commit();
	}

	/**
	 * 得到下载1300码流名称
	 * */
	public String getPlayHigh_zh() {
		String dn = "超清";
		SharedPreferences sp = context.getSharedPreferences(BR_CONTROL, Context.MODE_PRIVATE);
		String name = sp.getString("high_play_zh", dn);
		if (TextUtils.isEmpty(name)) {
			name = dn;
		}
		return name;
	}
	
	
	public boolean sinaIsShare() {
		SharedPreferences sp = context.getSharedPreferences(SHARE, Context.MODE_PRIVATE);
		return sp.getBoolean("sinaIsShare", true);
	}

	public boolean tencentIsShare() {
		SharedPreferences sp = context.getSharedPreferences(SHARE, Context.MODE_PRIVATE);
		return sp.getBoolean("tencentIsShare", true);
	}

	public boolean qzoneIsShare() {
		SharedPreferences sp = context.getSharedPreferences(SHARE, Context.MODE_PRIVATE);
		return sp.getBoolean("qzoneIsShare", true);
	}

	public boolean renrenIsShare() {
		SharedPreferences sp = context.getSharedPreferences(SHARE, Context.MODE_PRIVATE);
		return sp.getBoolean("renrenIsShare", true);
	}

	public boolean lestarIsShare() {
		SharedPreferences sp = context.getSharedPreferences(SHARE, Context.MODE_PRIVATE);
		return sp.getBoolean("lestarIsShare", true);
	}

	public void setSinaIsShare(boolean isShare) {
		SharedPreferences sp = context.getSharedPreferences(SHARE, Context.MODE_PRIVATE);
		sp.edit().putBoolean("sinaIsShare", isShare).commit();
	}

	public void setTencentIsShare(boolean isShare) {
		SharedPreferences sp = context.getSharedPreferences(SHARE, Context.MODE_PRIVATE);
		sp.edit().putBoolean("tencentIsShare", isShare).commit();
	}

	public void setQzoneIsShare(boolean isShare) {
		SharedPreferences sp = context.getSharedPreferences(SHARE, Context.MODE_PRIVATE);
		sp.edit().putBoolean("qzoneIsShare", isShare).commit();
	}

	public void setRenrenIsShare(boolean isShare) {
		SharedPreferences sp = context.getSharedPreferences(SHARE, Context.MODE_PRIVATE);
		sp.edit().putBoolean("renrenIsShare", isShare).commit();
	}

	public void setLestarIsShare(boolean isShare) {
		SharedPreferences sp = context.getSharedPreferences(SHARE, Context.MODE_PRIVATE);
		sp.edit().putBoolean("lestarIsShare", isShare).commit();
	}
	
	/**
	 * 保存客户端dialog数据服务端化Markid
	 * */
	public void saveDialogMsgMarkid(String markid) {
		SharedPreferences sp = context.getSharedPreferences(DIALOG_MSG, Context.MODE_PRIVATE);
		sp.edit().putString("dialogMsgMarkid", markid).commit();
	}

	/**
	 * 得到客户端dialog数据服务端化Markid
	 * */
	public String getDialogMsgMarkid() {
		SharedPreferences sp = context.getSharedPreferences(DIALOG_MSG, Context.MODE_PRIVATE);
		return sp.getString("dialogMsgMarkid", null);
	}

	public void saveDialogMsgInfo(String info) {
		SharedPreferences sp = context.getSharedPreferences(DIALOG_MSG, Context.MODE_PRIVATE);
		sp.edit().putString("dialogMsgInfo", info).commit();
	}

	public String getDialogMsgInfo() {
		SharedPreferences sp = context.getSharedPreferences(DIALOG_MSG, Context.MODE_PRIVATE);
		return sp.getString("dialogMsgInfo", null);
	}

	public void saveDialogMsgIsSuc(boolean isSuc) {
		SharedPreferences sp = context.getSharedPreferences(DIALOG_MSG, Context.MODE_PRIVATE);
		sp.edit().putBoolean("dialogMsgInit", isSuc).commit();
	}

	public boolean getDialogMsgIsSuc() {
		SharedPreferences sp = context.getSharedPreferences(DIALOG_MSG, Context.MODE_PRIVATE);
		return sp.getBoolean("dialogMsgInit", false);
	}
	

	public void notShowNewFeaturesDialog() {

		SharedPreferences sp = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);

		sp.edit().putInt("isShowNewFeatures", LetvConstant.Global.VERSION_CODE).commit();
	}

	public boolean isShowNewFeaturesDialog() {

		SharedPreferences sp = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);

		int shortcut = sp.getInt("isShowNewFeatures", -1);

		if (shortcut >= LetvConstant.Global.VERSION_CODE) {
			return false;
		}

		return true;
	}





      public String getLiveNtificationGameId() {
            SharedPreferences sp = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);

            String notificationGameId = sp.getString("notificationGameId", "0");

            return notificationGameId;
      }

      public void setLiveNtificationGameId(String liveNtificationGameId) {

            SharedPreferences sp = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);

            sp.edit().putString("notificationGameId",liveNtificationGameId).commit();

      }


      public void setShowWorldCup(boolean showWorldCup) {
            SharedPreferences sp = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);

            sp.edit().putBoolean("showworldcup",showWorldCup).commit();
      }

      public boolean isShowWorldCup() {
            SharedPreferences sp = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);

            boolean isShowWorldCup = sp.getBoolean("showworldcup", false);

            return isShowWorldCup;
      }

      private static final String HOME_PAGE = "home_page";

      /**
       * Utp运营信息
       * */
      public void setUtp(boolean status) {
            SharedPreferences sp = context.getSharedPreferences(HOME_PAGE, Context.MODE_PRIVATE);
            sp.edit().putBoolean("utp", status).commit();
      }

      /**
       * 得到Utp运营信息
       * */
      public boolean getUtp() {

            SharedPreferences sp = context.getSharedPreferences(HOME_PAGE, Context.MODE_PRIVATE);
            return sp.getBoolean("utp", false);
//    	  return false;
      }

      /**
       * 是否第一次登陆
       * @param isFirstEnter
       */

      public void setFirstEnter(boolean isFirstEnter) {

            SharedPreferences sp = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);

            sp.edit().putBoolean("firstEnter", isFirstEnter).commit();
      }

      public boolean isFirstEnter() {

            SharedPreferences sp = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);

            boolean isFirstEnter = sp.getBoolean("firstEnter", true);

            return isFirstEnter;
      }

      public boolean isShowAd() {
            SharedPreferences sp = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);

            boolean isFirstEnter = sp.getBoolean("showAd", true);

            return isFirstEnter;
      }

      public void setShowAd(boolean showAd) {
            SharedPreferences sp = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);

            sp.edit().putBoolean("showAd", showAd).commit();
      }
}
