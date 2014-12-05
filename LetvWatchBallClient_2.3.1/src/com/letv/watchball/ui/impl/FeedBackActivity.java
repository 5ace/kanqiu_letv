package com.letv.watchball.ui.impl;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.letv.watchball.R;
import com.letv.watchball.activity.LetvBaseActivity;
import com.letv.watchball.db.PreferencesManager;
import com.letv.watchball.utils.UIs;
import com.letv.watchball.view.PublicLoadLayout;

public class FeedBackActivity extends LetvBaseActivity implements
		OnClickListener {
	private PublicLoadLayout root;
	// /**
	// * 提交按钮
	// */
	// private Button feedback_submit,feedback_submit_unenable;
	// /**
	// * 性别
	// */
	// private LetvSpinner spinner_sex;
	// /**
	// * 年龄
	// */
	// private LetvSpinner spinner_age;
	// /**
	// * 用户意见
	// */
	// private EditText user_content;
	// private int sexPos = 0;
	// private int agePos = 0;
	//
	// /**
	// * 反馈内容
	// * */
	// private String feedback;
	//
	// private String[] feedback_sex;
	// private String[] feedback_age;

	private WebView feedback_webview;

	private ImageView feedback_loading;

	AnimationDrawable anim;

	public static void launch(Context context) {

		Intent intent = new Intent(context, FeedBackActivity.class);
		context.startActivity(intent);

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		root = UIs.createPage(this, R.layout.setting_center_feedback_layout);
		setContentView(root);
		// getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		findView();
	}

	/**
	 * 初始化控件
	 * */
	private void findView() {
		feedback_webview = (WebView) root.findViewById(R.id.faceback_webview);
		feedback_loading = (ImageView) root.findViewById(R.id.faceback_loading);

		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(root.getWindowToken(), 0); // 强制隐藏键盘

		anim = (AnimationDrawable) feedback_loading.getBackground();
		anim.setOneShot(false);
		anim.start();
		// 判断用户是否登录
		/**
		 * 已登录则，则带上登录token,及跳转地址http://q.letv.com 没有登录则直接跳转到http://q.letv.com
		 * */
		StringBuffer urlSb = new StringBuffer(
				"http://sso.letv.com/user/setUserStatus");
		String token = PreferencesManager.getInstance().getSso_tk();
		if (!token.equals("")) {
			urlSb.append("?tk=").append(token).append("&from=")
					.append("sports").append("&next_action=")
					.append("http://q.letv.com/m/letv_app-kanqiu_android");
			feedback_webview.loadUrl(urlSb.toString());
		} else {
			feedback_webview
					.loadUrl("http://q.letv.com/m/letv_app-kanqiu_android");
		}
		feedback_webview.getSettings().setJavaScriptEnabled(true);
		feedback_webview.getSettings().setDomStorageEnabled(true);
		feedback_webview.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return false;
			}

			@Override
			public void onReceivedSslError(WebView view,
					SslErrorHandler handler, SslError error) {
				super.onReceivedSslError(view, handler, error);
				handler.proceed();
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				anim.stop();
				feedback_loading.setVisibility(View.GONE);
				super.onPageFinished(view, url);
			}
		});

		// new
		// RequestFeedBackLoginInfo(FeedBackActivity.this,urlSb.toString()).start();
		// createHead();
		// feedback_sex =
		// this.getResources().getStringArray(R.array.feedback_sex);
		// feedback_age =
		// this.getResources().getStringArray(R.array.feedback_age);
		// feedback_submit = (Button) findViewById(R.id.feedback_submit);
		// feedback_submit_unenable = (Button)
		// findViewById(R.id.feedback_submit_unenable);
		// user_content = (EditText) findViewById(R.id.user_content);
		// spinner_sex = (LetvSpinner) findViewById(R.id.spinner_sex);
		// spinner_age = (LetvSpinner) findViewById(R.id.spinner_age);
		//
		// spinner_age.init(feedback_age,"年龄");
		// spinner_sex.init(feedback_sex,"性别");
		// spinner_sex.setListener(new
		// LetvSpinner.OnLetvSpinnerSelectedListener(){
		// @Override
		// public void onSelected(int pos) {
		// sexPos = pos+1;
		// }
		// });
		// spinner_age.setListener(new
		// LetvSpinner.OnLetvSpinnerSelectedListener(){
		// @Override
		// public void onSelected(int pos) {
		// agePos = pos+1;
		// }
		// });
		// feedback_submit.setOnClickListener(this);
		// setSubmitUnEnable();
		// user_content.addTextChangedListener(new TextWatcher() {
		//
		// @Override
		// public void onTextChanged(CharSequence s, int start, int before, int
		// count) {
		//
		// }
		//
		// @Override
		// public void beforeTextChanged(CharSequence s, int start, int count,
		// int after) {
		// }
		//
		// @Override
		// public void afterTextChanged(Editable s) {
		// String scontent = s.toString();
		// if(scontent.length()>0){
		// setSubmitEnable();
		// }else {
		// setSubmitUnEnable();
		// }
		// }
		// });
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	// /**
	// * 设置提交按钮为灰，不可点击
	// */
	// private void setSubmitUnEnable() {
	// feedback_submit.setVisibility(View.GONE);
	// feedback_submit_unenable.setVisibility(View.VISIBLE);
	// }
	// /**
	// * 设置提交按钮可点击
	// */
	// private void setSubmitEnable() {
	// feedback_submit.setVisibility(View.VISIBLE);
	// feedback_submit_unenable.setVisibility(View.GONE);
	// }
	// /**
	// * 初始化title
	// * */
	// private void createHead() {
	// ImageView back_iv = (ImageView) findViewById(R.id.back_iv);
	//
	// back_iv.setOnClickListener(this);
	// }

	// @Override
	// public void onClick(View v) {
	// switch (v.getId()) {
	// case R.id.back_iv:
	// finish();
	// break;
	// case R.id.feedback_submit:
	// submitFeedback();
	// break;
	// }
	// }

	// private void submitFeedback() {
	// feedback = user_content.getText().toString().trim();
	// if(TextUtils.isEmpty(feedback)){
	// UIs.showToast(R.string.more_setting_feedback_textnull);
	// return ;
	// }else if(sexPos<=0){
	// UIs.showToast(R.string.more_setting_feedback_sexnull);
	// return ;
	// }else if(agePos<=0){
	// UIs.showToast(R.string.more_setting_feedback_agenull);
	// return ;
	// }else {
	// sendFeedback();
	// }
	// }
	// /**
	// * 发送反馈意见
	// * */
	// private void sendFeedback() {
	// new RequestFeedBackInfo(this).start();
	// }

	// private class RequestFeedBackInfo extends LetvHttpAsyncTask<State> {
	//
	// public RequestFeedBackInfo(Activity activity) {
	// super(activity);
	// if (root != null) {
	// root.loading(true);
	// }
	// }
	//
	// @Override
	// public LetvDataHull<com.letv.watchball.bean.State> doInBackground() {
	//
	// String devid = LetvUtil.getDeviceId(FeedBackActivity.this);
	// String name = LetvUtil.getDeviceName();
	// String sysname = LetvUtil.getSystemName();
	// String sysver = LetvUtil.getOSVersionName();
	// String model = LetvUtil.getUserAgent();
	// String lmodel = "";
	//
	// LetvDataHull<com.letv.watchball.bean.State> dataHull = LetvHttpApi
	// .requestFeedBack(0, devid, name, sysname, sysver, model, lmodel,
	// feedback, (sexPos - 1) + "", (agePos - 1) + "", new FeedBackParse());
	//
	// return dataHull;
	// }
	//
	// @Override
	// public void onPostExecute(int updateId, com.letv.watchball.bean.State
	// result) {
	// if (result.isSucceed()) {
	// user_content.setText("");
	// feedback = "";
	// UIs.showToast(R.string.more_feedback_submit_succeed);
	// finish();
	// } else {
	// UIs.showToast(R.string.more_feedback_submit_fail);
	//
	// }
	// if (root != null) {
	// root.finish();
	// }
	// }
	//
	// @Override
	// public void netNull() {
	// // if (root != null) {
	// // root.error(false);
	// // }
	// if (root != null) {
	// root.finish();
	// }
	// UIs.showToast(R.string.net_no);
	// }
	//
	// @Override
	// public void netErr(int updateId, String errMsg) {
	// // if (root != null) {
	// // root.error(false);
	// // }
	// if (root != null) {
	// root.finish();
	// }
	// UIs.showToast(R.string.net_err);
	// }
	//
	// @Override
	// public void dataNull(int updateId, String errMsg) {
	// // if (root != null) {
	// // root.error(false);
	// // }
	// if (root != null) {
	// root.finish();
	// }
	// UIs.showToast(R.string.more_feedback_submit_fail);
	// }
	//
	// }
}
