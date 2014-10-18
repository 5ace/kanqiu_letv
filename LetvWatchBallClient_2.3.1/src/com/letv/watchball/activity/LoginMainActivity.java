package com.letv.watchball.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.letv.watchball.R;
import com.letv.watchball.http.api.LetvHttpApi;
import com.letv.watchball.utils.LogInfo;

public class LoginMainActivity extends PimBaseActivity implements View.OnClickListener {
	public static final String FROM_OTHER = "0";
	public static final String FROM_MORE = "1";
	private int src = 0;

	/**
	 * 登录成功
	 * */
	public static final int LOGIN_SUCCESS = 1;

	/**
	 * 登录失败
	 * */
	public static final int LOGIN_FAILURE = 0;

	/**
	 * 登录成功
	 * */
	public static final int LOGIN_SUCCESS_FOR_TS = 2;

	/**
	 * 登录行为的requestCode
	 * */
	public static final int LOGIN = 0x10;

	public static final int FORPLAY = 1;

	public static final int FORTS = 2;

	
	/**
	 * 右侧圆形icon登录
	 * */
	public static void launch(Fragment context) {
		Intent intent = new Intent(context.getActivity(), LoginMainActivity.class);
		context.startActivityForResult(intent, LOGIN);
	}
	
	/**
	 * 评论登录入口
	 * @param context
	 */
	public static void launch(Activity context) {
		Intent intent = new Intent(context, LoginMainActivity.class);
		context.startActivityForResult(intent, LOGIN);
	}

	@Override
	public int getContentView() {
		return R.layout.login_main_layout;
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		// instance = this;

		findView();
		setTitle(R.string.login_title);
		findViewById(R.id.login_qq).setOnClickListener(this);
		findViewById(R.id.login_letv).setOnClickListener(this);
		findViewById(R.id.login_weibo).setOnClickListener(this);
		findViewById(R.id.login_main_findpsw).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login_qq:
			LetvOpenIDOAuthLoginActivity.launch(this, LetvHttpApi.getQQLoginUrl(), getResources().getString(R.string.login_qq), src);
			break;
		case R.id.login_letv:
			LetvAccountLogin.launch(LoginMainActivity.this);
			break;
		case R.id.login_weibo:
			LetvOpenIDOAuthLoginActivity.launch(this, LetvHttpApi.getSinaLoginUrl(), getResources().getString(R.string.login_weibo), src);
			break;

		case R.id.login_main_findpsw:
			RegisterActivity.launch(LoginMainActivity.this);
			break;

		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		LogInfo.log("lhz", "LoginMainActivity.onActivityResult(),requestCode:"+arg0);
		if (arg0 == LOGIN && arg1 == LOGIN_SUCCESS) {
			setResult(LOGIN_SUCCESS);
			finish();
		}
	}

}
