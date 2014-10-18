package com.tencent.weibo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.tencent.weibo.TWeiboNew.TWeiboListener;
import com.tencent.weibo.oauthv2.OAuthV2;
import com.tencent.weibo.oauthv2.OAuthV2Client;

public class LoginNewActivity extends Activity {

	private OAuthV2 oAuth;
	static TWeiboListener listener;

	public static void launch(Context context, TWeiboListener listener) {
		LoginNewActivity.listener = listener;
		Intent intent = new Intent(context, LoginNewActivity.class);
		context.startActivity(intent);
	}

	public final static int RESULT_CODE = 2;
	private static final String TAG = LoginNewActivity.class.getName();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LinearLayout linearLayout = new LinearLayout(this);
		WebView webView = new WebView(this);
		linearLayout.addView(webView, new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		setContentView(linearLayout);

		oAuth = TWeiboNew.getInstance().getAuth();
		String urlStr = OAuthV2Client.generateImplicitGrantUrl(oAuth);

		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setSupportZoom(true);
		webView.requestFocus();
		if(urlStr.contains("https")){
			urlStr = urlStr.replaceFirst("https", "http");
		}
		webView.loadUrl(urlStr);
		Log.i(TAG, "WebView Starting....");
		WebViewClient client = new WebViewClient() {
			/**
			 * 回调方法，当页面开始加载时执行
			 */
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				Log.i(TAG, "WebView onPageStarted...");
				Log.i(TAG, "URL = " + url);
				if (url.indexOf("access_token=") != -1) {
					int start = url.indexOf("access_token=");
					String responseData = url.substring(start);
					OAuthV2Client
							.parseAccessTokenAndOpenId(responseData, oAuth);
					if (oAuth.getStatus() == 0) {
						// 登录成功
						TWeiboNew.getInstance().saveUserInfo(
								getApplicationContext(), oAuth);
//						Toast.makeText(getApplicationContext(), "登陆成功",
//								Toast.LENGTH_SHORT).show();
						listener.onComplete();
					} else {
//						Toast.makeText(getApplicationContext(), "登陆失败",
//								Toast.LENGTH_SHORT).show();
						listener.onFail("登陆失败");
					}

					view.destroyDrawingCache();
					view.destroy();
					finish();
				}
				super.onPageStarted(view, url, favicon);
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
		};
		webView.setWebViewClient(client);
	}
}
