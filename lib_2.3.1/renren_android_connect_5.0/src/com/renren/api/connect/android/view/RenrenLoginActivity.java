package com.renren.api.connect.android.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.renren.api.connect.android.R;
import com.renren.api.connect.android.Renren;

public class RenrenLoginActivity extends Activity{
	private static final String LOG_TAG = "RenrenDialog";

	private static final int RENREN_BLUE = 0xFF005EAC;


	/**
	 * 验证过程被取消的错误码
	 */
	private static final String CODE_AUTH_CANCEL = "login_denied";

	protected static String mUrl;

	protected static RenrenDialogListener mListener;

	private ProgressDialog progress;

	protected WebView webView;

	private LinearLayout content;

	private TextView title;

	private static boolean showTitle = false;
	
	private String mPostData;
	private boolean isPost = false;
	
	public static void Lanuch(Activity activity ,String url,
			RenrenDialogListener listener){
		Intent intent = new Intent(activity , RenrenLoginActivity.class);
		activity.startActivity(intent);
		mUrl = url;
		mListener = listener;
		showTitle = false;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		progress = new ProgressDialog(this);
		progress.requestWindowFeature(Window.FEATURE_NO_TITLE);
		progress.setMessage(getResources().getString(R.string.loading));

		content = new LinearLayout(this);
		
		content.setOrientation(LinearLayout.VERTICAL);
		RelativeLayout top = (RelativeLayout) getLayoutInflater().inflate(R.layout.top_renren_back, null, false);
		content.addView(top);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		if (this.showTitle) {
			setUpTitle();
		}
		setUpWebView();
		content.setBackgroundResource(R.drawable.content_bg);
		addContentView(content, new LayoutParams(LayoutParams.FILL_PARENT , LayoutParams.FILL_PARENT));
	}
	
	private void setUpTitle() {
		Drawable icon = getResources().getDrawable(
				R.drawable.renren_sdk_android_title_logo);
		title = new TextView(this);
		title.setText("与人人连接");
		title.setTextColor(Color.WHITE);
		title.setGravity(Gravity.CENTER_VERTICAL);
		title.setTypeface(Typeface.DEFAULT_BOLD);
		title.setBackgroundColor(RENREN_BLUE);
		title.setBackgroundResource(R.drawable.renren_sdk_android_title_bg);
		title.setCompoundDrawablePadding(6);
		title.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
		content.addView(title);
	}
	private void setUpWebView() {
		webView = new WebView(this);
		webView.setVisibility(View.INVISIBLE);
		webView.setVerticalScrollBarEnabled(false);
		webView.setHorizontalScrollBarEnabled(false);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebViewClient(new RenrenWebViewClient());
		if (isPost) {
//			webView.postUrl(mUrl, EncodingUtils.getBytes(mPostData, "BASE64"));
		} else {
			if(mUrl.contains("https")){
				mUrl = mUrl.replaceFirst("https", "http");
			}
			webView.loadUrl(mUrl);
		}
		FrameLayout.LayoutParams fill = new FrameLayout.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT);
		webView.setLayoutParams(fill);
		content.addView(webView);
	}

	/**
	 * Reponse on the Back key click to close the authrization dialog and send
	 * back the Cancel message Added by Shaofeng Wang
	 * (shaofeng.wang@renren-inc.com)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (webView != null) {
				webView.stopLoading();
			}
			try{
				if (progress != null && progress.isShowing()) {
					progress.dismiss();
				}
			}catch (Exception e) {
			}

			finish();
			mListener.onPageBegin(Renren.DEFAULT_REDIRECT_URI + "?error="
					+ CODE_AUTH_CANCEL);
		}
		return super.onKeyDown(keyCode, event);
	}

	private class RenrenWebViewClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			Log.d(LOG_TAG, "Redirect URL: " + url);
			int b = mListener.onPageBegin(url);
			switch (b) {
			case RenrenDialogListener.ACTION_PROCCESSED:
				finish();
				return true;
			case RenrenDialogListener.ACTION_DIALOG_PROCCESS:
				return false;
			}
			RenrenLoginActivity.this.startActivity(
					new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
			return true;
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			Log.d(LOG_TAG, "Webview loading URL: " + url);
			boolean b = mListener.onPageStart(url);
			if (b) {
				view.stopLoading();
				finish();
				return;
			}
			super.onPageStarted(view, url, favicon);
			try{
				if(progress != null && !progress.isShowing()){
					if(!RenrenLoginActivity.this.isFinishing()){
						progress.show();
					}
				}
			}catch (Exception e) {
			}
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
			mListener.onReceivedError(errorCode, description, failingUrl);
			progress.hide();
			finish();
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			mListener.onPageFinished(url);
			if (showTitle) {
				String t = view.getTitle();
				if (t != null && t.length() > 0) {
					title.setText(t);
				}
			}
			webView.setVisibility(View.VISIBLE);
			progress.hide();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		try{
			if (progress != null && progress.isShowing()) {
				progress.dismiss();
			}
		}catch (Exception e) {
		}
	}
}
