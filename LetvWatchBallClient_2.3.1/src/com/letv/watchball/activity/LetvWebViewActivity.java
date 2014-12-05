package com.letv.watchball.activity;

import java.lang.reflect.Method;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.letv.watchball.R;

public class LetvWebViewActivity extends LetvBaseActivity implements
		OnClickListener {

	private WebView mWebView;

	private String baseUrl;

	private ImageView back;

	private ImageView forward;

	private ImageView refresh;

	private ProgressBar progressBar;

	private boolean isFinish;

	private TextView titleView;
	private TextView urlTitleView;

	private String loadType;

	// public static void launch(Context context , String url,int title){
	// Intent intent = new Intent(context , LetvWebViewActivity.class);
	// intent.putExtra("url", url);
	// intent.putExtra("loadType", title);
	// context.startActivity(intent);
	// }

	public static void launch(Context context, String url, String title) {
		Intent intent = new Intent(context, LetvWebViewActivity.class);
		intent.putExtra("url", url);
		intent.putExtra("loadType", title);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.letv_webview);
		getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
		loadType = getIntent().getStringExtra("loadType");
		baseUrl = getIntent().getStringExtra("url");
		// Log.e("gongmeng", "URL:" + baseUrl.toString());
		findView();
	}

	private void findView() {
		back = (ImageView) findViewById(R.id.back);
		forward = (ImageView) findViewById(R.id.forward);
		refresh = (ImageView) findViewById(R.id.refresh);
		progressBar = (ProgressBar) findViewById(R.id.loading_progress);
		titleView = (TextView) findViewById(R.id.letv_webview_title);
		urlTitleView = (TextView) findViewById(R.id.webview_title_url);

		ImageView back_iv = (ImageView) findViewById(R.id.back_iv);
		back_iv.setOnClickListener(this);
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
		if (baseUrl != null)
			urlTitleView.setText(baseUrl);
		this.back.setOnClickListener(this);
		forward.setOnClickListener(this);
		refresh.setOnClickListener(this);
		titleView.setText(loadType);
	}

	// private class LetvWebViewClient extends WebViewClient{

	private class LetvWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			try {
				int index = url.indexOf("?");
				if (index > 0) {
					String u = url.substring(0, url.indexOf("?"));
					if (".mp4".equals(u.substring(u.lastIndexOf("."),
							u.length()))
							&& url.contains("vtype=mp4")) {
						view.stopLoading();
						if (!isFinish) {
							Intent intent = new Intent(Intent.ACTION_VIEW);
							String type = "video/mp4";
							Uri name = Uri.parse(url);
							intent.setDataAndType(name, type);
							intent.putExtra(
									MediaStore.EXTRA_SCREEN_ORIENTATION,
									ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
							LetvWebViewActivity.this.startActivity(intent);
						}
						return true;
					} else {
						view.loadUrl(url);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return super.shouldOverrideUrlLoading(view, url);
		}
	}

	private class LetvWebViewChromeClient extends WebChromeClient {

		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			super.onProgressChanged(view, newProgress);
			if (progressBar.getVisibility() != View.VISIBLE) {
				progressBar.setVisibility(View.VISIBLE);
			}
			progressBar.setProgress(newProgress);
			if (newProgress == 100) {
				progressBar.setVisibility(View.GONE);
				urlTitleView.setVisibility(View.GONE);
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_iv:
			finish();
			break;
		case R.id.back:
			if (mWebView.canGoBack()) {
				mWebView.goBack();
			}
			break;
		case R.id.forward:
			if (mWebView.canGoForward()) {
				mWebView.goForward();
			}
			break;
		case R.id.refresh:
			mWebView.reload();
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			getWindow().clearFlags(
					WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
			isFinish = true;
			if (mWebView != null) {
				mWebView.stopLoading();
				/**
				 * 3.0以上系统编译，如果不隐藏webview会出现崩溃
				 */
				mWebView.setVisibility(View.GONE);
				mWebView.destroy();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		callHiddenWebViewMethod("onPause");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		callHiddenWebViewMethod("onResume");
	}

	private void callHiddenWebViewMethod(String name) {
		if (mWebView != null) {
			try {
				Method method = WebView.class.getMethod(name);
				method.invoke(mWebView);
			} catch (Exception e) {
			}
		}
	}
}
