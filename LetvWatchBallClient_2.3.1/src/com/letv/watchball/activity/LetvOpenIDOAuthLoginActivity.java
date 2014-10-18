package com.letv.watchball.activity;

import java.net.URL;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.letv.http.bean.LetvDataHull;
import com.letv.watchball.R;
import com.letv.watchball.async.LetvHttpAsyncTask;
import com.letv.watchball.bean.User;
import com.letv.watchball.db.PreferencesManager;
import com.letv.watchball.http.api.LetvHttpApi;
import com.letv.watchball.parser.OpenUserParser;

public class LetvOpenIDOAuthLoginActivity extends LetvBaseActivity implements View.OnClickListener{

	private WebView mWebView ;
	
	private String baseUrl ;
	
	private String title ;
	
	private ImageView back ;
	
	private ImageView forward ;
	
	private ImageView refresh ;
	
	private TextView address ;
	
	private ProgressBar progressBar ;
	
	private int src;
	
	
	public static void launch(Activity context , String url , String title, int src){
 		Intent intent = new Intent(context , LetvOpenIDOAuthLoginActivity.class);
 		intent.putExtra("url", url);
 		intent.putExtra("title", title);
 		intent.putExtra("src", src);
 		context.startActivityForResult(intent, LoginMainActivity.LOGIN);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.letv_webview);
		
		baseUrl = getIntent().getStringExtra("url");
		title = getIntent().getStringExtra("title");
		src = getIntent().getIntExtra("src", 0);
		
		findView();
	}
	
	private void findView(){
		back = (ImageView) findViewById(R.id.back);
		forward = (ImageView) findViewById(R.id.forward);
		refresh = (ImageView) findViewById(R.id.refresh);
		address = (TextView) findViewById(R.id.letv_webview_title);
		progressBar = (ProgressBar) findViewById(R.id.loading_progress);
		findViewById(R.id.back_iv).setOnClickListener(this);
		address.setText(title);
		
		mWebView = (WebView) findViewById(R.id.webView);
		mWebView.getSettings().setUseWideViewPort(true);
		mWebView.getSettings().setSupportZoom(true);
		mWebView.getSettings().setBuiltInZoomControls(true);
		mWebView.setVerticalScrollBarEnabled(true);
		mWebView.setHorizontalScrollBarEnabled(true);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.setWebViewClient(new LetvWebViewClient());
		mWebView.setWebChromeClient(new LetvWebViewChromeClient());
		mWebView.loadUrl(baseUrl);
		
		this.back.setOnClickListener(this);
		forward.setOnClickListener(this);
		refresh.setOnClickListener(this);
	}
	
	private class LetvWebViewClient extends WebViewClient{
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return super.shouldOverrideUrlLoading(view, url);
		}
		
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			try{
				URL mUrl = new URL(url);
				String path = mUrl.getPath();
				if(path.contains("callbackdata")){
					view.stopLoading();
					new RequestLoginTask(LetvOpenIDOAuthLoginActivity.this, url).start();
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onPageFinished(WebView view, String url) {
		}
		
		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
		}
		
		@Override
		public void onTooManyRedirects(WebView view, Message cancelMsg, Message continueMsg) {
			super.onTooManyRedirects(view, cancelMsg, continueMsg);
		}
	}
	
	private class LetvWebViewChromeClient extends WebChromeClient{
		
		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			super.onProgressChanged(view, newProgress);
			if(progressBar.getVisibility() != View.VISIBLE){
				progressBar.setVisibility(View.VISIBLE);
			}
			progressBar.setProgress(newProgress);
			if(newProgress == 100){
				progressBar.setVisibility(View.GONE);
			}
		}
	}
	
	

//	@Override
//	protected void updateUI(int updateId, Object... obj) {
//		
//	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			if(mWebView.canGoBack()){
				mWebView.goBack();
			}
			break;
		case R.id.forward:
			if(mWebView.canGoForward()){
				mWebView.goForward();
			}
			break;
		case R.id.refresh:
			mWebView.reload();
			break;
		case R.id.back_iv:
			finish();
			break;
		}
//		super.onClick(v);
	}
	
	@Override
	protected void onDestroy() {
		mWebView.stopLoading();
		super.onDestroy();
	}
	
	private class RequestLoginTask extends LetvHttpAsyncTask<User>{

		private String url ;
		
		public RequestLoginTask(Context context,String url) {
			super(context);
			this.url = url ;
		}

		@Override
		public LetvDataHull<User> doInBackground() {
			LetvDataHull<User> dataHull = LetvHttpApi.openIDOAuthLogin(0, url, new OpenUserParser());
			
			return dataHull ;
		}

		@Override
		public void onPostExecute(int updateId, User result) {
			PreferencesManager.getInstance().setUserName(result.getUsername());
			PreferencesManager.getInstance().setUserId(result.getUid());
			PreferencesManager.getInstance().setSso_tk(result.getTv_token());
			PreferencesManager.getInstance().setRemember_pwd(true);
//			UIs.notifyShort(context, R.string.toast_login_ok);  
//			if (LoginMainActivity.FROM_MORE.equalsIgnoreCase(from)) {
//				LoginMainActivity.launchBySrc(LetvOpenIDOAuthLoginActivity.this, src);
//			}
			setResult(LoginMainActivity.LOGIN_SUCCESS);
			finish();
//			LoginActivity.closeMe();
		}
		
		@Override
		public void netErr(int updateId, String errMsg) {
		}
		
		@Override
		public void netNull() {
		}
		
		@Override
		public void dataNull(int updateId, String errMsg) {
		}
	}
}
