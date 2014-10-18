package com.letv.watchball.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.letv.http.bean.LetvDataHull;
import com.letv.watchball.R;
import com.letv.watchball.async.LetvAsyncTask;
import com.letv.watchball.async.LetvHttpAsyncTask;
import com.letv.watchball.bean.RegisterResult;
import com.letv.watchball.bean.S_SendMobileResult;
import com.letv.watchball.bean.User;
import com.letv.watchball.db.PreferencesManager;
import com.letv.watchball.http.api.LetvHttpApi;
import com.letv.watchball.parser.RegisterResultParser;
import com.letv.watchball.parser.S_SendMobileResultParser;
import com.letv.watchball.parser.UserParser;
import com.letv.watchball.utils.LetvConstant;
import com.letv.watchball.utils.LetvConstant.DialogMsgConstantId;
import com.letv.watchball.utils.LetvUtil;
import com.letv.watchball.utils.UIs;
import com.letv.watchball.view.DeleteButtonEditText;
import com.letv.watchball.view.EmailAutoCompleteTextView;

public class RegisterActivity extends LetvBaseActivity implements View.OnClickListener {
	private ImageView backBtn;
	private Button phone_regist_btn, email_regist_btn;
	private LinearLayout phoneRegistLayout, emailRegistLayout;
	private ViewFlipper regist_bodyFlipper;
	private boolean isAgreeProtol = true;
	private boolean isAgreeProtol2 = true;
	private ImageView agreeView;
	private ImageView agreeView2;
	/**
	 * 手机注册 view
	 * 
	 * @param context
	 */
	private DeleteButtonEditText phoneNumView;
	private DeleteButtonEditText authCodeView;
	private Button getAuthCodeBtn;
	private DeleteButtonEditText psdView;
	private DeleteButtonEditText psdViewConfirm;
	private TextView agreeViewPhone;
	private Button phoneRegisterBtn;
	/**
	 * 邮箱注册View
	 * 
	 * @param context
	 */
	private EmailAutoCompleteTextView emailView;
	private DeleteButtonEditText psdViewEmail;
	private DeleteButtonEditText psdViewConfirmEmail;
	private Button emailRegisterBtn;
	private TextView agreeViewEmail;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {

		};
	};

	public static void launch(Activity context) {
		Intent intent = new Intent(context, RegisterActivity.class);
		context.startActivityForResult(intent, LoginMainActivity.LOGIN);
//		context.finish();
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.personal_center_activity_regiest);
		findView();
	}

	private void findView() {
		regist_bodyFlipper = (ViewFlipper) findViewById(R.id.regist_bodyViewFlipper);
		phoneRegistLayout = (LinearLayout) findViewById(R.id.regist_bodyPhone);
		emailRegistLayout = (LinearLayout) findViewById(R.id.regist_bodyMail);
		backBtn = (ImageView) findViewById(R.id.back_btn);
		agreeView = (ImageView) findViewById(R.id.regist_agreeCheckBtn);
		agreeView2 = (ImageView) findViewById(R.id.regist_agreeCheckBtn2);
		agreeView2.setOnClickListener(this);
		backBtn.setOnClickListener(this);
		agreeView.setOnClickListener(this);
		phone_regist_btn = (Button) findViewById(R.id.my_top_title1_btn_left);
		email_regist_btn = (Button) findViewById(R.id.my_top_title1_btn_right);
		phone_regist_btn.setOnClickListener(this);
		email_regist_btn.setOnClickListener(this);
		phone_regist_btn.setText(R.string.phone_register);
		email_regist_btn.setText(R.string.email_register);
		phone_regist_btn.setSelected(true);
		email_regist_btn.setSelected(false);
		/**
		 * phone
		 */
		phoneNumView = (DeleteButtonEditText) findViewById(R.id.regist_phone_edit);
		authCodeView = (DeleteButtonEditText) findViewById(R.id.regist_phoneCheckNum_edit);
		getAuthCodeBtn = (Button) findViewById(R.id.regist_getAuthCode);
		psdView = (DeleteButtonEditText) findViewById(R.id.regist_password_edit);
		psdViewConfirm = (DeleteButtonEditText) findViewById(R.id.regist_password_confirm_edit);
		agreeViewPhone = (TextView) findViewById(R.id.regist_protocol_txt_phone);
		phoneRegisterBtn = (Button) findViewById(R.id.regist_btnLogin_phone);
		getAuthCodeBtn.setOnClickListener(this);
		agreeViewPhone.setOnClickListener(this);
		phoneRegisterBtn.setOnClickListener(this);

		/**
		 * email
		 */
		emailView = (EmailAutoCompleteTextView) findViewById(R.id.regist_mail_edit);
		psdViewEmail = (DeleteButtonEditText) findViewById(R.id.regist_mail_password_edit);
		psdViewConfirmEmail = (DeleteButtonEditText) findViewById(R.id.regist_mail_password_confirm_edit);
		emailRegisterBtn = (Button) findViewById(R.id.regist_btnLogin_email);
		agreeViewEmail = (TextView) findViewById(R.id.regist_protocol_txt_email);
		emailRegisterBtn.setOnClickListener(this);
		agreeViewEmail.setOnClickListener(this);
		regist_bodyFlipper.setDisplayedChild(0);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_btn:
			finish();
			break;
		case R.id.my_top_title1_btn_left:
			phone_regist_btn.setBackgroundResource(R.drawable.recommend_left_select);
			email_regist_btn.setBackgroundResource(R.drawable.recommend_right_normal);
			phone_regist_btn.setTextColor(getResources().getColor(R.color.letv_color_ffffffff));
			email_regist_btn.setTextColor(getResources().getColor(R.color.letv_color_ff00a0e9));
			// phone_regist_btn.setSelected(true);
			// email_regist_btn.setSelected(false);
			regist_bodyFlipper.setDisplayedChild(0);
			break;
		case R.id.my_top_title1_btn_right:
			email_regist_btn.setTextColor(getResources().getColor(R.color.letv_color_ffffffff));
			phone_regist_btn.setTextColor(getResources().getColor(R.color.letv_color_ff00a0e9));
			phone_regist_btn.setBackgroundResource(R.drawable.recommend_left_normal);
			email_regist_btn.setBackgroundResource(R.drawable.recommend_right_select);
			// phone_regist_btn.setSelected(false);
			// email_regist_btn.setSelected(true);
			regist_bodyFlipper.setDisplayedChild(1);
			break;
		case R.id.regist_getAuthCode:// 获取验证码
			doGetAuthCode();
			break;
		case R.id.regist_protocol_txt_phone:// 乐视协议
			gotoLetvProtocol();
			break;
		case R.id.regist_btnLogin_phone:// 手机登陆btn
			if (!isAgreeProtol) {
				UIs.showToast(R.string.agree_letv_protol);
				return;
			}
			doRegister();
			break;
		case R.id.regist_btnLogin_email:// 邮箱注册登陆
			if (!isAgreeProtol2) {
				UIs.showToast(R.string.agree_letv_protol);
				return;
			}
			doRegister();
			break;
		case R.id.regist_protocol_txt_email:// 乐视协议
			gotoLetvProtocol();
			break;

		case R.id.regist_agreeCheckBtn:
			if (isAgreeProtol) {
				isAgreeProtol = false;
				agreeView.setImageResource(R.drawable.protol_unselect_btn);
			} else {
				agreeView.setImageResource(R.drawable.setting_select_sure);
				isAgreeProtol = true;
			}
			break;
		case R.id.regist_agreeCheckBtn2:
			if (isAgreeProtol2) {
				isAgreeProtol2 = false;
				agreeView2.setImageResource(R.drawable.protol_unselect_btn);
			} else {
				isAgreeProtol2 = true;
				agreeView2.setImageResource(R.drawable.setting_select_sure);
			}
			break;
		default:
			break;
		}
	}

	private void gotoLetvProtocol() {
		LetvWebViewActivity.launch(this, LetvConstant.USER_PROTOCOL_URL,
				getResources().getString(R.string.letv_protol_name));
	}

	/**
	 * 获取验证码
	 */
	private void doGetAuthCode() {
		if (TextUtils.isEmpty(phoneNumView.getText().toString())) {
			UIs.callDialogMsgPositiveButton(RegisterActivity.this,
					DialogMsgConstantId.FOURTEEN_ZERO_ONE_CONSTANT,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							phoneNumView.requestFocus();
						}
					});
			return;
		}
		if (!LetvUtil.isMobileNO(phoneNumView.getText().toString())) {
			UIs.callDialogMsgPositiveButton(RegisterActivity.this,
					DialogMsgConstantId.FOURTEEN_ZERO_THREE_CONSTANT,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							phoneNumView.requestFocus();
						}
					});
			return;
		}
		if (getAuthCodeBtn.isEnabled()) {
			startTimeTimer();
			UIs.notifyLong(this, R.string.registeractivity_send);
		}
	}

	private void doRegister() {
		String email = null;
		String mobile = null;
		String password = null;
		String nickName = null;
		String gender = null;
		String vcode = null;
		if (regist_bodyFlipper.getDisplayedChild() == 0) {// 手机登陆
			if (!checkPhoneFormat()) {
				return;
			}
			mobile = phoneNumView.getText().toString();
			vcode = authCodeView.getText().toString();
			password = psdView.getText().toString();
		} else if (regist_bodyFlipper.getDisplayedChild() == 1) {// email
			if (!checkEmailFormat()) {
				return;
			}
			email = emailView.getText().toString();
			password = psdViewEmail.getText().toString();
		}
		new RequestRegisterTask(this, email, mobile, password, nickName, gender, vcode).start();
	}

	private class RequestRegisterTask extends LetvHttpAsyncTask<RegisterResult> {

		private String email;

		private String mobile;

		private String password;

		private String nickName;

		private String gender;

		private String vcode;

		public RequestRegisterTask(Context context, String email, String mobile, String password,
				String nickName, String gender, String vcode) {
			super(context);

			this.email = email;
			this.mobile = mobile;
			this.password = password;
			this.nickName = nickName;
			this.gender = gender;
			this.vcode = vcode;
		}

		@Override
		public LetvDataHull<RegisterResult> doInBackground() {
			return LetvHttpApi.register(0, email, mobile, password, nickName, gender, "mapp",
					vcode, new RegisterResultParser());
		}

		@Override
		public void onPostExecute(int updateId, RegisterResult result) {
			PreferencesManager.getInstance().setUserId(result.getSsouid());
			if (TextUtils.isEmpty(email)) {
				new RequestLoginTask(RegisterActivity.this, mobile, password).start();
			} else {
				UIs.call(RegisterActivity.this, getMessage(),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								finish();
							}
						}, false);
			}
		}

		@Override
		public void dataNull(int updateId, String errMsg) {
			// if(errMsg == R.string.err_403){
			// errMsg = R.string.err_1000 ;
			// }
			UIs.call(RegisterActivity.this, errMsg, null);
//			UIs.call(RegisterActiv	·ity.this, "320 dataNull", null);
		}

		@Override
		public void netErr(int updateId, String errMsg) {
			UIs.notifyShortNormal(context, R.string.net_no);
		}
	}

	private class RequestLoginTask extends LetvHttpAsyncTask<User> {

		private String username;

		private String password;

		public RequestLoginTask(Context context, String username, String password) {
			super(context);
			this.username = username;
			this.password = password;
		}

		@Override
		public LetvDataHull<User> doInBackground() {
			return LetvHttpApi.login(0, username, password, "mapp", "1", new UserParser());
		}

		@Override
		public void onPostExecute(int updateId, User result) {
			PreferencesManager.getInstance().setLoginName(username);
			PreferencesManager.getInstance().setLoginPassword(password);
			PreferencesManager.getInstance().setUserName(result.getUsername());
			PreferencesManager.getInstance().setUserId(result.getUid());
			PreferencesManager.getInstance().setSso_tk(result.getTv_token());
			PreferencesManager.getInstance().setRemember_pwd(true);
			
			setResult(LoginMainActivity.LOGIN_SUCCESS);
			finish();
		}

		@Override
		public void dataNull(int updateId, String errMsg) {
//			UIs.call(RegisterActivity.this, errMsg, null);
			//邮箱注册bug
			UIs.call(RegisterActivity.this, "RegisterActivity 362h dataNull", null);
		}
	}

	private TimeTimer mTimeTimer;

	private void startTimeTimer() {
		if (getAuthCodeBtn.isEnabled()) {
			getAuthCodeBtn.setEnabled(false);
			closeTimeTimer();
			mTimeTimer = new TimeTimer(phoneNumView.getText().toString());
			mTimeTimer.execute();
		}
	}

	private void closeTimeTimer() {
		if (null != mTimeTimer) {
			if (!mTimeTimer.isCancelled()) {
				mTimeTimer.cancel();
			}
			mTimeTimer = null;
		}
	}

	/**
	 * 15秒倒计时,获取验证码Task
	 * 
	 */
	class TimeTimer extends LetvAsyncTask<Integer, Integer> {

		private int times = 15;
		private String mobile;

		public TimeTimer(String mobile) {
			this.mobile = mobile;
		}

		@Override
		public void onPreExecute() {
			super.onPreExecute();
			getAuthCodeBtn.setEnabled(false);
		}

		@Override
		public Integer doInBackground() {
			// String mobile = params[0];
			int code = -1;
			LetvDataHull<S_SendMobileResult> dataHull = LetvHttpApi.s_sendMobile(0, mobile,
					new S_SendMobileResultParser());
			if (dataHull.getDataType() == LetvDataHull.DataType.DATA_IS_INTEGRITY) {
				code = dataHull.getDataEntity().getCode();
			}

			while (times != 0) {
				publishProgress(times);

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				times--;
			}
			return code;
		}

		@Override
		public void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			getAuthCodeBtn.setText(String.valueOf(values[0])+ getString(R.string.registeractivity_second));
		}

		@Override
		public void onPostExecute(Integer result) {
			super.onPostExecute(result);
			getAuthCodeBtn.setText(getString(R.string.get_veriycode));
			getAuthCodeBtn.setEnabled(true);

			if (result != 200) {
				UIs.callDialogMsgPositiveButton(RegisterActivity.this,
						DialogMsgConstantId.FOURTEEN_ZERO_FIVE_CONSTANT,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								phoneNumView.requestFocus();
							}
						});
			}
		}
	}

	/**
	 * 检查手机注册各项内容是否填写正确
	 */
	private boolean checkPhoneFormat() {

		if (TextUtils.isEmpty(phoneNumView.getText().toString())) {
			// 手机号码不能为空
			UIs.callDialogMsgPositiveButton(this, DialogMsgConstantId.FOURTEEN_ZERO_ONE_CONSTANT,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							phoneNumView.requestFocus();
						}
					});
			return false;
		}

		if (!LetvUtil.isMobileNO(phoneNumView.getText().toString())) {
			// 手机号码格式不正确
			UIs.callDialogMsgPositiveButton(this, DialogMsgConstantId.FOURTEEN_ZERO_THREE_CONSTANT,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							phoneNumView.requestFocus();
						}
					});
			return false;
		}

		if (TextUtils.isEmpty(authCodeView.getText().toString().trim())) {
			// 验证码不能为空
			UIs.callDialogMsgPositiveButton(this, DialogMsgConstantId.FOURTEEN_ZERO_FOUR_CONSTANT,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							authCodeView.requestFocus();
						}
					});
			return false;
		}

		if (TextUtils.isEmpty(psdView.getText().toString())) {
			// 密码不能为空
			UIs.callDialogMsgPositiveButton(this, DialogMsgConstantId.FIFTEEN_ELEVEN_CONSTANT,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							psdView.requestFocus();
						}
					});
			return false;
		}

		if (!LetvUtil.passwordFormat(psdView.getText().toString())) {
			// 密码格式不正确
			UIs.callDialogMsgPositiveButton(this, DialogMsgConstantId.FOURTEEN_ZERO_SEVEN_CONSTANT,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							psdView.setText("");
							psdView.requestFocus();
						}
					});
			return false;
		}

		if (TextUtils.isEmpty(psdViewConfirm.getText().toString())) {
			UIs.call(this, R.string.registeractivity_repasswordnull,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							psdViewConfirm.requestFocus();
						}
					});
			return false;
		}

		if (!LetvUtil.passwordFormat(psdViewConfirm.getText().toString())) {
			UIs.call(this, R.string.registeractivity_repassworderr,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							psdViewConfirm.setText("");
							psdViewConfirm.requestFocus();
						}
					});
			return false;
		}

		if (!psdView.getText().toString().equals(psdViewConfirm.getText().toString())) {
			// 密码与确认密码不一致
			UIs.callDialogMsgPositiveButton(this, DialogMsgConstantId.FOURTEEN_ZERO_EIGHT_CONSTANT,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							psdViewConfirm.setText("");
							psdViewConfirm.requestFocus();
						}
					});
			return false;
		}

		return true;
	}

	/**
	 * 检查邮箱注册各项内容是否填写正确
	 */
	private boolean checkEmailFormat() {

		if (TextUtils.isEmpty(emailView.getText().toString())) {
			// 邮箱不能为空
			UIs.callDialogMsgPositiveButton(this, DialogMsgConstantId.SIXTEEN_ZERO_ONE_CONSTANT,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							emailView.requestFocus();
						}
					});
			return false;
		}

		if (!LetvUtil.emailFormats(emailView.getText().toString())) {
			// 邮箱格式不正确
			UIs.callDialogMsgPositiveButton(this, DialogMsgConstantId.SIXTEEN_ZERO_TWO_CONSTANT,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							emailView.requestFocus();
						}
					});
			return false;
		}

		if (TextUtils.isEmpty(psdViewEmail.getText().toString())) {
			// 密码不能为空
			UIs.callDialogMsgPositiveButton(this, DialogMsgConstantId.FIFTEEN_ELEVEN_CONSTANT,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							psdViewEmail.requestFocus();
						}
					});
			return false;
		}

		if (!LetvUtil.passwordFormat(psdViewEmail.getText().toString())) {
			// 密码格式不正确
			UIs.callDialogMsgPositiveButton(this, DialogMsgConstantId.FOURTEEN_ZERO_SEVEN_CONSTANT,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							psdViewEmail.setText("");
							psdViewEmail.requestFocus();
						}
					});
			return false;
		}

		if (TextUtils.isEmpty(psdViewConfirmEmail.getText().toString())) {
			UIs.call(this, R.string.registeractivity_repasswordnull,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							psdViewConfirmEmail.requestFocus();
						}
					});
			return false;
		}

		if (!LetvUtil.passwordFormat(psdViewConfirmEmail.getText().toString())) {
			// 确认密码格式不正确
			UIs.call(this, R.string.registeractivity_repassworderr,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							psdViewConfirmEmail.setText("");
							psdViewConfirmEmail.requestFocus();
						}
					});
			return false;
		}

		if (!psdViewEmail.getText().toString().equals(psdViewConfirmEmail.getText().toString())) {
			// 密码与确认密码不一致
			UIs.callDialogMsgPositiveButton(this, DialogMsgConstantId.FOURTEEN_ZERO_EIGHT_CONSTANT,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							psdViewConfirmEmail.setText("");
							psdViewConfirmEmail.requestFocus();
						}
					});
			return false;
		}

		return true;
	}

}
