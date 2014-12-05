package com.letv.watchball.parser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.letv.http.parse.LetvMainParser;
import com.letv.watchball.bean.User;
import com.letv.watchball.bean.User.VipInfo;

/**
 * 用户信息解析
 * */
public class UserParser extends LetvMainParser<User, JSONObject> {

	/**
	 * 接口返回数据节点
	 * */
	protected final String ERRORCODE = "errorCode";

	protected final String MESSAGE = "message";

	protected final String BEAN = "bean";

	private final String UID = "uid";

	private final String USERNAME = "username";

	private final String STATUS = "status";

	private final String GENDER = "gender";

	private final String QQ = "qq";

	private final String REGISTIP = "registIp";

	private final String REGISTTIME = "registTime";

	private final String LASTMODIFYTIME = "lastModifyTime";

	private final String BIRTHDAY = "birthday";

	private final String NICKNAME = "nickname";

	private final String MSN = "msn";

	private final String REGISTSERVICE = "registService";

	private final String EMAIL = "email";

	private final String MOBILE = "mobile";

	private final String PROVINCE = "province";

	private final String CITY = "city";

	private final String POSTCODE = "postCode";

	private final String ADDRESS = "address";

	private final String MAC = "mac";

	private final String PICTURE = "picture";

	private final String NAME = "name";

	private final String CONTACTEMAIL = "contactEmail";

	private final String DELIVERY = "delivery";

	private final String POINT = "point";

	private final String LEVEL_ID = "level_id";

	private final String ISVIP = "isvip";

	private final String EDUCATION = "education";

	private final String INDUSTRY = "industry";

	private final String JOB = "job";

	private final String INCOME = "income";

	private final String LASTLOGINTIME = "lastLoginTime";

	private final String LASTLOGINIP = "lastLoginIp";

	private final String VIPINFO = "vipinfo";

	private final String TV_TOKEN = "tv_token";

	private String tv_token;

	@Override
	public User parse(JSONObject data) throws JSONException {

		User letvUser = new User();

		letvUser.setUid(getString(data, UID));
		letvUser.setUsername(getString(data, USERNAME));
		letvUser.setStatus(getString(data, STATUS));
		letvUser.setGender(getString(data, GENDER));
		letvUser.setQq(getString(data, QQ));
		letvUser.setRegistIp(getString(data, REGISTIP));
		letvUser.setRegistTime(getString(data, REGISTTIME));
		letvUser.setLastModifyTime(getString(data, LASTMODIFYTIME));
		letvUser.setBirthday(getString(data, BIRTHDAY));
		letvUser.setNickname(getString(data, NICKNAME));
		letvUser.setMsn(getString(data, MSN));
		letvUser.setRegistService(getString(data, REGISTSERVICE));
		letvUser.setEmail(getString(data, EMAIL));
		letvUser.setMobile(getString(data, MOBILE));
		letvUser.setProvince(getString(data, PROVINCE));
		letvUser.setCity(getString(data, CITY));
		letvUser.setPostCode(getString(data, POSTCODE));
		letvUser.setAddress(getString(data, ADDRESS));
		letvUser.setMac(getString(data, MAC));
		letvUser.setName(getString(data, NAME));
		letvUser.setContactEmail(getString(data, CONTACTEMAIL));
		letvUser.setDelivery(getString(data, DELIVERY));
		letvUser.setPoint(getString(data, POINT));
		letvUser.setLevel_id(getString(data, LEVEL_ID));
		letvUser.setIsvip(getString(data, ISVIP));
		letvUser.setEducation(getString(data, EDUCATION));
		letvUser.setIndustry(getString(data, INDUSTRY));
		letvUser.setJob(getString(data, JOB));
		letvUser.setIncome(getString(data, INCOME));
		letvUser.setLastLoginTime(getString(data, LASTLOGINTIME));
		letvUser.setLastLoginIp(getString(data, LASTLOGINIP));
		if (!TextUtils.isEmpty(tv_token)) {
			letvUser.setTv_token(tv_token);
		}
		if (has(data, VIPINFO)) {
			JSONArray array = getJSONArray(data, VIPINFO);
			if (array != null && array.length() > 0) {
				JSONObject object = getJSONObject(array, 0);

				VipInfo info = new VipInfo();
				info.setId(getString(object, "id"));
				info.setUsername(getString(object, "username"));
				info.setCanceltime(getLong(object, "canceltime"));
				info.setOrderFrom(getInt(object, "orderFrom"));
				info.setProductid(getInt(object, "productid"));
				info.setVipType(getInt(object, "vipType"));

				letvUser.setVipInfo(info);
			}
		}

		// if (User.ISVIP_YES.equals(letvUser.getIsvip())) {
		// PreferencesManager.getInstance().setVip(true);
		// if (letvUser.getVipInfo() != null) {
		// PreferencesManager.getInstance().setVipCancelTime(letvUser.getVipInfo().getCanceltime());
		// PreferencesManager.getInstance().setVipLevel(letvUser.getVipInfo().getVipType());
		// }
		// } else {
		// PreferencesManager.getInstance().setVip(false);
		// PreferencesManager.getInstance().setVipCancelTime(0);
		// PreferencesManager.getInstance().setVipLevel(0);
		// }

		/**
		 * 用户头像url解析，所有分辨率的头像，通过string拼接返回 各分辨率之间“，”隔开，目前使用的头像分辨率：200x200
		 */
		String pictures = getString(data, PICTURE);
		if (!TextUtils.isEmpty(pictures)) {
			String[] splits = pictures.split(",");
			letvUser.setPicture(splits[1]);
		}

		return letvUser;
	}

	@Override
	protected boolean canParse(String data) {
		try {
			JSONObject object = new JSONObject(data);
			if (!object.has(STATUS)) {
				return false;
			}
			int status = object.getInt(STATUS);
			int errorCode = object.optInt(ERRORCODE);
			String message = object.optString(MESSAGE);

			setMessage(message);
			if (status == 1 && errorCode == 0) {
				if (has(object, TV_TOKEN)) {
					tv_token = getString(object, TV_TOKEN);
				}
				return true;
			} else {
				setErrorMsg(errorCode);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	protected JSONObject getData(String data) throws Exception {
		JSONObject object = new JSONObject(data);
		object = getJSONObject(object, BEAN);
		return object;
	}

	/**
	 * 得到错误信息
	 * */
	// private int createErrorMsg(int code) {
	// int errString ;
	// switch (code) {
	// case 403:
	// errString = R.string.err_403 ;
	// break;
	// case 500:
	// errString = R.string.err_500 ;
	// break;
	// case 1000:
	// errString = R.string.err_1000 ;
	// break;
	// case 1001:
	// errString = R.string.err_1001 ;
	// break;
	// case 1002:
	// errString = R.string.err_1002 ;
	// break;
	// case 1003:
	// errString = R.string.err_1003 ;
	// break;
	// case 1004:
	// errString = R.string.err_1004 ;
	// break;
	// case 1005:
	// errString = R.string.err_1005 ;
	// break;
	// case 1006:
	// errString = R.string.err_1006 ;
	// break;
	// case 1007:
	// errString = R.string.err_1007 ;
	// break;
	// case 1008:
	// errString = R.string.err_1008 ;
	// break;
	// case 1009:
	// errString = R.string.err_1009 ;
	// break;
	// case 1010:
	// errString = R.string.err_1010 ;
	// break;
	// case 1011:
	// errString = R.string.err_1011 ;
	// break;
	// case 1012:
	// errString = R.string.err_1012 ;
	// break;
	// case 1013:
	// errString = R.string.err_1013 ;
	// break;
	// case 1014:
	// errString = R.string.err_1014 ;
	// break;
	// case 1015:
	// errString = R.string.err_1015 ;
	// break;
	// case 1016:
	// errString = R.string.err_1016 ;
	// break;
	// default:
	// errString = R.string.err_default ;
	// break;
	// }
	// return errString ;
	// }
}