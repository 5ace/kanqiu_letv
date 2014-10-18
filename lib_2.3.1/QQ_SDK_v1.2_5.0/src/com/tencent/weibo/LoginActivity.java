package com.tencent.weibo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.tencent.weibo.TWeibo.TWeiboListener;
import com.tencent.weibo.constants.OAuthConstants;
import com.tencent.weibo.oauthv1.OAuthV1;
import com.tencent.weibo.oauthv2.OAuthV2Client;

public class LoginActivity extends Activity {

	private OAuthV1 oAuth;
	
	private static CallbackListener listener;

	private ProgressDialog mSpinner;
	private WebView mWebView;
	private RelativeLayout mContent;
	private RelativeLayout webViewContainer;
	private RelativeLayout top ;
	
	public interface CallbackListener{
		
		public void callBack(String msg);
	}
	
	public static void launch(Context context, CallbackListener listener) {
		LoginActivity.listener = listener;
		Intent intent = new Intent(context, LoginActivity.class);
		context.startActivity(intent);
	}

	public final static int RESULT_CODE = 2;
	private static final String TAG = LoginActivity.class.getName();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		mSpinner = new ProgressDialog(this);
		mSpinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mSpinner.setMessage(getResources().getString(R.string.loading));
		mSpinner.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				onBack();
				return false;
			}

		});
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mContent = new RelativeLayout(this);
		top = (RelativeLayout) getLayoutInflater().inflate(R.layout.top_back, null, false);
		setUpWebView();
		mContent.setBackgroundResource(R.drawable.content_bg);
		addContentView(mContent, new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		
		super.onCreate(savedInstanceState);
	}
	
	private void setUpWebView(){
		webViewContainer = new RelativeLayout(this);
		
		mWebView = new WebView(this);
		webViewContainer.addView(mWebView, new RelativeLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

		oAuth = TWeibo.getInstance().getAuth();
		String urlStr = OAuthConstants.OAUTH_V1_AUTHORIZE_URL+"?oauth_token="+oAuth.getOauthToken();

		WebSettings webSettings = mWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setSupportZoom(true);
		mWebView.requestFocus();
		if(urlStr.contains("https")){
			urlStr = urlStr.replaceFirst("https", "http");
		}
		mWebView.loadUrl(urlStr);
		mWebView.setWebViewClient(client);
		mWebView.setVisibility(View.INVISIBLE);
		
		mContent.addView(webViewContainer ,new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		mContent.addView(top);
	}
	
	
	protected void onBack() {
		try {
			mSpinner.dismiss();
			if (null != mWebView) {
				mWebView.stopLoading();
				mWebView.destroy();
			}
		} catch (Exception e) {
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mWebView != null) {
				try{
					mWebView.stopLoading();
				}catch (Exception e) {
				}
			}
			try{
				if (mSpinner != null && mSpinner.isShowing()) {
					mSpinner.dismiss();
				}
			}catch (Exception e) {
			}

			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private WebViewClient client = new WebViewClient() {
		/**
		 * 回调方法，当页面开始加载时执行
		 */
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			Log.i(TAG, "WebView onPageStarted...");
			Log.i(TAG, "URL = " + url);
			
		 if (url.indexOf("checkType=verifycode") != -1) {
                int start=url.indexOf("checkType=verifycode&v=")+23;
                String verifyCode=url.substring(start, start+6);
//                oAuth.setOauthVerifier(verifyCode);
//                TWeibo.getInstance().saveUserInfo(getApplicationContext(), oAuth);
                listener.callBack(verifyCode);
                view.destroyDrawingCache();
                view.destroy();
                finish();
            }
			super.onPageStarted(view, url, favicon);
			try{
				if(mSpinner != null && !mSpinner.isShowing()){
					if(!LoginActivity.this.isFinishing()){
						mSpinner.show();
					}
				}
			}catch (Exception e) {
			}
		}

		/*
		 * TODO Android2.2及以上版本才能使用该方法
		 * 目前https://open.t.qq.com中存在http资源会引起sslerror，待网站修正后可去掉该方法
		 */
		public void onReceivedSslError(WebView view,
				SslErrorHandler handler, SslError error) {
			if ((null != view.getUrl())
					&& (view.getUrl().startsWith("https://open.t.qq.com"))) {
				handler.proceed();// 接受证书
			} else {
				handler.cancel(); // 默认的处理方式，WebView变成空白页
			}
			// handleMessage(Message msg); 其他处理
		}
		
		public void onPageFinished(WebView view, String url) {
				Log.d(TAG, "onPageFinished URL: " + url);
				super.onPageFinished(view, url);
				try{
					if (mSpinner != null && mSpinner.isShowing()) {
						if(!LoginActivity.this.isFinishing()){
							mSpinner.dismiss();
						}
					}
				}catch (Exception e) {
				}

				mContent.setBackgroundColor(Color.TRANSPARENT);
				//webViewContainer.setBackgroundResource(R.drawable.weibosdk_dialog_bg);
				// mBtnClose.setVisibility(View.VISIBLE);
				mWebView.setVisibility(View.VISIBLE);
			
		};
	};
}
