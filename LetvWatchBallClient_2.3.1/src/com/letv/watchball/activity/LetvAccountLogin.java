package com.letv.watchball.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.letv.datastatistics.DataStatistics;
import com.letv.http.bean.LetvDataHull;
import com.letv.watchball.LetvApplication;
import com.letv.watchball.R;
import com.letv.watchball.async.LetvHttpAsyncTask;
import com.letv.watchball.bean.MessageBean;
import com.letv.watchball.bean.User;
import com.letv.watchball.db.DBManager;
import com.letv.watchball.db.PreferencesManager;
import com.letv.watchball.http.api.LetvHttpApi;
import com.letv.watchball.parser.UserParser;
import com.letv.watchball.utils.LetvConstant.DialogMsgConstantId;
import com.letv.watchball.utils.LetvUtil;
import com.letv.watchball.utils.UIs;
import com.letv.watchball.view.EmailAutoCompleteTextView;

public class LetvAccountLogin extends PimBaseActivity implements View.OnClickListener {
	private EmailAutoCompleteTextView usernameText;
	private EditText userpasswordText;
	private Button loginBtn;
	private TextView registerText;
	private TextView findPsswordText;
	private CheckBox remberCheckBox;
	private static LetvAccountLogin instance;

	public static void launch(Activity context) {
		Intent intent = new Intent(context, LetvAccountLogin.class);
		context.startActivityForResult(intent, LoginMainActivity.LOGIN);
	}

	@Override
	public int getContentView() {
		return R.layout.letv_login;
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		instance = this;
		findView();
		init();
		setTitle(R.string.letv_account_login);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		instance = null;
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
	}

	@Override
	public void findView() {
		super.findView();
		usernameText = (EmailAutoCompleteTextView) findViewById(R.id.letv_account);
		userpasswordText = (EditText) findViewById(R.id.letv_account_password);
		loginBtn = (Button) findViewById(R.id.letv_login_btn);
		registerText = (TextView) findViewById(R.id.letv_account_registe_btn);
		findPsswordText = (TextView) findViewById(R.id.letv_account_forgetpass_btn);
		loginBtn.setOnClickListener(this);
		registerText.setOnClickListener(this);
		findPsswordText.setOnClickListener(this);
	}

	private void init() {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.letv_account_forgetpass_btn:// 找回密码
			FindLetvAccountPasswordActivity.launch(LetvAccountLogin.this);
			break;
		case R.id.letv_account_registe_btn:// 注册
			RegisterActivity.launch(LetvAccountLogin.this);
			break;

		case R.id.letv_login_btn:// 登陆
			logIn();
			UIs.hideSoftkeyboard(this);
			break;

		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		if (arg0 == LoginMainActivity.LOGIN && arg1 == LoginMainActivity.LOGIN_SUCCESS) {
			setResult(LoginMainActivity.LOGIN_SUCCESS);
			finish();
		}
	}

	/**
	 * 登陆letv
	 */
	private String username;
	private String password;

	private void logIn() {
		username = usernameText.getText().toString();
		password = userpasswordText.getText().toString();
		if (checkLogin()) {
			new RequestLoginTask(this, username, password).start();
		}
	}

	private boolean checkLogin() {
		if (TextUtils.isEmpty(username)) {
			UIs.callDialogMsgPositiveButton(this, DialogMsgConstantId.FIFTEEN_ZERO_ONE_CONSTANT,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							usernameText.requestFocus();
						}
					});
			return false;
		}
		if (TextUtils.isEmpty(password)) {
			UIs.callDialogMsgPositiveButton(this, DialogMsgConstantId.FIFTEEN_ELEVEN_CONSTANT,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							userpasswordText.requestFocus();
						}
					});
			return false;
		}
		return true;
	}

	private class RequestLoginTask extends LetvHttpAsyncTask<User> {
		private String username;
		private String password;
		private UserParser mUserParser;
		public RequestLoginTask(Context context, String username, String password) {
			super(context);
			this.username = username;
			this.password = password;
			showLoading();
		}

		@Override
		public LetvDataHull<User> doInBackground() {
			mUserParser = new UserParser();
			return LetvHttpApi.login(0, username, password, "mapp", "1", mUserParser);
		}

		@Override
		public void onPostExecute(int updateId, User result) {
			if ("1".equals(result.getStatus())) {
				PreferencesManager.getInstance().setLoginName(username);
				// PreferencesManager.getInstance().setLoginName(username);
				PreferencesManager.getInstance().setLoginPassword(password);
				PreferencesManager.getInstance().setUserId(result.getUid());
				PreferencesManager.getInstance().setUserName(result.getUsername());
				PreferencesManager.getInstance().setSso_tk(result.getTv_token());
				// PreferencesManager.getInstance().setRemember_pwd(rememberpwd_chk.isChecked());
//				UIs.showToast(R.string.toast_login_ok);
				setResult(LoginMainActivity.LOGIN_SUCCESS);
				LetvApplication.getInstance().setLogInTime(System.currentTimeMillis());
				//登录成功 上报统计 modified by zengsonghai 20131112
				DataStatistics.getInstance().sendLoginInfo(LetvAccountLogin.this, "0", "0", LetvUtil.getUID(), "-", "-", System.currentTimeMillis()/1000 +"", LetvUtil.getPcode(), 0);
				finish();
			}else {
				UIs.call(LetvAccountLogin.this, mUserParser.getMessage(), null);
			}
			hideErrorLayoutMessage();
		}

		@Override
		public void netNull() {
			MessageBean dialogMsgByMsg = DBManager.getInstance().getDialogMsgTrace()
					.getDialogMsgByMsgId(DialogMsgConstantId.THIRTEEN_ZERO_ONE_CONSTANT);
			hideErrorLayoutMessage();
			UIs.call(LetvAccountLogin.this, dialogMsgByMsg.message, null);
		}

		@Override
		public void netErr(int updateId, String errMsg) {
			// TODO Auto-generated method stub
			MessageBean dialogMsgByMsg = DBManager.getInstance().getDialogMsgTrace()
					.getDialogMsgByMsgId(DialogMsgConstantId.TWELVE_ZERO_ONE_CONSTANT);
			hideErrorLayoutMessage();
			UIs.call(LetvAccountLogin.this, dialogMsgByMsg.message, null);

		}

		@Override
		public void dataNull(int updateId, String errMsg) {
			UIs.call(LetvAccountLogin.this,errMsg, null);
			hideErrorLayoutMessage();
		}

	}

	public static void close() {
		if (instance != null) {
			instance.finish();
			instance = null;
		}
	}

}
