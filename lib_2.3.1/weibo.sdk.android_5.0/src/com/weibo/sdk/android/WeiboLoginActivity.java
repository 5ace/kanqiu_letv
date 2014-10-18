package com.weibo.sdk.android;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.weibo.sdk.android.util.Utility;

public class WeiboLoginActivity extends Activity{

	private static final FrameLayout.LayoutParams FILL = new FrameLayout.LayoutParams(
			ViewGroup.LayoutParams.FILL_PARENT,
			ViewGroup.LayoutParams.FILL_PARENT);
	static final int MARGIN = 4;
	static final int PADDING = 2;

	private final Weibo mWeibo = Weibo.getInstance(Weibo.app_key , Weibo.redirecturl);
	private static String mUrl;
	private static WeiboAuthListener mListener;
	private ProgressDialog mSpinner;
	private ImageView mBtnClose;
	private WebView mWebView;
	private RelativeLayout webViewContainer;
	private RelativeLayout mContent;
	private RelativeLayout top ;

	private final static String TAG = "Weibo-WebView";
	
	public static void Luanch(Context context , String url , WeiboAuthListener listener){
		Intent intent = new Intent(context , WeiboLoginActivity.class);
		mUrl = url ;
		mListener = listener ;
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
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
//		mContent = (RelativeLayout) getLayoutInflater().inflate(R.layout.login, null, false);
		top = (RelativeLayout) getLayoutInflater().inflate(R.layout.top_sina_back, null, false);
		setUpWebView();
		// setUpCloseBtn();
		mContent.setBackgroundResource(R.drawable.content_bg);
		addContentView(mContent, new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		
		super.onCreate(savedInstanceState);
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
//		mSpinner.dismiss();
	}
	
	private void setUpWebView() {
		webViewContainer = new RelativeLayout(this);
		
		mWebView = new WebView(this);
		mWebView.setVerticalScrollBarEnabled(false);
		mWebView.setHorizontalScrollBarEnabled(false);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.setWebViewClient(new WeiboWebViewClient());
		mWebView.loadUrl(mUrl);
		mWebView.setLayoutParams(FILL);
		mWebView.setVisibility(View.INVISIBLE);

		webViewContainer.addView(mWebView);
        final float scale = WeiboLoginActivity.this.getResources().getDisplayMetrics().density;
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		lp.leftMargin = -10;
		lp.topMargin =(int) (40 * scale - 14);
		lp.rightMargin = -10;
		lp.bottomMargin = -10;
		mContent.addView(webViewContainer, lp);
		mContent.addView(top);
	}
	
	private void handleRedirectUrl(WebView view, String url) {
		Bundle values = Utility.parseUrl(url);

		String error = values.getString("error");
		String error_code = values.getString("error_code");

		if (error == null && error_code == null) {
			mListener.onComplete(values);
		} else if (error.equals("access_denied")) {
			// 用户或授权服务器拒绝授予数据访问权限
			mListener.onCancel();
		} else {
			mListener.onWeiboException(new WeiboException(error, Integer
					.parseInt(error_code)));
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
	
	private class WeiboWebViewClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			Log.d(TAG, "Redirect URL: " + url);
			 if (url.startsWith("sms:")) {  //针对webview里的短信注册流程，需要在此单独处理sms协议
	                Intent sendIntent = new Intent(Intent.ACTION_VIEW);  
	                sendIntent.putExtra("address", url.replace("sms:", ""));  
	                sendIntent.setType("vnd.android-dir/mms-sms");  
	                startActivity(sendIntent);  
	                return true;  
	            }  
			return super.shouldOverrideUrlLoading(view, url);
		}

		@Override
		public void onReceivedError(WebView view, int errorCode, String description,
				String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
			mListener.onError(new WeiboDialogError(description, errorCode, failingUrl));
			finish();
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			Log.d(TAG, "onPageStarted URL: " + url);
			if (url.startsWith(Weibo.redirecturl)) {
				handleRedirectUrl(view, url);
				view.stopLoading();
				finish();
				return;
			}
			super.onPageStarted(view, url, favicon);
			try{
				mSpinner.show();
			}catch(Exception e){
			}
			
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			Log.d(TAG, "onPageFinished URL: " + url);
			super.onPageFinished(view, url);
			try{
				if (mSpinner.isShowing()) {
					mSpinner.dismiss();
				}
			}catch(Exception e){
			}
			mWebView.setVisibility(View.VISIBLE);
		}

		public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
			handler.proceed();
		}

	}
}
